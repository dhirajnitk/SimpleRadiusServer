package se.nexus.interview.radius.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;


public class ResponseAccessRejectPacket implements ResponsePacket {
    private final int identifier;
    private final byte[] requestAuthenticator;
    private final String message;
    private final ByteBuffer buffer;

    public ResponseAccessRejectPacket(int identifier, byte[] requestAuthenticator, String message) {
        this.identifier = identifier;
        this.requestAuthenticator = requestAuthenticator;
        this.message = message;
        this.buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    }

    @Override
    public byte[] build() throws NoSuchAlgorithmException, IOException {
        ByteBuffer headerBuffer = ByteBuffer.allocate(20);
        // Access-Reject code
        headerBuffer.put((byte) 3);
        headerBuffer.put((byte) identifier);
        headerBuffer.putShort((short) 20);
        headerBuffer.put(requestAuthenticator);
        buffer.put(headerBuffer.array());
        // Reply-Message
        Attribute.append(buffer, (byte) 18, message.getBytes());

        return ResponseBuilder.finalizeAccessResponse(buffer, requestAuthenticator);
    }
 }
