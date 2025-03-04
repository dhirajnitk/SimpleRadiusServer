package se.nexus.interview.radius.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import se.nexus.interview.radius.server.response.RadiusServerException;

/*
RadiusServer class implements the Server interface and provides the implementation for starting and stopping the server.
The server listens on the RADIUS_PORT for incoming packets and processes them using a thread pool of THREAD_POOL_SIZE.
The server is stopped by closing the socket and shutting down the executor service.
Thread safe is ensured by using AtomicBoolean for isRunning flag and synchronized methods for start and stop.
*/

public class RadiusServer implements Server {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ExecutorService executorService;
    private DatagramSocket socket;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public synchronized void start() throws RadiusServerException {
        if (isRunning.get()) {
            throw new IllegalStateException("Server is already running");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        try {
            socket = new DatagramSocket(Constants.RADIUS_PORT);
            executorService = Executors.newCachedThreadPool();
            byte[] buffer = new byte[Constants.BUFFER_SIZE];

            System.out.println("SimpleRadius server running on port " + Constants.RADIUS_PORT);

            isRunning.set(true);
            countDownLatch.countDown(); // Signal that the server has started
            while (isRunning.get()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                int receivedLength = packet.getLength();
                if (receivedLength < Constants.MIN_PACKET_SIZE) {
                    System.err.println("Received packet is too short: " + receivedLength + " bytes");
                    continue;
                }
                executorService.submit(() -> processPacket(packet));
            }
        } catch (SocketException e) {
            throw new RadiusServerException("Failed to create socket: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RadiusServerException("Failed to receive packet: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void stop() {
        if (!isRunning.get()) {
            return;
        }
        isRunning.set(false);
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void processPacket(DatagramPacket packet) {
        try {
            PacketProcessorFactory.getProcessor(packet).process(packet);
        } catch (IllegalArgumentException e) {
            System.err.println("Received Invalid Radius packet: " + e.getMessage());
        } catch (IOException | NoSuchAlgorithmException | RadiusServerException e) {
            System.err.println("Error processing packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RadiusServerException {
        RadiusServer server = new RadiusServer();
        server.start();
    }

    public CountDownLatch getLatch() {
        return countDownLatch;
    }
}