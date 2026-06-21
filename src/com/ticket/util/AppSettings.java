package com.ticket.util;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.nio.file.Files;

import java.nio.file.Path;

import java.util.Properties;


public class AppSettings {

    private static final Path SETTINGS_PATH = Path.of("app-settings.properties");

    private static final Properties props = new Properties();



    static {

        load();

    }



    private AppSettings() {

    }



    public static void load() {
        props.clear();

        // Default system properties
        props.setProperty("web.base.url", "http://localhost:5173");
        props.setProperty("api.port", "8080");

        // Default user properties
        props.setProperty("company.name", "Booking Pro");
        props.setProperty("company.hotline", "1900 1234");
        props.setProperty("ticket.footer", "Cảm ơn quý khách. Vui lòng xuất trình vé khi lên xe.");
        props.setProperty("sms.enabled", "true");
        props.setProperty("sms.provider", "console");
        props.setProperty("sms.webhook.url", "");
        props.setProperty("sms.api.key", "");

        if (Files.exists(SETTINGS_PATH)) {
            try (InputStream in = Files.newInputStream(SETTINGS_PATH)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            com.ticket.repository.SettingsRepository repo = new com.ticket.repository.SettingsRepository();
            java.util.Map<String, String> dbSettings = repo.getAllSettings();
            for (java.util.Map.Entry<String, String> entry : dbSettings.entrySet()) {
                props.setProperty(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Failed to load settings from DB: " + e.getMessage());
        }
    }

    public static void save() {
        // Save user properties to DB
        try {
            com.ticket.repository.SettingsRepository repo = new com.ticket.repository.SettingsRepository();
            java.util.Map<String, String> dbSettings = new java.util.HashMap<>();
            dbSettings.put("company.name", props.getProperty("company.name", ""));
            dbSettings.put("company.hotline", props.getProperty("company.hotline", ""));
            dbSettings.put("ticket.footer", props.getProperty("ticket.footer", ""));
            dbSettings.put("sms.enabled", props.getProperty("sms.enabled", "false"));
            dbSettings.put("sms.provider", props.getProperty("sms.provider", "console"));
            dbSettings.put("sms.webhook.url", props.getProperty("sms.webhook.url", ""));
            dbSettings.put("sms.api.key", props.getProperty("sms.api.key", ""));
            repo.saveAllSettings(dbSettings);
        } catch (Exception e) {
            System.err.println("Failed to save settings to DB: " + e.getMessage());
        }

        // Save system properties to file
        Properties fileProps = new Properties();
        fileProps.setProperty("web.base.url", props.getProperty("web.base.url", "http://localhost:5173"));
        fileProps.setProperty("api.port", props.getProperty("api.port", "8080"));
        
        try (OutputStream out = Files.newOutputStream(SETTINGS_PATH)) {
            fileProps.store(out, "Booking Pro System Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static String getCompanyName() {

        return props.getProperty("company.name", "Booking Pro");

    }



    public static void setCompanyName(String value) {

        props.setProperty("company.name", value != null ? value.trim() : "");

    }



    public static String getCompanyHotline() {

        return props.getProperty("company.hotline", "1900 1234");

    }



    public static void setCompanyHotline(String value) {

        props.setProperty("company.hotline", value != null ? value.trim() : "");

    }



    public static String getTicketFooter() {

        return props.getProperty("ticket.footer", "");

    }



    public static void setTicketFooter(String value) {

        props.setProperty("ticket.footer", value != null ? value.trim() : "");

    }



    public static String getWebBaseUrl() {

        String url = props.getProperty("web.base.url", "http://localhost:5173");

        if (url.endsWith("/")) {

            return url.substring(0, url.length() - 1);

        }

        return url;

    }



    public static void setWebBaseUrl(String value) {

        props.setProperty("web.base.url", value != null ? value.trim() : "");

    }



    public static int getApiPort() {

        try {

            return Integer.parseInt(props.getProperty("api.port", "8080"));

        } catch (NumberFormatException e) {

            return 8080;

        }

    }



    public static void setApiPort(String value) {

        props.setProperty("api.port", value != null ? value.trim() : "8080");

    }



    public static boolean isSmsEnabled() {

        return "true".equalsIgnoreCase(props.getProperty("sms.enabled", "true"));

    }



    public static void setSmsEnabled(boolean enabled) {

        props.setProperty("sms.enabled", String.valueOf(enabled));

    }



    public static String getSmsProvider() {

        return props.getProperty("sms.provider", "console");

    }



    public static void setSmsProvider(String value) {

        props.setProperty("sms.provider", value != null ? value.trim() : "console");

    }



    public static String getSmsWebhookUrl() {

        return props.getProperty("sms.webhook.url", "");

    }



    public static void setSmsWebhookUrl(String value) {

        props.setProperty("sms.webhook.url", value != null ? value.trim() : "");

    }



    public static String getSmsApiKey() {

        return props.getProperty("sms.api.key", "");

    }



    public static void setSmsApiKey(String value) {

        props.setProperty("sms.api.key", value != null ? value.trim() : "");

    }



    public static String getTicketWebUrl(String bookingCode) {

        return getWebBaseUrl() + "/ticket/" + bookingCode;

    }

}

