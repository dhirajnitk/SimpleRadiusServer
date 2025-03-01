package se.nexus.interview.radius.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    public static int RADIUS_PORT;
    public static String SHARED_SECRET;
    // Buffer size for reading and writing packets as per the RADIUS protocol.
    public static final int BUFFER_SIZE = 4096;
    public static String FRAMED_IP_ADDRESS;
    public static String FRAMED_IP_NETMASK;

    // Static block to initialize the constants from the config file.
    static {
        initConstants();
    }

    private static void initConstants() {
        Properties properties = new Properties();
        try (InputStream input = Constants.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new ExceptionInInitializerError("Failed to find config.properties");
            }
            properties.load(input);

            // Initialize the constants with values from the properties file.
            FRAMED_IP_ADDRESS = properties.getProperty("framed.ip.address", "127.0.0.1");
            FRAMED_IP_NETMASK = properties.getProperty("framed.ip.netmask", "255.255.255.0");
            RADIUS_PORT = Integer.parseInt(properties.getProperty("radius.port", "1000"));
            SHARED_SECRET = properties.getProperty("shared.secret", "nexus");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to read config file: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new ExceptionInInitializerError("Invalid number format in config file: " + e.getMessage());
        }
    }
}