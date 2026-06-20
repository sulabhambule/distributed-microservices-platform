package com.sulabh.patient_service.kafka;

import com.sulabh.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate; // spring helper class for talking to kafka

    // the spring boot see the dependencies and creates the kafkaProducerFactory and then create kafkaTemplate
    // so spring have the kafkaTemplate so we inject it here. (same as autowire bean stuffs of the spring boot)
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();
        try {
            kafkaTemplate.send("patient", event.toByteArray());
            // kafka store bytes not the java object. so the patient event becomes byte[] array.
        } catch (Exception ex) {
            log.error("Error sending patientCreated event : {}", event);
        }
    }
}
