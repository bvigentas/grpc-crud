package com.github.bvigentas.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50055)
                .addService(new BlogServiceImpl())
                .build();

        server.start();
        System.out.println("Server Running");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            server.shutdown();
            System.out.println("Server shutdown");
        }));

        server.awaitTermination();

    }

}
