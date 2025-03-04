package se.nexus.interview.radius.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import se.nexus.interview.radius.server.response.RadiusServerException;

@TestMethodOrder(OrderAnnotation.class)
public class RadiusServerTest {

    private Thread serverThread;
    private RadiusServer server;

    @BeforeEach
    public void setUp() throws InterruptedException {
        server = new RadiusServer();
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (RadiusServerException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        server.getLatch().await();
    }

    @AfterEach
    public void tearDown() {
        if (serverThread.isAlive()) {
            server.stop();
            serverThread.interrupt();
        }
        try {
            serverThread.join();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Order(1)
    public void testRadiusServerHandlesInvalidPacket() throws IOException {
        try {
            Thread.sleep(1000);

            // Send an invalid packet of length 5 bytes
            byte[] invalidData = new byte[5];
            DatagramPacket invalidPacket = new DatagramPacket(invalidData, invalidData.length,
                    InetAddress.getLocalHost(), Constants.RADIUS_PORT);
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(invalidPacket);
            }
            Thread.sleep(1000);
            assertTrue(serverThread.isAlive(),
                    "Radius server should continue running after receiving an invalid packet.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Order(2)
    public void testRadiusServerStartsAndStops() throws IOException, InterruptedException {
        Thread.sleep(1000);

        // Send a dummy packet
        sendDummyPacket();
        server.stop();
        Thread.sleep(1000);
        assertFalse(serverThread.isAlive(), "Radius server should have stopped.");
    }

    private void sendDummyPacket() throws IOException {
        byte[] data = new byte[20];
        // Access-Request code
        data[0] = 1;
        // Identifier
        data[1] = 1;
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),
                Constants.RADIUS_PORT);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        }
    }
}