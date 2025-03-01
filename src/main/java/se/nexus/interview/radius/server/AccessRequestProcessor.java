package se.nexus.interview.radius.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AccessRequestProcessor implements PacketProcessor {

    AccessRequestProcessor() {
    }

    @Override
    public void process(DatagramPacket packet) throws IOException, NoSuchAlgorithmException, RadiusServerException {
        byte[] data = packet.getData();
        int length = packet.getLength();
        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();

        String username = null;
        String password = null;

        int attributeIndex = 20;
        while (attributeIndex < length) {
            int attributeType = data[attributeIndex] & 0xFF;
            int attributeLength = data[attributeIndex + 1] & 0xFF;

            if (attributeLength <= 0 || attributeIndex + attributeLength > length) {
                throw new RadiusServerException("Invalid attribute length");
            }

            byte[] attributeValue = Arrays.copyOfRange(data, attributeIndex + 2, attributeIndex + attributeLength);

            if (attributeType == 1) {
                username = new String(attributeValue);
            } else if (attributeType == 2) {
                password = PasswordDecoder.decode(attributeValue, Arrays.copyOfRange(data, 4, 20));
            }

            attributeIndex += attributeLength;
        }

        byte[] responsePacket;
        if (username == null || password == null) {
            responsePacket = ResponseBuilder.generateAccessReject(packet.getData()[1], Arrays.copyOfRange(data, 4, 20), "Missing username or password");
        } else if (RadiusServerAuthenticator.checkCredential(username, password)) {
            responsePacket = ResponseBuilder.generateAccessAccept(packet.getData()[1], Arrays.copyOfRange(data, 4, 20), username);
            System.out.println("User " + username + " logged in successfully.");
        } else {
            responsePacket = ResponseBuilder.generateAccessReject(packet.getData()[1], Arrays.copyOfRange(data, 4, 20), "Invalid username or password");
            System.out.println("Login failed for user " + username + ".");
        }

        DatagramPacket response = new DatagramPacket(responsePacket, responsePacket.length, clientAddress, clientPort);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(response);
        }
    }
}