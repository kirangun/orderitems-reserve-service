package com.azprc.itemreserver;

import com.azprc.itemreserver.pojo.Order;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class Function {
    // Connection string for Blob Storage
    // Get the connection string and container name from environment variables
    private static final String STORAGE_CONNECTION_STRING = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
    private static final String CONTAINER_NAME = System.getenv("AZURE_STORAGE_CONTAINER_NAME");

    @FunctionName("processOrder")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Parse the incoming request body (order details)
        String requestBody = request.getBody().orElse("");
        if (requestBody.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Request body is empty").build();
        }

        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            // Deserialize the order details JSON into an Order object
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(requestBody, Order.class);

            // Upload the order to Blob Storage
            uploadOrderToBlob(order.getSessionId(), requestBody);

            // Return a success response
            return request.createResponseBuilder(HttpStatus.OK).body("Order processed successfully").build();

        } catch (IOException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the order").build();
        }

    }

    private void uploadOrderToBlob(String sessionId, String orderDetailsJson) {
        // Create a BlobServiceClient
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(STORAGE_CONNECTION_STRING)
                .buildClient();

        // Get the BlobContainerClient
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        // Create a BlobClient for the session ID file
        BlobClient blobClient = containerClient.getBlobClient(sessionId + ".json");

        // Convert the order JSON to a byte array (since the upload method requires InputStream or byte[])
        byte[] jsonBytes = orderDetailsJson.getBytes();

        // Upload the order JSON, overwrite the existing file if it exists
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonBytes)) {
            blobClient.upload(byteArrayInputStream, jsonBytes.length, true);
            System.out.println("Order uploaded to blob storage with session ID: " + sessionId);
        } catch (IOException e) {
            System.err.println("Error uploading order: " + e.getMessage());
        }
    }
}
