
package se.nexus.interview.radius.server.response;

public enum RadiusCode {
    AccessRequest(1),
    AccessAccept(2),
    AccessReject(3),
    AccountingRequest(4),
    AccountingResponse(5),
    AccessChallenge(11),
    StatusServer_experimental(12),
    StatusClient_experimental(13),
    Reserved(255);

    public final int code;

    private RadiusCode(int code) {
        this.code = code;
    }

    public static RadiusCode parse(int read) throws RadiusServerException {
        for (RadiusCode code : values()) {
            if (read == code.code) {
                return code;
            }
        }

        throw new RadiusServerException("Unknown Radius code");
    }
}
