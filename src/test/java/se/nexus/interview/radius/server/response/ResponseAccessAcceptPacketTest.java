package se.nexus.interview.radius.server.response;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import se.nexus.interview.radius.server.request.Constants;

public class ResponseAccessAcceptPacketTest {

    @Test
    void testBuild() throws NoSuchAlgorithmException, IOException {
        int identifier = 1;
        byte[] requestAuthenticator = new byte[16];
        String username = "testAccessAcceptUser";

        ResponseAccessAcceptPacket packet = new ResponseAccessAcceptPacket(identifier, requestAuthenticator, username);
        byte[] result = packet.build();
        // Calculate expected total length
        int expectedTotalLength = 20 + 3 + 6 + 6 + (2 + packet.generateClassAttribute(username).getBytes().length) + 6;
        // Verify the header
        assertEquals(2, result[0]); // Access-Accept code
        assertEquals(identifier, result[1]); // Identifier
        assertEquals(expectedTotalLength, ByteBuffer.wrap(result, 2, 2).getShort()); 
        byte[] responseAuthenticator = ResponseBuilder.generateResponseAuthenticator(requestAuthenticator, result, result.length, Constants.SHARED_SECRET);
        assertArrayEquals(responseAuthenticator, Arrays.copyOfRange(result, 4, 20)); 

        // Verify the attributes
        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.position(20); // Skip the header

        // Service-Type
        assertEquals(6, buffer.get()); // Type
        assertEquals(3, buffer.get()); // Length
        assertEquals(1, buffer.get()); // Value

        // Framed-IP-Address
        assertEquals(8, buffer.get()); // Type
        assertEquals(6, buffer.get()); // Length
        byte[] ipAddress = new byte[4];
        buffer.get(ipAddress);
        assertArrayEquals(InetAddress.getByName(Constants.FRAMED_IP_ADDRESS).getAddress(), ipAddress);

        // Framed-IP-Netmask
        assertEquals(9, buffer.get());
        // Length
        assertEquals(6, buffer.get());
        byte[] ipNetmask = new byte[4];
        buffer.get(ipNetmask);
        assertArrayEquals(InetAddress.getByName(Constants.FRAMED_IP_NETMASK).getAddress(), ipNetmask);

        // Class
        assertEquals(25, buffer.get());
        int classLength = buffer.get();
        byte[] classValue = new byte[classLength - 2];
        buffer.get(classValue);
        String expectedClassAttribute = "CLASS-" + username + "-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + UUID.randomUUID().toString();
        assertTrue(expectedClassAttribute.startsWith("CLASS-" + username + "-"));

        // Session-Timeout
        assertEquals(27, buffer.get());
        // Length
        assertEquals(6, buffer.get());
        // Value
        assertEquals(3600, buffer.getInt());
    }

    @Test
    void testGenerateClassAttribute() {
        String username = "testAccessAcceptUser";
        ResponseAccessAcceptPacket packet = new ResponseAccessAcceptPacket(1, new byte[16], username);
        String classAttribute = packet.generateClassAttribute(username);

        assertTrue(classAttribute.startsWith("CLASS-" + username + "-"));      
        assertTrue(classAttribute.split("-").length == 8);
    }
}