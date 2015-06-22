package org.eclipse.tutorials.leshan;

import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;

public class Server {

    public static void main(String[] args) {

        final LeshanServer server = new LeshanServerBuilder().build();

        // TODO

        server.start();
    }

}
