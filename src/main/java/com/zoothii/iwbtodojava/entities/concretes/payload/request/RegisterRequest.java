package com.zoothii.iwbtodojava.entities.concretes.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    // TODO activate later
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$",
            message = "one special character '@#$%', one number, one lowercase character, one UPPERCASE character, less than 20 and more than 8 characters")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> roles;

}
