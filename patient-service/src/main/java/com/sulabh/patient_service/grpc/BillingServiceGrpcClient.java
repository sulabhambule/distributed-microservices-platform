package com.sulabh.patient_service.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    // we need a logger
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    // Blocking stub : A stub to allow clients to do synchronous rpc calls to service BillingService.

    // HTTP -> TCP Connection  gRPC -> ManagedChannel(network between patient and billing service)

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grcp.port:9001}") int serverPort
    ) {
        log.info("Connecting to Billing service GRPC service at {}:{}", serverAddress, serverPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
        // Creates a new blocking-style stub that supports unary and streaming output calls on the service
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest billingRequest = BillingRequest.newBuilder().setName(name).setEmail(email).setPatientId(patientId).build();
        BillingResponse billingResponse = blockingStub.createBillingAccount(billingRequest);

        log.info("Receive response from billing service via GRPC: {}", billingResponse);
        return billingResponse;
    }
}
/*

we can also do this , this is a simpler way to connect the patient client and call services of the billing service.
@Service
public class BillingGrpcClient {

    @GrpcClient("billing-service")
    private BillingServiceGrpc.BillingServiceBlockingStub billingStub;

    public BillingResponse createBillingAccount(Patient patient) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .build();

        return billingStub.createBillingAccount(request);
    }
}
Then in application.properties:
grpc.client.billing-service.address=static://localhost:9001
grpc.client.billing-service.negotiationType=plaintext
* */