package com.azprc.itemreserver;

import com.azprc.itemreserver.model.Order;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Function {

    private static final String STORAGE_CONNECTION_STRING = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
    private static final String CONTAINER_NAME = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
    private static final Logger logger = LoggerFactory.getLogger(Function.class);

    @FunctionName("processOrder")
    public void process(
            @ServiceBusQueueTrigger(
                    name = "msg",
                    queueName = "order-queue",
                    connection = "MyStorageConnection")
            String message,
            final ExecutionContext context) {

        context.getLogger().info("Message received from queue");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(message, Order.class);

            logger.info("order details {}", order.toString());

            throw new RuntimeException("Trigger DLQ for error handling");
            //uploadOrderDetailsToBlob(order.getId(), message);

        } catch (IOException e) {
            logger.error("Exception while saving order in blob");
        }

    }

    private void uploadOrderDetailsToBlob(String sessionId, String requestBody) {

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(STORAGE_CONNECTION_STRING)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        BlobClient blobClient = containerClient.getBlobClient(sessionId + ".json");

        byte[] jsonBytes = requestBody.getBytes();

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonBytes)) {
            blobClient.upload(byteArrayInputStream, jsonBytes.length, true);
            logger.info("Order uploaded to blob storage with session ID: {}", sessionId);
        } catch (IOException e) {
            logger.info("Error uploading order: {}", e.getMessage());
        }
    }
}
