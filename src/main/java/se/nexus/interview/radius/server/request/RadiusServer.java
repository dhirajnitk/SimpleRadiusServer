package se.nexus.interview.radius.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

import se.nexus.interview.radius.server.response.RadiusServerException;

public class RadiusServer {

    private static boolean isRunning = true;

    public static void shutDownServer() {
        isRunning = false;
    }

    public static void main(String[] args) throws RadiusServerException {
        Runtime.getRuntime().addShutdownHook(new Thread(RadiusServer::shutDownServer));

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Constants.RADIUS_PORT);
            byte[] buffer = new byte[Constants.BUFFER_SIZE];

            System.out.println("SimpleRadius server running on port " + Constants.RADIUS_PORT);

            while (isRunning) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                try {
                    PacketProcessorFactory.getProcessor(packet).process(packet);
                } catch (IllegalArgumentException e) {
                    System.err.println("Received Invalid Radius packet: " + e.getMessage());
                } catch (IOException | NoSuchAlgorithmException e) {
                    System.err.println("Error processing packet: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            System.err.println("Failed to create socket: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to receive packet: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}