package se.nexus.interview.radius.server.request;

import java.net.DatagramPacket;

import se.nexus.interview.radius.server.response.RadiusCode;

public class PacketProcessorFactory {

    public static PacketProcessor getProcessor(DatagramPacket packet) {
        byte[] data = packet.getData();
        int code = data[0] & 0xFF;
        if (code == RadiusCode.AccessRequest.code) {
            return new AccessRequestProcessor();
        }
        // To do: Add support for AccountingRequest
        else if (code == RadiusCode.AccountingRequest.code) {
            return new AccountingRequestProcessor();
        }  
        else {
            // To support other packet types in future
            System.err.println("Unsupported packet type: " + code);
            throw new IllegalArgumentException("Unsupported packet type: " + code);
        }
    }
}