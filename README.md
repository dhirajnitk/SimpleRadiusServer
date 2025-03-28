
# Simple Radius Server

This is a simple Radius server implementing a subset of RFC-2865 configured to run on IP address `127.0.0.1` at port `1000` with the shared secret `nexus`.

## Configuration

config.properties stores the config properties. The values can be configured
- **IP Address:** 127.0.0.1
- **Port:** 1000
- **Shared Secret:** nexus

user.properties stores the user credentials and it can be configured.
## Command to Run

To start the Radius server, use the following command:

```
java -cp .\target\classes se.nexus.interview.radius.server.request.RadiusServer
```

Ensure you have compiled the project and have the classes in the `target/classes` and right path separator for linux/windows




### Client Run

```sh
java -cp .\radiusclient.jar se.nexus.interview.radius.packet.Main 127.0.0.1 1000 nexus
```

Ensure you have `radiusclient.jar` in your classpath before running the command and right path separator for linux/windows

Results:
1. Case 0(Logging in with frans1 successfully): status: success as expected.
2. Case 1(Logging in without password): status: fail as expected. Expected RADIUS response code was AccessAccept received result: AccessReject
3. Case 2(Logging in with unkonwn user, nisse): status: fail as expected. Server rejected the authentication request
4. Case 3(Logging in with frans1 but incorrect secret): status: fail as expected. Authenticator from server failed
5. Case 4(Logging in successfully with frans2): status: success as expected.
6. Case 5(Logging in successfully with frans1, with bad password): status: fail as expected. Server rejected the authentication request 

### Test cases

The following test files are included to ensure the functionality of the Radius server:
- `request/RadiusServerTest.java`
- `response/RadiusServerRequestTest.java`
- `response/ResponseAccessRejectPacketTest.java`
- `response/ResponseAccessAcceptPacketTest.java`

## License

This project is licensed under the MIT License.