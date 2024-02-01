package com.brandedCompany.exception;

import com.brandedCompany.domain.Customer;
import com.brandedCompany.domain.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNotFoundException extends RuntimeException {
    final Logger logger = LoggerFactory.getLogger(CustomerNotFoundException.class);

    private Exception exception;
    private String errorCode;
    private Customer customer;
    public CustomerNotFoundException(String errorCode, Customer customer) {
        this.errorCode = errorCode;
        this.customer =customer;
    }

}
