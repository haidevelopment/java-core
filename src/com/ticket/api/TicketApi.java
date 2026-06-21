package com.ticket.api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.ticket.model.TicketDetail;
import com.ticket.repository.BookingRepository;
import com.ticket.repository.DatabaseConnection;
import com.ticket.util.AppSettings;
import com.ticket.util.QrCodeGenerator;
import com.ticket.util.JsonUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TicketApi {
  private final BookingRepository bookingRepo = new BookingRepository();

  private HttpServer server;

  public void start() {
    AppSettings.load();
    int port = AppSettings.getApiPort();

    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/api/health", new HealthHandler());
      server.createContext("/api/tickets/by-phone/", new TicketsByPhoneHandler());
      server.createContext("/api/tickets/", new TicketHandler());
      server.setExecutor(null);
      server.start();
      System.out.println(">>> API khach hang dang chay tai http://localhost:" + port);
    } catch (IOException e) {
      System.err.println("Khong the khoi dong API: " + e.getMessage());
    }
  }

  public void stop() {
    if (server != null) {
      server.stop(0);
    }
  }

  private void addCorsHeaders(HttpExchange exchange) {
    Headers headers = exchange.getResponseHeaders();
    headers.add("Access-Control-Allow-Origin", "*");
    headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
    headers.add("Access-Control-Allow-Headers", "Content-Type");
  }

  private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
    addCorsHeaders(exchange);
    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(statusCode, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
    sendJson(exchange, statusCode, "{\"error\":\"" + JsonUtil.escape(message) + "\"}");
  }

  private class HealthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(204, -1);
        return;
      }
      sendJson(exchange, 200, "{\"status\":\"ok\",\"service\":\"booking-pro-api\"}");
    }
  }

  private class TicketsByPhoneHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(204, -1);
        return;
      }

      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendError(exchange, 405, "Method not allowed");
        return;
      }

      String path = exchange.getRequestURI().getPath();
      String prefix = "/api/tickets/by-phone/";
      if (!path.startsWith(prefix)) {
        sendError(exchange, 404, "Not found");
        return;
      }

      String phone = path.substring(prefix.length()).trim();
      if (phone.isEmpty()) {
        sendError(exchange, 400, "Phone number is required");
        return;
      }

      List<TicketDetail> tickets = bookingRepo.findTicketsByPhone(phone);
      sendJson(exchange, 200, JsonUtil.ticketsToJson(tickets));
    }
  }

  private class TicketHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(204, -1);
        return;
      }

      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendError(exchange, 405, "Method not allowed");
        return;
      }

      String path = exchange.getRequestURI().getPath();
      String prefix = "/api/tickets/";
      if (!path.startsWith(prefix) || path.equals(prefix)) {
        sendError(exchange, 404, "Not found");
        return;
      }

      String remainder = path.substring(prefix.length());
      if (remainder.endsWith("/qr")) {
        String code = remainder.substring(0, remainder.length() - 3);
        handleQr(exchange, code);
        return;
      }

      TicketDetail ticket = bookingRepo.findTicketDetailByCode(remainder);
      if (ticket == null) {
        sendError(exchange, 404, "Ticket not found");
        return;
      }

      AppSettings.load();
      sendJson(exchange, 200, JsonUtil.ticketToJson(ticket));
    }

    private void handleQr(HttpExchange exchange, String bookingCode) throws IOException {
      TicketDetail ticket = bookingRepo.findTicketDetailByCode(bookingCode);
      if (ticket == null) {
        sendError(exchange, 404, "Ticket not found");
        return;
      }

      try {
        String qrContent = AppSettings.getWebBaseUrl() + "/ticket/" + ticket.getBookingCode();
        BufferedImage qr = QrCodeGenerator.generate(qrContent, 280);
        addCorsHeaders(exchange);
        exchange.getResponseHeaders().add("Content-Type", "image/png");
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
          ImageIO.write(qr, "png", os);
        }
      } catch (Exception e) {
        sendError(exchange, 500, "Cannot generate QR code");
      }

    }
  }

}
