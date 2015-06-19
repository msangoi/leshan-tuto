package org.eclipse.tutorials.leshan;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;

public class SmartWatchClient {

    private final LeshanClient client;

    // the registration ID assigned by the server
    private String registrationId;

    public SmartWatchClient(String serverHost, int serverPort) {

        // Initialize object list with the standard device object (id 3)
        List<ObjectEnabler> enablers = new ObjectsInitializer().create(3);

        // Create client
        final InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
        client = new LeshanClient(serverAddress, new ArrayList<LwM2mObjectEnabler>(enablers));

        client.start();

        // De-register on shutdown and stop client.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (registrationId != null) {
                    client.send(new DeregisterRequest(registrationId));
                    client.stop();
                }
            }
        });
    }

    public boolean register() {

        RegisterResponse response = client.send(new RegisterRequest("my-smart-watch"));

        System.out.println("Register response code: " + response.getCode());
        if (response.getCode() == ResponseCode.CREATED) {
            registrationId = response.getRegistrationID();
            System.out.println("Registered with id: " + registrationId);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

        // String serverHost = "leshan.eclipse.org";
        String serverHost = "localhost";

        SmartWatchClient client = new SmartWatchClient(serverHost, 5683);
        boolean registered = client.register();

        System.out.println(registered ? "Congrats!" : "Client registration failed");
    }
}
