package se.nexus.interview.radius.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import se.nexus.interview.radius.server.response.RadiusServerException;

@TestMethodOrder(OrderAnnotation.class)
public class RadiusServerTest {


    @Test
    @Order(1)
    //Test verifies that the server doesn't crash when receiving an invalid packe
    public void testRadiusServerHandlesInvalidPacket() throws IOException {
       

        Thread serverThread = new Thread(() -> {
            try {
                RadiusServer.main(new String[]{});
            } catch (RadiusServerException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        try {
            Thread.sleep(1000);

            // Send a invalid packet of length 5 bytes
            byte[] invalidData = new byte[5];
            DatagramPacket invalidPacket = new DatagramPacket(invalidData, invalidData.length, InetAddress.getLocalHost(), Constants.RADIUS_PORT);
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(invalidPacket);
            }
            Thread.sleep(1000);
            assertTrue(serverThread.isAlive(), "Radius server should continue running after receiving an invalid packet.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (serverThread.isAlive()) {
                RadiusServer.shutDownServer();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    @Order(2)
    //Test verifies that the server starts and stops correctly with a valid packet
    public void testRadiusServerStartsAndStops() throws IOException, InterruptedException {
        Thread serverThread = new Thread(() -> {
            try {
                RadiusServer.main(new String[]{});
            } catch (RadiusServerException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(1000);

        // Send a dummy packet
        sendDummyPacket();
        RadiusServer.shutDownServer();
        Thread.sleep(1000);
        assertFalse(serverThread.isAlive(), "Radius server should have stopped.");
    }

    private void sendDummyPacket() throws IOException {
        byte[] data = new byte[20];
        // Access-Request code
        data[0] = 1;
        // Identifier
        data[1] = 1;
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.RADIUS_PORT);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        }
    }

}