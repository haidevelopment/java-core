package com.ticket.repository;

import com.ticket.model.Booking;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static void exportBookings(List<Booking> bookings, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bookings");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Mã booking", "Khách hàng", "Chuyến đi", "Ngày đặt", "Tổng tiền", "Trạng thái", "Thanh toán"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        // Data rows
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(b.getId());
            row.createCell(1).setCellValue(b.getBookingCode());
            row.createCell(2).setCellValue(b.getCustomerName());
            row.createCell(3).setCellValue(b.getTripName());
            row.createCell(4).setCellValue(b.getBookingDate().toString().substring(0, 16));
            row.createCell(5).setCellValue(b.getTotalAmount());
            row.createCell(6).setCellValue(b.getStatus());
            row.createCell(7).setCellValue(b.getPaymentMethod() != null ? b.getPaymentMethod() : "N/A");
        }

        // Save file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}