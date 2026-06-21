package com.ticket.util;

import com.ticket.model.TicketDetail;
import com.ticket.util.AppSettings;
import java.text.SimpleDateFormat;
import java.util.List;

public final class JsonUtil {
  private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  public static String escape(String value) {
    if (value == null) {
      return "";
    }
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }

  public static String formatTimestamp(java.sql.Timestamp timestamp) {
    if (timestamp == null) {
      return null;
    }
    synchronized (ISO_FORMAT) {
      return ISO_FORMAT.format(timestamp);
    }
  }

  public static String ticketToJson(TicketDetail ticket) {
    AppSettings.load();
    return "{"
        + "\"bookingCode\":\"" + escape(ticket.getBookingCode()) + "\","
        + "\"customerName\":\"" + escape(ticket.getCustomerName()) + "\","
        + "\"phoneNumber\":\"" + escape(ticket.getPhoneNumber()) + "\","
        + "\"tripName\":\"" + escape(ticket.getTripName()) + "\","
        + "\"startLocation\":\"" + escape(ticket.getStartLocation()) + "\","
        + "\"endLocation\":\"" + escape(ticket.getEndLocation()) + "\","
        + "\"departureTime\":" + timestampJson(ticket.getDepartureTime()) + ","
        + "\"totalSeats\":\"" + ticket.getTotalSeats() + "\","
        + "\"bookingDate\":" + timestampJson(ticket.getBookingDate()) + ","
        + "\"totalAmount\":" + String.format("%.2f", ticket.getTotalAmount()) + ","
        + "\"status\":\"" + escape(ticket.getStatus()) + "\","
        + "\"paymentMethod\":\"" + escape(ticket.getPaymentMethod()) + "\","
        + "\"companyName\":\"" + escape(AppSettings.getCompanyName()) + "\","
        + "\"companyHotline\":\"" + escape(AppSettings.getCompanyHotline()) + "\","
        + "\"ticketFooter\":\"" + escape(AppSettings.getTicketFooter()) + "\""
        + "}";
  }

  public static String ticketsToJson(List<TicketDetail> tickets) {
    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < tickets.size(); i++) {
      if (i > 0) {
        json.append(",");
      }
      json.append(ticketToJson(tickets.get(i)));
    }
    json.append("]");
    return json.toString();
  }

  private static String timestampJson(java.sql.Timestamp timestamp) {
    if (timestamp == null) {
      return "null";
    }
    return "\"" + escape(formatTimestamp(timestamp)) + "\"";
  }

}