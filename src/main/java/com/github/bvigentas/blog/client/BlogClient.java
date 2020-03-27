package com.github.bvigentas.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    public static void main(String[] args) {

        BlogClient main = new BlogClient();
        main.run();

    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50055)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub blogCliente = BlogServiceGrpc.newBlockingStub(channel);


        Blog blog = Blog.newBuilder()
                .setContent("Hello World this is my first blog")
                .setTitle("New Blog")
                .setAuthorId("Bruno")
                .build();

        CreateBlogRequest request = CreateBlogRequest.newBuilder().setBlog(blog).build();

        CreateBlogResponse response = blogCliente.createBlog(request);

        System.out.println("Received created blog response");
        System.out.println(response.toString());

        ReadBlogResponse readResponse = blogCliente.readBlog(ReadBlogRequest.newBuilder().setBlogId(
                response.getBlog().getId()
        ).build());

        System.out.println("Received read blog response");
        System.out.println(readResponse.toString());
    }

}
