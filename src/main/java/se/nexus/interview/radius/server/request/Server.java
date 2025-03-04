package se.nexus.interview.radius.server.request;

import se.nexus.interview.radius.server.response.RadiusServerException;

public interface Server {
    void start() throws RadiusServerException;
    void stop();
}