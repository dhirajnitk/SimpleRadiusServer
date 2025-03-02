package se.nexus.interview.radius.server.response;

public class RadiusServerException extends Exception {
    private static final long serialVersionUID = 188678608602485400L;

    public RadiusServerException(String string) {
        super(string);
    }

    public RadiusServerException(String string, Exception e) {
        super(string, e);
    }
}
