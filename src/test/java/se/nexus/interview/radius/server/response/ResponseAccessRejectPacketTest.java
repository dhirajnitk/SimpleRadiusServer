package se.nexus.interview.radius.server.response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import se.nexus.interview.radius.server.request.Constants;

public class ResponseAccessRejectPacketTest {

    @Test
    void testBuild() throws NoSuchAlgorithmException, IOException {
        int identifier = 1;
        byte[] requestAuthenticator = new byte[16];
        String message = "Access Denied";

        ResponseAccessRejectPacket packet = new ResponseAccessRejectPacket(identifier, requestAuthenticator, message);
        byte[] result = packet.build();

        int expectedTotalLength = 35;
        // Access-Reject code
        assertEquals(3, result[0]); 
        // Identifier
        assertEquals(identifier, result[1]);
        // Length
        assertEquals(expectedTotalLength, ByteBuffer.wrap(result, 2, 2).getShort()); 
        
        byte[] responseAuthenticator = ResponseBuilder.generateResponseAuthenticator(requestAuthenticator, result, result.length, Constants.SHARED_SECRET);
        assertArrayEquals(responseAuthenticator, Arrays.copyOfRange(result, 4, 20)); 
        // Verify the attributes
        ByteBuffer buffer = ByteBuffer.wrap(result);
        
        buffer.position(20);

        // Reply-Message
        assertEquals(18, buffer.get());
        int messageLength = buffer.get();
        byte[] messageBytes = new byte[messageLength - 2];
        buffer.get(messageBytes);
        assertEquals(message, new String(messageBytes));
    }
}