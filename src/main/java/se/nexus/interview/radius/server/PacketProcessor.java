// Version: 2021.09.19

 package se.nexus.interview.radius.server;

 import java.io.IOException;
 import java.net.DatagramPacket;
 import java.security.NoSuchAlgorithmException;
 
 public interface PacketProcessor {
     void process(DatagramPacket packet) throws IOException, NoSuchAlgorithmException, RadiusServerException;
 }
