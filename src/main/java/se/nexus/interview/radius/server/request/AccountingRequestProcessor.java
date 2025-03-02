package se.nexus.interview.radius.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.security.NoSuchAlgorithmException;

import se.nexus.interview.radius.server.response.RadiusServerException;

public class AccountingRequestProcessor implements PacketProcessor {

    AccountingRequestProcessor() {
    }

     @Override
    public void process(DatagramPacket packet) throws IOException, NoSuchAlgorithmException, RadiusServerException {
        throw new UnsupportedOperationException("AccountingRequestProcessor is not supported");
    }
}