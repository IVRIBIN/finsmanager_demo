package com.tutu.finsmanager.registration;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String password;
    private String newpassword;
    private String approval;
    private String phone;
    private String role;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String FirstName) {this.firstName = FirstName;}
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String LastName) {this.lastName = LastName;}
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getEmail() {return email;}
    public void setEmail(String Email) {this.email = Email;}
    public String getPassword() {return password;}
    public void setPassword(String Password) {this.password = Password;}
    public String getNewpassword() {return newpassword;}
    public void setNewpassword(String Newpassword) {this.newpassword = Newpassword;}
    public String getApproval() { return approval; }
    public void setApproval(String approval) { this.approval = approval; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
