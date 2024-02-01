package com.brandedCompany.validator;

import com.brandedCompany.domain.Customer;
import com.brandedCompany.exception.TableNotFoundException;
import com.brandedCompany.serivce.CRUDService;
import com.brandedCompany.util.DomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigInteger;
import java.util.Optional;

import static com.brandedCompany.util.CustomerControllerUtils.checkName;
import static com.brandedCompany.util.DomainUtils.TABLE.CUSTOMERS;

@Component
public class CustomerLoginValidator implements Validator {

    final Logger logger = LoggerFactory.getLogger(CustomerLoginValidator.class);


    CRUDService crud;

    public CustomerLoginValidator(@Qualifier("CRUDServiceImpl") CRUDService crud) {
        this.crud = crud;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer customer = (Customer) target;

        BigInteger customerId = customer.getCustomerId();
        String customerName = customer.getName();

        if ((Optional.ofNullable(customerId).isEmpty() && customer.getName().trim().isEmpty())) {
            errors.reject("required.customer.customerIdName");
            logger.error("required.customer.customerIdName -> 고객번호와 고객이름을 입력해주세요 !");
        } else if (Optional.ofNullable(customerId).isEmpty()) {
            errors.reject("required.customer.customerId");
            logger.error("required.customer.customerId->고객번호를 숫자로 입력해주세요 !");
        } else if (!checkName(customerName)) {
            errors.reject("typeMismatch.customer.name");
            logger.error("typeMismatch.customer.name->고객이름을 문자로 입력해주세요 !");
        } else if (customerName.trim().isEmpty()) {
            errors.reject("required.customer.name");
            logger.error("required.customer.name->고객이름을 입력해주세요 !");
        } else if (!isCustomer(customer)) {
            logger.error("notFound.customer->고객을 찾지 못했습니다 !");
            errors.reject("notFound.customer");
        }



}
    private boolean isCustomer(Customer target) {
        final DomainUtils.TABLE TABLE = CUSTOMERS;
        BigInteger targetId = target.getCustomerId();
        Customer customer = null;
        try {
            if (!crud.isExist(TABLE, targetId)) throw new TableNotFoundException(TABLE);
            customer = (Customer) crud.select(TABLE, targetId);
        } catch (Exception e) {
            //     e.printStackTrace();
            return false;
        }
        return customer.equals(target);

    }

}
