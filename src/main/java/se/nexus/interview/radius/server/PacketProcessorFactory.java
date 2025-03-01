package se.nexus.interview.radius.server;

import java.net.DatagramPacket;

public class PacketProcessorFactory {

    public static PacketProcessor getProcessor(DatagramPacket packet) {
        byte[] data = packet.getData();
        int code = data[0] & 0xFF;

        if (code == RadiusCode.AccessRequest.code) {
            return new AccessRequestProcessor();
        } else {
            throw new IllegalArgumentException("Unsupported packet type: " + code);
        }
    }
}