package com.brandedCompany.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Setter
@Component
public class Employee extends Domain{

    private BigInteger employeeId,  managerId;
    private String firstName,lastName,email,phone,jobTitle;
    private LocalDate hireDate;

    private String imageBase64;


    public Employee(BigInteger employeeId, BigInteger managerId, String firstName, String lastName, String email, String phone, String jobTitle, LocalDate hireDate)
    {
        this.employeeId = employeeId;
        this.managerId = managerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
    }

    public Employee(BigInteger employeeId, String firstName, String lastName)
    {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Employee(BigInteger employeeId,String fullName){
        this.employeeId = employeeId;
        String[] names = fullName.split(" ");
        if(null==names) throw new IllegalArgumentException();
        this.firstName=names[0];
        this.lastName = names[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, firstName, lastName);
    }
    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", managerId=" + managerId +
                ", fistName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", hireDate=" + hireDate +
                '}';
    }

    public String getFullName(){
        return firstName+" "+lastName;
    }
}
