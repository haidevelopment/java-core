package com.ticket.view;

import com.ticket.model.Booking;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Desktop;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class BookingDetailDialog extends JDialog {

    public BookingDetailDialog(Frame owner, Booking booking) {
        super(owner, "Ticket Details", true);
        setSize(480, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("TICKET DETAILS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblCode = new JLabel("#" + booking.getBookingCode());
        lblCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCode.setForeground(new Color(189, 195, 199));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblCode, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        contentPanel.add(createSection("BOOKING INFORMATION"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createRow("Booking Code", booking.getBookingCode()));
        contentPanel.add(createRow("Booking Date", booking.getBookingDate().toString().substring(0, 16)));
        contentPanel.add(createRow("Status", booking.getStatus(), getStatusColor(booking.getStatus())));

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createSection("CUSTOMER INFORMATION"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createRow("Customer Name", booking.getCustomerName()));

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createSection("TRIP & PAYMENT"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createRow("Trip", booking.getTripName()));
        // contentPanel.add(createRow("Payment Method", booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "N/A"));
        contentPanel.add(createRow("Total Amount", String.format("$%.2f", booking.getTotalAmount())));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnExport = createFooterButton("Export HTML", new Color(39, 174, 96));
        JButton btnClose  = createFooterButton("Close",       new Color(52, 73, 94));

        btnExport.addActionListener(e -> exportToHtml(booking));
        btnClose.addActionListener(e -> dispose());

        footerPanel.add(btnExport);
        footerPanel.add(btnClose);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createFooterButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void exportToHtml(Booking booking) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Ticket");
        chooser.setSelectedFile(new File("ticket_" + booking.getBookingCode() + ".html"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        String statusColor;
        if ("CONFIRMED".equals(booking.getStatus()))      statusColor = "#27ae60";
        else if ("PENDING".equals(booking.getStatus()))   statusColor = "#f1c40f";
        else if ("CANCELLED".equals(booking.getStatus())) statusColor = "#e74c3c";
        else                                              statusColor = "#34495e";

        String html = "<!DOCTYPE html>\n"
            + "<html lang=\"en\"><head><meta charset=\"UTF-8\">\n"
            + "<title>Ticket " + booking.getBookingCode() + "</title>\n"
            + "<style>\n"
            + "  body{font-family:'Segoe UI',Arial,sans-serif;background:#f0f2f5;margin:0;padding:30px;}\n"
            + "  .card{max-width:520px;margin:auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,.12);}\n"
            + "  .header{background:#34495e;color:#fff;padding:24px 28px;}\n"
            + "  .header h1{margin:0 0 6px;font-size:20px;letter-spacing:1px;}\n"
            + "  .header p{margin:0;font-size:13px;color:#bdc3c7;}\n"
            + "  .body{padding:28px;}\n"
            + "  .section-title{font-size:10px;font-weight:700;color:#95a5a6;letter-spacing:1.5px;text-transform:uppercase;margin:22px 0 10px;}\n"
            + "  .section-title:first-child{margin-top:0;}\n"
            + "  hr{border:none;border-top:1px solid #ecf0f1;margin:0 0 10px;}\n"
            + "  .row{display:flex;justify-content:space-between;padding:7px 0;border-bottom:1px solid #f8f9fa;}\n"
            + "  .row:last-child{border-bottom:none;}\n"
            + "  .label{color:#7f8c8d;font-size:13px;}\n"
            + "  .value{font-weight:700;font-size:13px;color:#2c3e50;}\n"
            + "  .status{color:" + statusColor + ";}\n"
            + "  .footer{background:#f8f9fa;border-top:1px solid #e0e0e0;padding:14px 28px;font-size:11px;color:#95a5a6;text-align:right;}\n"
            + "</style></head><body>\n"
            + "<div class=\"card\">\n"
            + "  <div class=\"header\"><h1>TICKET DETAILS</h1><p>#" + booking.getBookingCode() + "</p></div>\n"
            + "  <div class=\"body\">\n"
            + "    <div class=\"section-title\">Booking Information</div><hr>\n"
            + "    <div class=\"row\"><span class=\"label\">Booking Code</span><span class=\"value\">" + booking.getBookingCode() + "</span></div>\n"
            + "    <div class=\"row\"><span class=\"label\">Booking Date</span><span class=\"value\">" + booking.getBookingDate().toString().substring(0, 16) + "</span></div>\n"
            + "    <div class=\"row\"><span class=\"label\">Status</span><span class=\"value status\">" + booking.getStatus() + "</span></div>\n"
            + "    <div class=\"section-title\">Customer Information</div><hr>\n"
            + "    <div class=\"row\"><span class=\"label\">Customer Name</span><span class=\"value\">" + booking.getCustomerName() + "</span></div>\n"
            + "    <div class=\"section-title\">Trip &amp; Payment</div><hr>\n"
            + "    <div class=\"row\"><span class=\"label\">Trip</span><span class=\"value\">" + booking.getTripName() + "</span></div>\n"
            // + "    <div class=\"row\"><span class=\"label\">Payment Method</span><span class=\"value\">" + (booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "N/A") + "</span></div>\n"
            + "    <div class=\"row\"><span class=\"label\">Total Amount</span><span class=\"value\">" + String.format("$%.2f", booking.getTotalAmount()) + "</span></div>\n"
            + "  </div>\n"
            + "  <div class=\"footer\">Generated on " + new java.util.Date() + "</div>\n"
            + "</div></body></html>\n";

        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            w.write(html);
            JOptionPane.showMessageDialog(this, "Exported successfully!\n" + file.getAbsolutePath());
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(149, 165, 166));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(236, 240, 241));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(sep, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRow(String label, String value) {
        return createRow(label, value, new Color(52, 73, 94));
    }

    private JPanel createRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblKey.setForeground(new Color(127, 140, 141));
        lblKey.setPreferredSize(new Dimension(140, 20));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVal.setForeground(valueColor);

        row.add(lblKey, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.CENTER);
        return row;
    }

    private Color getStatusColor(String status) {
        if ("CONFIRMED".equals(status)) return new Color(39, 174, 96);
        if ("PENDING".equals(status))   return new Color(241, 196, 15);
        if ("CANCELLED".equals(status)) return new Color(231, 76, 60);
        return new Color(52, 73, 94);
    }
}
