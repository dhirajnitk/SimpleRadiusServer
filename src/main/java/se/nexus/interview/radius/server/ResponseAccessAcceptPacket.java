package se.nexus.interview.radius.server;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ResponseAccessAcceptPacket implements ResponsePacket {
    private final int identifier;
    private final byte[] requestAuthenticator;
    private final String username;
    private final ByteBuffer buffer;

    public ResponseAccessAcceptPacket(int identifier, byte[] requestAuthenticator, String username) {
        this.identifier = identifier;
        this.requestAuthenticator = requestAuthenticator;
        this.username = username;
        this.buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    }

    @Override
    public byte[] build() throws NoSuchAlgorithmException, IOException {
        ByteBuffer headerBuffer = ByteBuffer.allocate(20);
        // Access-Accept code
        headerBuffer.put((byte) 2);
        headerBuffer.put((byte) identifier);
        headerBuffer.putShort((short) 20);
        headerBuffer.put(requestAuthenticator);
        buffer.put(headerBuffer.array());
        // Service-Type
        Attribute.append(buffer, (byte) 6, new byte[]{(byte) 1});
        // Framed-IP-Address
        Attribute.append(buffer, (byte) 8, InetAddress.getByName(Constants.FRAMED_IP_ADDRESS).getAddress());
        // Framed-IP-Netmask
        Attribute.append(buffer, (byte) 9, InetAddress.getByName(Constants.FRAMED_IP_NETMASK).getAddress());
        // Class
        Attribute.append(buffer, (byte) 25, generateClassAttribute(username).getBytes());
        // Session-Timeout
        Attribute.append(buffer, (byte) 27, ByteBuffer.allocate(4).putInt(3600).array());

        return ResponseBuilder.finalizeAccessResponse(buffer, requestAuthenticator);
    }

    private String generateClassAttribute(String username) {
        String sessionId = UUID.randomUUID().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "CLASS-" + username + "-" + timeStamp + "-" + sessionId;
    }

}