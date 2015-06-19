package org.eclipse.tutorials.leshan;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class SmartWatchClient {

    private final LeshanClient client;

    // the registration ID assigned by the server
    private String registrationId;

    public SmartWatchClient(String serverHost, int serverPort) {

        ObjectsInitializer initializer = new ObjectsInitializer();
        initializer.setClassForObject(3, Device.class);
        List<ObjectEnabler> enablers = initializer.create(3);

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

    public static class Device extends BaseInstanceEnabler {

        private final String manufacturelModel = "EclipseCon Tuto Client";
        private final String modelNumber = "2015";
        private final String serialNumber = "leshan-client-001";
        private final BindingMode bindingModel = BindingMode.U;

        private AtomicLong currentTimestamp = new AtomicLong(0);

        public Device() {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    currentTimestamp.getAndAdd(1000);
                }
            }, 1000, 1000);
        }

        @Override
        public ValueResponse read(int resourceid) {
            switch (resourceid) {

            // TODO read resources

            default:
                return super.read(resourceid);
            }
        }

        // TODO write current timestamp

        // TODO exec reboot

    }

    public void register() {

        RegisterResponse response = client.send(new RegisterRequest("my-smart-watch"));

        if (response.getCode() == ResponseCode.CREATED) {
            registrationId = response.getRegistrationID();
            System.out.println("Registered with id: " + registrationId);
        }
    }

    public static void main(String[] args) {

        // String serverHost = "localhost";
        String serverHost = "leshan.eclipse.org";

        SmartWatchClient client = new SmartWatchClient(serverHost, 5683);
        client.register();
    }
}
