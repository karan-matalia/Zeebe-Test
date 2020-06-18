package com.karan.zeebetest.workflow;

import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.ZeebeClientLifecycle;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(classPathResource = "order-process.bpmn")
public class SpringZeebeWorkflowInstance {
    @Autowired
    private ZeebeClientLifecycle client;

    public static void main(final String[] args) {
        SpringApplication.run(SpringZeebeWorkflowInstance.class, args);
    }

    public void startProcesses() {
        if (!client.isRunning()) {
            return;
        }
        final String bpmnProcessId = "order-process";
        Integer[] orderIds = {124, 456, 789};
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        final Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderIds[randomNum]);
        data.put("orderItems", Arrays.asList(435, 182, 376));
        final WorkflowInstanceEvent event =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId(bpmnProcessId)
                        .latestVersion()
                        .variables(data)
                        .send()
                        .join();

        System.out.println("Workflow instance created with key: " + event.getWorkflowInstanceKey());
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
