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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class Function {

    private static final String STORAGE_CONNECTION_STRING = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
    private static final String CONTAINER_NAME = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
    private static final Logger logger = LoggerFactory.getLogger(Function.class);

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
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(requestBody, Order.class);

            logger.info("order details "+ order.toString());

            uploadOrderDetailsToBlob(order.getSessionId(), requestBody);

            return request.createResponseBuilder(HttpStatus.OK).body("Order processed successfully").build();

        } catch (IOException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the order").build();
        }

    }

    private void uploadOrderDetailsToBlob(String sessionId, String orderDetailsJson) {

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(STORAGE_CONNECTION_STRING)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        BlobClient blobClient = containerClient.getBlobClient(sessionId + ".json");

        byte[] jsonBytes = orderDetailsJson.getBytes();

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonBytes)) {
            blobClient.upload(byteArrayInputStream, jsonBytes.length, true);
            logger.info("Order uploaded to blob storage with session ID: {}", sessionId);
        } catch (IOException e) {
            logger.info("Error uploading order: {}", e.getMessage());
        }
    }
}
