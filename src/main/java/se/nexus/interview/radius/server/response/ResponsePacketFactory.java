package se.nexus.interview.radius.server.response;

public class ResponsePacketFactory {

    public static ResponsePacket createAccessAcceptPacket(int identifier, byte[] requestAuthenticator, String userName) {
        return new ResponseAccessAcceptPacket(identifier, requestAuthenticator, userName);
    }

    public static ResponsePacket createAccessRejectPacket(int identifier, byte[] requestAuthenticator, String message) {
        return new ResponseAccessRejectPacket(identifier, requestAuthenticator, message);
    }
}