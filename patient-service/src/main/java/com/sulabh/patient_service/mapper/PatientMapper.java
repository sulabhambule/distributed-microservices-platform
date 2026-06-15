package com.sulabh.patient_service.mapper;

import com.sulabh.patient_service.dto.PatientRequestDTO;
import com.sulabh.patient_service.dto.PatientResponseDTO;
import com.sulabh.patient_service.model.Patient;

import java.time.LocalDate;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        patientDTO.setId(String.valueOf(patient.getId()));
        patientDTO.setName(patient.getName());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        // Didn't expose register date to Client
        return patientDTO;
    }


    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(patientRequestDTO.getDateOfBirth());
        patient.setRegisteredDate(patientRequestDTO.getRegisteredDate()); // need to check if error occurs.
        // Didn't set ID as backend assigns that
        return patient;
    }
}
