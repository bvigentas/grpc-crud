package com.github.bvigentas.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {


    private MongoClient mongoClient = MongoClients.create("mongodb+srv://bruno:bruno@cluster0-cogjj.mongodb.net/blog?retryWrites=true&w=majority");

    private MongoDatabase database = mongoClient.getDatabase("blog");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

        Blog blog = request.getBlog();

        Document doc = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        collection.insertOne(doc);

        String id = doc.getObjectId("_id").toString();

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {

        Blog blog = request.getBlog();

        String blogId = blog.getId();
        Document result = null;

        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();
        } catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found.")
                            .asRuntimeException()
            );
        }

        if (result == null) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found.")
                            .asRuntimeException()
            );
        } else {

            Document replacement = new Document("author_id", blog.getAuthorId())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent())
                    .append("_id", new ObjectId(blog.getId()));

            collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

            responseObserver.onNext(UpdateBlogResponse.newBuilder()
                    .setBlog(Blog.newBuilder()
                            .setAuthorId(replacement.getString("author_id"))
                            .setTitle(replacement.getString("title"))
                            .setContent(replacement.getString("content"))
                            .setId(replacement.getObjectId("_id").toString())
                            .build())
                    .build());

            responseObserver.onCompleted();
        }

    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {

        String blogId = request.getBlogId();
        Document result = null;

        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();
        } catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found.")
                            .asRuntimeException()
            );
        }

        if (result == null) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found.")
                    .asRuntimeException()
            );
        } else {

            Blog blog = Blog.newBuilder()
                    .setAuthorId(result.getString("author_id"))
                    .setTitle(result.getString("title"))
                    .setContent(result.getString("content"))
                    .setId(blogId)
                    .build();

            responseObserver.onNext(
                    ReadBlogResponse.newBuilder()
                            .setBlog(blog)
                            .build()
            );

            responseObserver.onCompleted();

        }

    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogRequest> responseObserver) {

        String blogId = request.getBlogId();
        DeleteResult result = null;

        try {
            result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
        } catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found.")
                            .asRuntimeException()
            );
        }

        if (result.getDeletedCount() == 0) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found.")
                            .asRuntimeException()
            );
        } else {
            responseObserver.onNext(DeleteBlogRequest.newBuilder()
                    .setBlogId(blogId)
                    .build());

            responseObserver.onCompleted();
        }

    }
}
