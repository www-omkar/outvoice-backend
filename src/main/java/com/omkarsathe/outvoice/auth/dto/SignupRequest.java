package com.omkarsathe.outvoice.auth.dto;

import com.omkarsathe.outvoice.common.validation.EmailOrMobileRequired;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EmailOrMobileRequired
public class SignupRequest {

    @NotBlank
    private String fullName;

    @Email
    private String email;

    private String mobileNumber;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    private String organizationName;

    // Defaults to fullName in the service if not provided
    private String taxComplianceName;

    private String panNumber;
    private String gstNumber;
    private String tanNumber;
}
