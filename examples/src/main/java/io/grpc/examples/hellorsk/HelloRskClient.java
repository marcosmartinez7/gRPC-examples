package io.grpc.examples.hellorsk;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloRskClient {
    private static final Logger logger = Logger.getLogger(HelloRskClient.class.getName());

    private final ManagedChannel channel;
    private final RSKGreeterGrpc.RSKGreeterBlockingStub blockingStub;

    public HelloRskClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build());
    }

    HelloRskClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = RSKGreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRskRequest request = HelloRskRequest.newBuilder().setName(name).build();
        HelloRskReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Hello from RSK: " + response.getMessage());
    }


    public static void main(String[] args) throws Exception {
        HelloRskClient client = new HelloRskClient("localhost", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
            String user = "Marcos";
            if (args.length > 0) {
                user = args[0]; /* Use the arg as the name to greet if provided */
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
