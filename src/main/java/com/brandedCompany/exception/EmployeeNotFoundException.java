package com.brandedCompany.exception;

import com.brandedCompany.domain.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeNotFoundException extends RuntimeException {
    final Logger logger = LoggerFactory.getLogger(EmployeeNotFoundException.class);

    private Exception exception;
    private String errorCode;
    private Employee employee;
    public EmployeeNotFoundException(String errorCode, Employee employee) {
        this.errorCode = errorCode;
        this.employee =employee;
    }

    }

