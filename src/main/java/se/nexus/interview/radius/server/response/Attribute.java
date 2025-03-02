package se.nexus.interview.radius.server.response;

import java.nio.ByteBuffer;

public class Attribute {

    public static void append(ByteBuffer buf, byte type, byte[] val) {
        if (val == null) {
            System.err.println("Error: Attribute value is null for type " + type);
            return;
        }

        int len = val.length + 2;

        if (len > 255) {
            System.err.println("Error: Attribute len exceeds max 255 for type " + type);
            return;
        }

        buf.put(type);
        buf.put((byte) len);
        buf.put(val);
    }
}