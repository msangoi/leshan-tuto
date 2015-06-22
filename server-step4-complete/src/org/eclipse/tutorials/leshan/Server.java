package org.eclipse.tutorials.leshan;

import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.observation.Observation;
import org.eclipse.leshan.server.observation.ObservationRegistryListener;

public class Server {

    public static void main(String[] args) {

        final LeshanServer server = new LeshanServerBuilder().build();

        // listen for observe notifications
        server.getObservationRegistry().addListener(new ObservationRegistryListener() {

            @Override
            public void newValue(Observation observation, LwM2mNode value) {

                System.out.println("New notification from client " + observation.getClient().getEndpoint() + ": "
                        + value);
            }

            @Override
            public void newObservation(Observation observation) {

                System.out.println("Observing resource " + observation.getPath() + " from client "
                        + observation.getClient().getEndpoint());
            }

            @Override
            public void cancelled(Observation observation) {
                // TODO
            }

        });

        // start the observation for each new client
        server.getClientRegistry().addListener(new ClientRegistryListener() {

            @Override
            public void registered(Client client) {
                LwM2mResponse response = server.send(client, new ObserveRequest(3, 0, 13));
                System.out.println("Observe response: " + response);
            }

            @Override
            public void updated(Client client) {
                //
            }

            @Override
            public void unregistered(Client client) {
                //
            }

        });

        server.start();
    }

}
