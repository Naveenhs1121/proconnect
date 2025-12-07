package com.proconnect.proconnect;

public class RegisterWithOtpRequest {

    private String name;
    private String email;
    private String password;
    private String role;   // USER / PROFESSIONAL

    // only if PROFESSIONAL:
    private String phone;
    private String serviceType;
    private String city;

    private String otp;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}

