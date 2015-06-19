package org.eclipse.tutorials.leshan;

import java.util.Date;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;

public class SmartWatchServer {

    public static void main(String[] args) {

        // build the lwm2m server
        final LeshanServer server = new LeshanServerBuilder().build();

        // listen for new client registrations
        server.getClientRegistry().addListener(new ClientRegistryListener() {

            @Override
            public void registered(Client client) {
                System.out.println("New client: " + client);

                // write the current time resource
                WriteRequest write = new WriteRequest("/3/0/13", new LwM2mResource(13, Value.newDateValue(new Date())),
                        ContentFormat.TEXT, true);
                LwM2mResponse response = server.send(client, write);

                System.out.println(response + " from client " + client.getEndpoint());
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
