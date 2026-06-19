package com.sulabh.billing_service.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {
        log.info("creatingBillingAccount request received {}", billingRequest.toString());

        // Business logic for example save to db and perform arithmetic
        BillingResponse response = BillingResponse.newBuilder()
                .setAccoundId("12345")
                .setStatus("ACTIVE")
                .build();

        // used to send a response from gRPC server to client
        responseObserver.onNext(response);

        // response is completed and ending current cycle of responses
        responseObserver.onCompleted();
    }
}
