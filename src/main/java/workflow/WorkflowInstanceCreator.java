/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package workflow;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.api.response.WorkflowInstanceEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class WorkflowInstanceCreator {

    public static void main(final String[] args) {
        final String broker = "127.0.0.1:26500";

        final String bpmnProcessId = "order-process";
        Integer[] orderIds = {124, 456, 789};
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        final Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderIds[randomNum]);
        data.put("orderItems", Arrays.asList(435, 182, 376));

        final ZeebeClientBuilder builder =
                ZeebeClient.newClientBuilder().brokerContactPoint(broker).usePlaintext();

        try (final ZeebeClient client = builder.build()) {

            System.out.println("Creating workflow instance");

            final WorkflowInstanceEvent workflowInstanceEvent =
                    client
                            .newCreateInstanceCommand()
                            .bpmnProcessId(bpmnProcessId)
                            .latestVersion()
                            .variables(data)
                            .send()
                            .join();

            System.out.println(
                    "Workflow instance created with key: " + workflowInstanceEvent.getWorkflowInstanceKey());
            if ((Integer) data.get("orderId") % 2 == 0) {
                client
                        .newPublishMessageCommand()
                        .messageName("PaymentConfirmation")
                        .correlationKey(data.get("orderId").toString())
                        .variables(data)
                        .send()
                        .join();
                System.out.println("published event: " + data.get("orderId").toString());
            }
        }
    }
}
