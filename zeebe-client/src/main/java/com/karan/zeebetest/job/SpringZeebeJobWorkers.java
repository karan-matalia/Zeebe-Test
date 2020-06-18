package com.karan.zeebetest.job;

import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableZeebeClient
public class SpringZeebeJobWorkers {
    public static void main(String[] args) {
        SpringApplication.run(SpringZeebeJobWorkers.class, args);
    }

    @ZeebeWorker(type = "payment-service")
    public void handlePaymentServiceJob(final JobClient client, final ActivatedJob job) {
        final Map<String, Object> variables = job.getVariablesAsMap();
        Object orderId = variables.get("orderId");
        System.out.println("Process order: " + orderId);
        double price = 0;
        if ((Integer) orderId % 2 != 0) {
            price = 40.0;
        } else {
            price = 55.0;
        }
        System.out.println("Collect money: $" + price);
        final Map<String, Object> result = new HashMap<>();
        result.put("totalPrice", price);
        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }

    @ZeebeWorker(type = "inventory-service")
    public void handleInventoryServiceJob(final JobClient client, final ActivatedJob job) {
        System.out.println("fetched items");
        client.newCompleteCommand(job.getKey())
                .send()
                .join();
    }

    @ZeebeWorker(type = "shipment-service")
    public void handleShipmentServiceJob(final JobClient client, final ActivatedJob job) {
        final Map<String, Object> variables = job.getVariablesAsMap();
        System.out.println("ship items. Price: " + variables.get("totalPrice"));
        client.newCompleteCommand(job.getKey())
                .send()
                .join();
    }

    @ZeebeWorker(type = "insurance-service")
    public void handleInsuranceServiceJob(final JobClient client, final ActivatedJob job) {
        System.out.println("adding insurance");
        client.newCompleteCommand(job.getKey())
                .send()
                .join();
    }
}
