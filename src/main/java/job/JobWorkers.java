package job;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.api.worker.JobWorker;

import java.util.HashMap;
import java.util.Map;

public class JobWorkers {
    public static void main(final String[] args) {
        final String broker = "localhost:26500";
        final ZeebeClientBuilder builder =
                ZeebeClient.newClientBuilder().brokerContactPoint(broker).usePlaintext();

        try (final ZeebeClient client = builder.build()) {
            final JobWorker jobWorker = client.newWorker()
                    .jobType("payment-service")
                    .handler((jobClient, job) ->
                    {
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
                        jobClient.newCompleteCommand(job.getKey())
                                .variables(result)
                                .send()
                                .join();
                    })
                    .open();
            final JobWorker jobWorker1 = client.newWorker()
                    .jobType("inventory-service")
                    .handler((jobClient, job) ->
                    {
                        System.out.println("fetched items");
                        jobClient.newCompleteCommand(job.getKey())
                                .send()
                                .join();
                    })
                    .open();
            final JobWorker jobWorker2 = client.newWorker()
                    .jobType("shipment-service")
                    .handler((jobClient, job) ->
                    {
                        final Map<String, Object> variables = job.getVariablesAsMap();
                        System.out.println("ship items. Price: " + variables.get("totalPrice"));
                        jobClient.newCompleteCommand(job.getKey())
                                .send()
                                .join();
                    })
                    .open();
            final JobWorker jobWorker3 = client.newWorker()
                    .jobType("insurance-service")
                    .handler((jobClient, job) ->
                    {
                        System.out.println("adding insurance");
                        jobClient.newCompleteCommand(job.getKey())
                                .send()
                                .join();
                    })
                    .open();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
