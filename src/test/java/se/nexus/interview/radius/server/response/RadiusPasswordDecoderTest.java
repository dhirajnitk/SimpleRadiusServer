package se.nexus.interview.radius.server.response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import se.nexus.interview.radius.server.request.Constants;

public class RadiusPasswordDecoderTest {

    @Test
    public void testDecodePassword() throws NoSuchAlgorithmException {
        // Test with a known password, encrypted password, and request authenticator
        String expectedPassword = "testPassword";
        byte[] requestAuthenticator = new byte[]{
                (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
                (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d, (byte) 0x0e, (byte) 0x0f, (byte) 0x10
        };

        // Encrypt the password
        byte[] encryptedPassword = encryptPassword(expectedPassword, requestAuthenticator);

        // Assuming Constants.SHARED_SECRET is set to "nexus" for this test
        String decryptedPassword = RadiusPasswordDecoder.decode(encryptedPassword, requestAuthenticator);
        assertEquals(expectedPassword, decryptedPassword, "Decrypted password should match the expected password.");
    }

    private byte[] encryptPassword(String password, byte[] requestAuthenticator) throws NoSuchAlgorithmException {
        byte[] sharedSecretBytes = Constants.SHARED_SECRET.getBytes();
        byte[] combined = new byte[sharedSecretBytes.length + requestAuthenticator.length];
        System.arraycopy(sharedSecretBytes, 0, combined, 0, sharedSecretBytes.length);
        System.arraycopy(requestAuthenticator, 0, combined, sharedSecretBytes.length, requestAuthenticator.length);

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] hash = md5.digest(combined);

        byte[] passwordBytes = password.getBytes();
        byte[] encryptedPassword = new byte[passwordBytes.length];

        for (int i = 0; i < passwordBytes.length; i++) {
            encryptedPassword[i] = (byte) (passwordBytes[i] ^ hash[i % 16]);
        }

        return encryptedPassword;
    }


    @Test
    public void testDecodePasswordWithEmptyRequestAuthenticator() throws NoSuchAlgorithmException {
        // Test with an empty request authenticator
        String expectedPassword = "test";
        byte[] requestAuthenticator = new byte[16]; // Empty request authenticator

        // Encrypt the password
        byte[] encryptedPassword = encryptPassword(expectedPassword, requestAuthenticator);


        // Assuming Constants.SHARED_SECRET is set to "nexus" for this test
        String decryptedPassword = RadiusPasswordDecoder.decode(encryptedPassword, requestAuthenticator);
        assertEquals(expectedPassword, decryptedPassword, "Decrypted password should match the expected password.");
    }

   
}