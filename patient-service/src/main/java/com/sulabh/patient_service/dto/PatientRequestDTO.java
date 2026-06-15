package com.sulabh.patient_service.dto;

import com.sulabh.patient_service.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.Getter;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 character")
    private String name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Date of Birth is Required")
    private LocalDate dateOfBirth;

    @NotNull(
            groups = CreatePatientValidationGroup.class,
            message = "Registered date is required"
    )
    private LocalDate registeredDate;

}
