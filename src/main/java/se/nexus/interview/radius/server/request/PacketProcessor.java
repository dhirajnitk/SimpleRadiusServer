 package se.nexus.interview.radius.server.request;

 import java.io.IOException;
 import java.net.DatagramPacket;
 import java.security.NoSuchAlgorithmException;

import se.nexus.interview.radius.server.response.RadiusServerException;
 
 public interface PacketProcessor {
     void process(DatagramPacket packet) throws IOException, NoSuchAlgorithmException, RadiusServerException;
 }
