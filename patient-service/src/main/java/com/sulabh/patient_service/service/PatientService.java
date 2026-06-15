package com.sulabh.patient_service.service;

import com.sulabh.patient_service.dto.PatientRequestDTO;
import com.sulabh.patient_service.dto.PatientResponseDTO;
import com.sulabh.patient_service.exception.EmailAlreadyExistsException;
import com.sulabh.patient_service.exception.PatientNotFoundException;
import com.sulabh.patient_service.mapper.PatientMapper;
import com.sulabh.patient_service.model.Patient;
import com.sulabh.patient_service.repo.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private  final PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = repo.findAll();
        List<PatientResponseDTO> response = new ArrayList<>();

        for(Patient patient : patients) {
            response.add(PatientMapper.toDTO(patient));
        }

        return  response;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO)  {
        if(repo.existsByEmail(patientRequestDTO.getEmail())) {
            // already patient is created with this email
            throw new EmailAlreadyExistsException("A person with this email is already exists: " + patientRequestDTO.getEmail());
        }
        Patient newPatient = repo.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = repo.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        if(repo.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists : " + patientRequestDTO.getEmail());
        }
        // now update the details
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(patientRequestDTO.getDateOfBirth());
        patient.setEmail(patientRequestDTO.getEmail());

        Patient updatedPatient = repo.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        // first check if the patient is already present in the db or not
        Patient patient = repo.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        repo.deleteById(id);
    }
}
