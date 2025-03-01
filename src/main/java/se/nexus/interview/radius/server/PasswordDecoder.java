package se.nexus.interview.radius.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PasswordDecoder {

    public static String decode(byte[] encodedPassword, byte[] requestAuthenticator) throws NoSuchAlgorithmException {
        byte[] sharedSecretBytes = Constants.SHARED_SECRET.getBytes();
        byte[] combinedBytes = combineArrays(sharedSecretBytes, requestAuthenticator);

        byte[] currentHash = MessageDigest.getInstance("MD5").digest(combinedBytes);
        byte[] decryptedPasswordBytes = new byte[encodedPassword.length];

        // Decrypt password in chunks of 16 bytes with old hash input
        for (int i = 0; i < encodedPassword.length; i += 16) {
            int chunkSize = Math.min(16, encodedPassword.length - i);
            byte[] result = decodeChunk(encodedPassword, currentHash, i, chunkSize);
            System.arraycopy(result, 0, decryptedPasswordBytes, i, chunkSize);

            // Handle password longer than 16 bytes but it should be not more than 128 bytes as per the RADIUS protocol
            if (encodedPassword.length > i + 16) {
                currentHash = getHash(sharedSecretBytes, result);
            }
        }

        // Remove padding bytes
        byte[] finalDecryptedPasswordBytes = removePadding(decryptedPasswordBytes);
        String decryptedPassword = new String(finalDecryptedPasswordBytes);
        return decryptedPassword;
    }

    private static byte[] combineArrays(byte[] array1, byte[] array2) {
        byte[] combined = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, combined, 0, array1.length);
        System.arraycopy(array2, 0, combined, array1.length, array2.length);
        return combined;
    }

    private static byte[] decodeChunk(byte[] encodedPassword, byte[] currentHash, int offset, int chunkSize) {
        byte[] result = new byte[chunkSize];
        for (int j = 0; j < chunkSize; j++) {
            result[j] = (byte) ((encodedPassword[offset + j] & 0xFF) ^ (currentHash[j] & 0xFF));
        }
        return result;
    }

    private static byte[] getHash(byte[] sharedSecretBytes, byte[] result) throws NoSuchAlgorithmException {
        byte[] input = combineArrays(sharedSecretBytes, result);
        return MessageDigest.getInstance("MD5").digest(input);
    }

    private static byte[] removePadding(byte[] decodedPasswordBytes) {
        int lastIndex = decodedPasswordBytes.length;
        for (int i = decodedPasswordBytes.length - 1; i >= 0; i--) {
            if (decodedPasswordBytes[i] != 0) {
                lastIndex = i + 1;
                break;
            }
        }
        return Arrays.copyOfRange(decodedPasswordBytes, 0, lastIndex);
    }
}