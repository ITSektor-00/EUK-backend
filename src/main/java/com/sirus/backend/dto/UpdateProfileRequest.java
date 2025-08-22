package com.sirus.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    
    @NotBlank(message = "Ime je obavezno")
    @Size(min = 2, max = 50, message = "Ime mora biti između 2 i 50 karaktera")
    private String firstName;
    
    @NotBlank(message = "Prezime je obavezno")
    @Size(min = 2, max = 50, message = "Prezime mora biti između 2 i 50 karaktera")
    private String lastName;
    
    @NotBlank(message = "Email adresa je obavezna")
    @Email(message = "Email adresa mora biti validna")
    private String email;
    
    // Opciono polje za username - ako se ne pošalje, neće se ažurirati
    @Size(min = 3, max = 50, message = "Korisničko ime mora biti između 3 i 50 karaktera")
    private String username;
    
    // Opciono polje za lozinku - ako se ne pošalje, neće se ažurirati
    @Size(min = 6, message = "Lozinka mora biti najmanje 6 karaktera")
    private String password;
    
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
