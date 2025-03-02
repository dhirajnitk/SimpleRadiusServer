package se.nexus.interview.radius.server.response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import se.nexus.interview.radius.server.request.Constants;


public class ResponseAccessRejectPacket implements ResponsePacket {
    private final int identifier;
    private final byte[] requestAuthenticator;
    private final String message;


    public ResponseAccessRejectPacket(int identifier, byte[] requestAuthenticator, String message) {
        this.identifier = identifier;
        this.requestAuthenticator = requestAuthenticator;
        this.message = message;
    }

    @Override
    public byte[] build() throws NoSuchAlgorithmException, IOException {
        byte[] messageBytes = message.getBytes();
    
        ByteBuffer attributeBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE - 20);
        Attribute.append(attributeBuffer, (byte) 18, messageBytes);
    
        int attributeLength = attributeBuffer.position();
        byte[] attributeBytes = Arrays.copyOfRange(attributeBuffer.array(), 0, attributeLength);
    
        ByteBuffer headerBuffer = ByteBuffer.allocate(20);
        // Access-Reject code
        headerBuffer.put((byte) 3);
        headerBuffer.put((byte) identifier);
        headerBuffer.putShort((short) (20 + attributeLength));
        headerBuffer.put(requestAuthenticator);
    
        byte[] headerBytes = headerBuffer.array();
    
        ByteBuffer accessRejectBuffer = ByteBuffer.allocate(20 + attributeLength);
        accessRejectBuffer.put(headerBytes);
        accessRejectBuffer.put(attributeBytes);
    
        byte[] accessRejectPacket = accessRejectBuffer.array();
    
        byte[] responseAuthenticator = ResponseBuilder.getAccessResponse(ByteBuffer.wrap(accessRejectPacket), requestAuthenticator, accessRejectPacket.length);
    
        System.arraycopy(responseAuthenticator, 0, accessRejectPacket, 4, 16);
    
        return accessRejectPacket;
    }
 }
