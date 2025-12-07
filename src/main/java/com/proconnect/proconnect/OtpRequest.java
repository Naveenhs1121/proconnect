package com.proconnect.proconnect;

public class OtpRequest {

    private String email;
    private String role; // USER or PROFESSIONAL

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
