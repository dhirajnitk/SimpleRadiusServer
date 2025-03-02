package se.nexus.interview.radius.server.response;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import se.nexus.interview.radius.server.request.Constants;

public class ResponseAccessAcceptPacket implements ResponsePacket {
    private final int identifier;
    private final byte[] requestAuthenticator;
    private final String username;

    public ResponseAccessAcceptPacket(int identifier, byte[] requestAuthenticator, String username) {
        this.identifier = identifier;
        this.requestAuthenticator = requestAuthenticator;
        this.username = username;
    }

    @Override
    public byte[] build() throws NoSuchAlgorithmException, IOException {
        ByteBuffer attributeBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE - 20);
        // Service-Type
        Attribute.append(attributeBuffer, (byte) 6, new byte[]{(byte) 1});
        // Framed-IP-Address
        Attribute.append(attributeBuffer, (byte) 8, InetAddress.getByName(Constants.FRAMED_IP_ADDRESS).getAddress());
        // Framed-IP-Netmask
        Attribute.append(attributeBuffer, (byte) 9, InetAddress.getByName(Constants.FRAMED_IP_NETMASK).getAddress());
        // Class
        Attribute.append(attributeBuffer, (byte) 25, generateClassAttribute(username).getBytes());
        // Session-Timeout
        Attribute.append(attributeBuffer, (byte) 27, ByteBuffer.allocate(4).putInt(3600).array());

        int attributeLength = attributeBuffer.position();
        byte[] attributeBytes = Arrays.copyOfRange(attributeBuffer.array(), 0, attributeLength);

        ByteBuffer headerBuffer = ByteBuffer.allocate(20);
        // Access-Accept code
        headerBuffer.put((byte) 2);
        headerBuffer.put((byte) identifier);
        headerBuffer.putShort((short) (20 + attributeLength));
        headerBuffer.put(requestAuthenticator);

        byte[] headerBytes = headerBuffer.array();

        ByteBuffer acessAcceptBuffer = ByteBuffer.allocate(20 + attributeLength);
        acessAcceptBuffer.put(headerBytes);
        acessAcceptBuffer.put(attributeBytes);

        byte[] accessAcceptPacket = acessAcceptBuffer.array();

        byte[] responseAuthenticator = ResponseBuilder.getAccessResponse(ByteBuffer.wrap(accessAcceptPacket), requestAuthenticator, accessAcceptPacket.length);

        System.arraycopy(responseAuthenticator, 0, accessAcceptPacket, 4, 16);

        return accessAcceptPacket;
    }

    public String generateClassAttribute(String username) {
        String sessionId = UUID.randomUUID().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "CLASS-" + username + "-" + timeStamp + "-" + sessionId;
    }

}