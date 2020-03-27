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

        Blog blogToBeUpdated = Blog.newBuilder()
                .setId(response.getBlog().getId())
                .setAuthorId(response.getBlog().getAuthorId())
                .setContent(response.getBlog().getContent())
                .setTitle("New Title")
                .build();

        UpdateBlogResponse updateResponse = blogCliente.updateBlog(UpdateBlogRequest.newBuilder().setBlog(
                blogToBeUpdated
        ).build());

        System.out.println("Received updated blog response");
        System.out.println(updateResponse.getBlog().toString());

        DeleteBlogResponse deleteResponse = blogCliente.deleteBlog(DeleteBlogRequest.newBuilder()
                .setBlogId(response.getBlog().getId())
                .build());

        System.out.println("Received delete blog response");
        System.out.println(deleteResponse.getBlogId());
    }

}
