package se.nexus.interview.radius.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RadiusServerAuthenticator {

    private static final Map<String, String> RADIUS_USERS = new HashMap<>();

    static {
        Properties properties = new Properties();
        try (InputStream input = RadiusServerAuthenticator.class.getClassLoader().getResourceAsStream("users.properties")) {
            if (input == null) {
                throw new RuntimeException("Not able to find users.properties");
            }
            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                RADIUS_USERS.put(key, properties.getProperty(key));
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load user credentials: " + e.getMessage());
        }
    }

    public static boolean checkCredential(String username, String password) {
        String userPassword = RADIUS_USERS.get(username);
        return userPassword != null && userPassword.equals(password);
    }
}
