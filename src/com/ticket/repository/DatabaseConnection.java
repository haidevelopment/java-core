package com.ticket.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@//localhost:1521/ORCL";
    private static final String DEFAULT_SID_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    private static final String DEFAULT_USER = "system";
    private static final String DEFAULT_PASSWORD = "oracle";
    private static final String TABLESPACE = System.getProperty("DB_TABLESPACE", "");

    public static Connection getConnection() {
        loadDotEnvIfPresent();

        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String url = getSetting("DB_URL", DEFAULT_URL);
            String user = getSetting("DB_USER", DEFAULT_USER);
            String password = getSetting("DB_PASSWORD", DEFAULT_PASSWORD);

            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("ĐÃ KẾT NỐI ORACLE THÀNH CÔNG!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("LỖI: Thiếu file ojdbc8.jar!");
        } catch (SQLException e) {
            System.err.println("LỖI KẾT NỐI DATABASE: " + e.getMessage());
            try {
                String sidUrl = getSetting("DB_SID_URL", DEFAULT_SID_URL);
                String user = getSetting("DB_USER", DEFAULT_USER);
                String password = getSetting("DB_PASSWORD", DEFAULT_PASSWORD);
                connection = DriverManager.getConnection(sidUrl, user, password);
                if (connection != null)
                    System.out.println("ĐÃ KẾT NỐI ORACLE THÀNH CÔNG!");
            } catch (SQLException e2) {
                System.err.println("KẾT NỐI THẤT BẠI");
            }
        }
        return connection;
    }

    private static String getSetting(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private static String tablespaceClause() {
        if (TABLESPACE == null || TABLESPACE.isBlank()) {
            return "";
        }
        return " TABLESPACE " + TABLESPACE;
    }

    private static void loadDotEnvIfPresent() {
        if (System.getProperty("DB_URL") != null
                || System.getProperty("DB_USER") != null
                || System.getProperty("DB_PASSWORD") != null
                || System.getProperty("DB_SID_URL") != null) {
            return;
        }

        Path envPath = Path.of(".env");
        if (!Files.exists(envPath)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(envPath, StandardCharsets.UTF_8);
            for (String rawLine : lines) {
                if (rawLine == null)
                    continue;
                String line = rawLine.trim();
                if (line.isEmpty())
                    continue;
                if (line.startsWith("#"))
                    continue;

                int idx = line.indexOf('=');
                if (idx <= 0)
                    continue;

                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();

                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                if (!key.isEmpty() && System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static void initDatabase() {
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("KHÔNG THỂ KHỞI TẠO DB: Kết nối đang bị NULL!");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            if ("true".equalsIgnoreCase(System.getProperty("db.recreate"))) {
                System.out.println(">>> Dang DROP bang cu...");
                String[] dropOrder = {"BOOKINGS", "COUPONS", "CUSTOMERS", "TRIPS", "SETTINGS", "ACCOUNTS"};
                for (String t : dropOrder) {
                    try {
                        stmt.execute("DROP TABLE " + t + " CASCADE CONSTRAINTS");
                        System.out.println("   Da DROP bang " + t);
                    } catch (SQLException e) {
                        if (e.getErrorCode() == 942) {
                            System.out.println("   Bang " + t + " chua ton tai, bo qua.");
                        } else { e.printStackTrace(); }
                    }
                }
            }
            String usersSQL = "CREATE TABLE ACCOUNTS (" +
                    "ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "USERNAME VARCHAR2(50) UNIQUE NOT NULL, " +
                    "PASSWORD VARCHAR2(100) NOT NULL, " +
                    "FULL_NAME VARCHAR2(100), " +
                    "PHONE_NUMBER VARCHAR2(20), " +
                    "EMAIL VARCHAR2(100), " +
                    "ROLE VARCHAR2(20) DEFAULT 'STAFF' CHECK (ROLE IN ('ADMIN', 'STAFF')), " +
                    "CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)" + tablespaceClause();
            executeCreateTable(stmt, "ACCOUNTS", usersSQL);

            String customersSQL = "CREATE TABLE CUSTOMERS (" +
                    "ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "FULL_NAME VARCHAR2(100) NOT NULL, " +
                    "PHONE_NUMBER VARCHAR2(20) UNIQUE NOT NULL, " +
                    "EMAIL VARCHAR2(100), " +
                    "CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)" + tablespaceClause();
            executeCreateTable(stmt, "CUSTOMERS", customersSQL);
            // thông tin chuyến đi
            String tripsSQL = "CREATE TABLE TRIPS (" +
                    "ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "TRIP_NAME VARCHAR2(100) NOT NULL, " +
                    "START_LOCATION VARCHAR2(100), " +
                    "END_LOCATION VARCHAR2(100), " +
                    "DEPARTURE_TIME TIMESTAMP, " +
                    "BASE_PRICE NUMBER(10,2), " +
                    "TOTAL_SEATS NUMBER(3), " +
                    "AVAILABLE_SEATS NUMBER(3))" + tablespaceClause();
            executeCreateTable(stmt, "TRIPS", tripsSQL);

            String bookingsSQL = "CREATE TABLE BOOKINGS (" +
                    "ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "BOOKING_CODE VARCHAR2(20) UNIQUE NOT NULL, " +
                    "CREATED_BY NUMBER REFERENCES ACCOUNTS(ID), " +
                    "CUSTOMER_ID NUMBER REFERENCES CUSTOMERS(ID), " +
                    "TRIP_ID NUMBER REFERENCES TRIPS(ID), " +
                    "BOOKING_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "TOTAL_SEATS NUMBER(3), " +
                    "TOTAL_AMOUNT NUMBER(10,2), " +
                    "STATUS VARCHAR2(20) DEFAULT 'PENDING' CHECK (STATUS IN ('PENDING', 'CONFIRMED', 'CANCELLED')), " +
                    "PAYMENT_METHOD VARCHAR2(20), " +
                    "COUPON_CODE VARCHAR2(50))" + tablespaceClause();
            executeCreateTable(stmt, "BOOKINGS", bookingsSQL);


// Bảng mã giảm giá
            String couponSQL = "CREATE TABLE COUPONS (" +
                    "ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "CODE VARCHAR2(50) UNIQUE NOT NULL, " +
                    "DISCOUNT_PERCENT NUMBER(5,2), " +
                    "DISCOUNT_AMOUNT NUMBER(10,2), " +
                    "EXPIRED_DATE DATE, " +
                    "IS_ACTIVE NUMBER(1) DEFAULT 1 CHECK (IS_ACTIVE IN (0, 1)), " +
                    "CHECK ((DISCOUNT_PERCENT IS NULL AND DISCOUNT_AMOUNT IS NOT NULL) " +
                    "   OR (DISCOUNT_PERCENT IS NOT NULL AND DISCOUNT_AMOUNT IS NULL)), " +
                    "CHECK (DISCOUNT_PERCENT IS NULL OR (DISCOUNT_PERCENT >= 1 AND DISCOUNT_PERCENT <= 100)))" + tablespaceClause();
            executeCreateTable(stmt, "COUPONS", couponSQL);

            // Bảng Settings
            String settingsSQL = "CREATE TABLE SETTINGS (" +
                    "KEY_NAME VARCHAR2(100) PRIMARY KEY, " +
                    "VALUE VARCHAR2(500))" + tablespaceClause();
            executeCreateTable(stmt, "SETTINGS", settingsSQL);

            seedData(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
            }
        }
    }

    private static void executeCreateTable(Statement stmt, String tableName, String sql) throws SQLException {
        try {
            stmt.execute(sql);
            System.out.println("Đã tạo bảng " + tableName + " thành công!");
        } catch (SQLException e) {
            if (e.getErrorCode() == 955) {
                System.out.println("Bảng " + tableName + " đã tồn tại.");
            } else {
                throw e;
            }
        }
    }

    private static void seedData(Statement stmt) throws SQLException {
        // Kiem tra xem da co du lieu mau chua (check admin account)
        try {
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ACCOUNTS WHERE USERNAME = 'admin'");
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println(">>> Du lieu mau da ton tai, bo qua seed data.");
                rs.close();
                return;
            }
            rs.close();
        } catch (SQLException e) {
            // Table might not exist yet, continue seeding
        }

        // 1. Chen tai khoan (10)
        String[][] accounts = {
            {"admin", "admin123", "Administrator", "ADMIN", "0900000000"},
            {"staff01", "staff123", "Nguyen Van Minh", "STAFF", "0901000001"},
            {"staff02", "staff123", "Tran Thi Lan", "STAFF", "0901000002"},
            {"staff03", "staff123", "Le Van Hung", "STAFF", "0901000003"},
            {"staff04", "staff123", "Pham Thi Mai", "STAFF", "0901000004"},
            {"staff05", "staff123", "Hoang Van Nam", "STAFF", "0901000005"},
            {"manager01", "manager1", "Dang Thi Huong", "ADMIN", "0902000001"},
            {"staff06", "staff123", "Bui Van Phuc", "STAFF", "0901000006"},
            {"staff07", "staff123", "Vo Thi Nhung", "STAFF", "0901000007"},
            {"staff08", "staff123", "Nguyen Van Thanh", "STAFF", "0901000008"},
        };
        for (String[] a : accounts) {
            try {
                stmt.execute("INSERT INTO ACCOUNTS (USERNAME, PASSWORD, FULL_NAME, ROLE, PHONE_NUMBER) VALUES ('"
                    + a[0] + "', '" + a[1] + "', '" + a[2] + "', '" + a[3] + "', '" + a[4] + "')");
            } catch (SQLException e) { e.printStackTrace(); }
        }
        System.out.println(">>> Da tao " + accounts.length + " tai khoan.");

        // 2. Chen chuyen di (12)
        String[][] trips = {
            {"Ha Noi - Da Nang", "Ha Noi", "Da Nang", "2026-07-10 06:00:00", "150000", "40", "35"},
            {"Sai Gon - Nha Trang", "Sai Gon", "Nha Trang", "2026-07-10 08:00:00", "120000", "30", "20"},
            {"Ha Noi - Hai Phong", "Ha Noi", "Hai Phong", "2026-07-11 05:30:00", "80000", "35", "30"},
            {"Sai Gon - Da Lat", "Sai Gon", "Da Lat", "2026-07-11 07:00:00", "180000", "30", "25"},
            {"Da Nang - Hue", "Da Nang", "Hue", "2026-07-12 09:00:00", "60000", "35", "32"},
            {"Can Tho - Sai Gon", "Can Tho", "Sai Gon", "2026-07-12 06:30:00", "130000", "35", "28"},
            {"Ha Noi - Sa Pa", "Ha Noi", "Sa Pa", "2026-07-13 22:00:00", "200000", "30", "25"},
            {"Sai Gon - Phan Thiet", "Sai Gon", "Phan Thiet", "2026-07-13 07:30:00", "110000", "35", "30"},
            {"Vinh - Ha Noi", "Vinh", "Ha Noi", "2026-07-14 19:00:00", "160000", "40", "35"},
            {"Da Nang - Sai Gon", "Da Nang", "Sai Gon", "2026-07-14 15:00:00", "320000", "35", "30"},
            {"Ha Noi - Thanh Hoa", "Ha Noi", "Thanh Hoa", "2026-07-15 06:00:00", "100000", "30", "27"},
            {"Nha Trang - Da Nang", "Nha Trang", "Da Nang", "2026-07-15 08:30:00", "170000", "35", "30"},
        };
        for (String[] t : trips) {
            try {
                stmt.execute("INSERT INTO TRIPS (TRIP_NAME, START_LOCATION, END_LOCATION, DEPARTURE_TIME, BASE_PRICE, TOTAL_SEATS, AVAILABLE_SEATS) "
                    + "VALUES ('" + t[0] + "', '" + t[1] + "', '" + t[2] + "', TIMESTAMP '" + t[3] + "', " + t[4] + ", " + t[5] + ", " + t[6] + ")");
            } catch (SQLException e) { e.printStackTrace(); }
        }
        System.out.println(">>> Da tao " + trips.length + " chuyen di.");

        // 3. Chen khach hang (10)
        String[][] customers = {
            {"Nguyen Van An", "0987654321", "an.nguyen@gmail.com"},
            {"Tran Thi Bich", "0912345678", "bich.tran@yahoo.com"},
            {"Le Van Cuong", "0923456789", "cuong.le@outlook.com"},
            {"Pham Thi Dung", "0934567890", "dung.pham@gmail.com"},
            {"Hoang Van Em", "0945678901", "em.hoang@gmail.com"},
            {"Vo Thi Phuong", "0956789012", "phuong.vo@yahoo.com"},
            {"Bui Van Giap", "0967890123", "giap.bui@gmail.com"},
            {"Dang Thi Hong", "0978901234", "hong.dang@outlook.com"},
            {"Ngo Van Ich", "0989012345", "ich.ngo@gmail.com"},
            {"Do Thi Khanh", "0990123456", "khanh.do@yahoo.com"},
        };
        for (String[] c : customers) {
            try {
                stmt.execute("INSERT INTO CUSTOMERS (FULL_NAME, PHONE_NUMBER, EMAIL) VALUES ('"
                    + c[0] + "', '" + c[1] + "', '" + c[2] + "')");
            } catch (SQLException e) { e.printStackTrace(); }
        }
        System.out.println(">>> Da tao " + customers.length + " khach hang.");

        // 4. Chen coupon (10)
        // [code, percent|null, amount|null, expiredDate, isActive]
        Object[][] coupons = {
            {"SUMMER10", 10.0, null, "2026-09-30", 1},
            {"WELCOME20", 20.0, null, "2026-08-31", 1},
            {"FLAT50K", null, 50000.0, "2026-12-31", 1},
            {"TET2026", 15.0, null, "2026-07-15", 1},
            {"VIP25", 25.0, null, "2026-10-31", 1},
            {"FLAT30K", null, 30000.0, "2026-08-31", 1},
            {"STUDENT5", 5.0, null, "2026-12-31", 1},
            {"EXPIRED50", 50.0, null, "2025-01-01", 0},
            {"HOLIDAY15", 15.0, null, "2026-09-15", 1},
            {"FLAT100K", null, 100000.0, "2026-11-30", 1},
        };
        for (Object[] cp : coupons) {
            try {
                String percentVal = (cp[1] != null) ? cp[1].toString() : "NULL";
                String amountVal = (cp[2] != null) ? cp[2].toString() : "NULL";
                stmt.execute("INSERT INTO COUPONS (CODE, DISCOUNT_PERCENT, DISCOUNT_AMOUNT, EXPIRED_DATE, IS_ACTIVE) VALUES ('"
                    + cp[0] + "', " + percentVal + ", " + amountVal + ", DATE '" + cp[3] + "', " + cp[4] + ")");
            } catch (SQLException e) { e.printStackTrace(); }
        }
        System.out.println(">>> Da tao " + coupons.length + " coupon.");

        // 5. Chen 100 booking
        seedBookings(stmt);
        System.out.println(">>> Da tao 100 booking.");
    }

    private static void seedBookings(Statement stmt) throws SQLException {
        // Arrays for random selection
        int[] tripIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int[] custIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] staffIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] statuses = {"CONFIRMED", "CONFIRMED", "CONFIRMED", "CONFIRMED", "CONFIRMED", "CONFIRMED",
                              "PENDING", "PENDING", "PENDING",
                              "CANCELLED", "CANCELLED"};
        String[] payments = {"CASH", "CREDIT_CARD", "E-WALLET"};
        String[] coupons = {"SUMMER10", "WELCOME20", "FLAT50K", "VIP25", "FLAT30K", null, null, null, null, null};

        // Pre-loaded trip prices for quick lookup
        double[] tripPrices = {0, 150000, 120000, 80000, 180000, 60000, 130000, 200000, 110000, 160000, 320000, 100000, 170000};

        java.util.Random rand = new java.util.Random(42); // fixed seed for reproducibility

        for (int i = 1; i <= 100; i++) {
            int tripId = tripIds[rand.nextInt(tripIds.length)];
            int custId = custIds[rand.nextInt(custIds.length)];
            int staffId = staffIds[rand.nextInt(staffIds.length)];
            String status = statuses[rand.nextInt(statuses.length)];
            String payment = payments[rand.nextInt(payments.length)];
            int seats = rand.nextInt(5) + 1;
            String coupon = coupons[rand.nextInt(coupons.length)];

            double basePrice = tripPrices[tripId];
            double amount = basePrice * seats;

            // Apply coupon
            if (coupon != null) {
                if ("SUMMER10".equals(coupon)) amount -= amount * 0.10;
                else if ("WELCOME20".equals(coupon)) amount -= amount * 0.20;
                else if ("FLAT50K".equals(coupon)) amount -= 50000;
                else if ("VIP25".equals(coupon)) amount -= amount * 0.25;
                else if ("FLAT30K".equals(coupon)) amount -= 30000;
            }
            if (amount < 0) amount = 0;

            // Random date in last 60 days
            int daysAgo = rand.nextInt(60);
            String bookingDate = "TIMESTAMP '2026-07-03 00:00:00' - INTERVAL '" + daysAgo + "' DAY";

            String couponStr = (coupon != null) ? "'" + coupon + "'" : "NULL";

            String sql = "INSERT INTO BOOKINGS (BOOKING_CODE, CREATED_BY, CUSTOMER_ID, TRIP_ID, "
                + "BOOKING_DATE, TOTAL_SEATS, TOTAL_AMOUNT, STATUS, PAYMENT_METHOD, COUPON_CODE) "
                + "VALUES ('BK-" + i + "', " + staffId + ", " + custId + ", " + tripId + ", "
                + bookingDate + ", " + seats + ", " + Math.round(amount) + ", '"
                + status + "', '" + payment + "', " + couponStr + ")";

            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        initDatabase();
    }
}
