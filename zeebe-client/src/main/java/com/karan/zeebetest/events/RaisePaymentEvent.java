package com.karan.zeebetest.events;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientBuilder;

public class RaisePaymentEvent {
    public static void main(final String[] args) {
        final String broker = "127.0.0.1:26500";
        final ZeebeClientBuilder builder =
                ZeebeClient.newClientBuilder().brokerContactPoint(broker).usePlaintext();

        try (final ZeebeClient client = builder.build()) {
        }
    }
}
