package se.nexus.interview.radius.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ResponseBuilder {

    public static byte[] generateAccessAccept(int id, byte[] reqAuth, String userName)
            throws NoSuchAlgorithmException, IOException {
        ResponsePacket packet = ResponsePacketFactory.createAccessAcceptPacket(id, reqAuth, userName);
        return packet.build();
    }

    public static byte[] generateAccessReject(int id, byte[] reqAuth, String message)
            throws NoSuchAlgorithmException, IOException {
        ResponsePacket packet = ResponsePacketFactory.createAccessRejectPacket(id, reqAuth, message);
        return packet.build();
    }

    public static byte[] generateResponseAuthenticator(byte[] reqAuth, byte[] respPacket, int len, String secret)
            throws NoSuchAlgorithmException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        bos.write(respPacket, 0, 4);
        bos.write(reqAuth, 0, 16);
        bos.write(respPacket, 20, len - 20);
        md5.update(bos.toByteArray());
        md5.update(secret.getBytes());
        return md5.digest();
    }
    
    public static byte[] finalizeAccessResponse(ByteBuffer buffer, byte[] requestAuthenticator)
            throws NoSuchAlgorithmException, IOException {
        int length = buffer.position();
        buffer.putShort(2, (short) length);

        byte[] responseAuthenticator = ResponseBuilder.generateResponseAuthenticator(requestAuthenticator, buffer.array(), length, Constants.SHARED_SECRET);
        for (int i = 0; i < 16; i++) {
            buffer.put(4 + i, responseAuthenticator[i]);
        }

        return Arrays.copyOfRange(buffer.array(), 0, length);
    }
    
}