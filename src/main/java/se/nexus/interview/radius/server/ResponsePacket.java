package se.nexus.interview.radius.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ResponsePacket {
    byte[] build() throws NoSuchAlgorithmException, IOException;
}