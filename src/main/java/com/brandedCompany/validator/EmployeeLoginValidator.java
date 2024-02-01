package com.brandedCompany.validator;

import com.brandedCompany.domain.Employee;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.exception.TableNotFoundException;
import com.brandedCompany.serivce.CRUDService;
import com.brandedCompany.util.CustomerControllerUtils;
import com.brandedCompany.util.DomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import static com.brandedCompany.util.DomainUtils.TABLE.EMPLOYEES;

@Component
public class EmployeeLoginValidator implements Validator
{
    final Logger logger = LoggerFactory.getLogger(EmployeeLoginValidator.class);

    private static CRUDService crud;

    @Autowired
    public EmployeeLoginValidator(
        @Qualifier("CRUDServiceImpl")
            CRUDService crud)
    {
        this.crud = crud;
    }

    @Override
    public boolean supports(Class<?> clazz)
    {
        return Employee.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@Valid Object target, Errors errors)
    {
        Employee employee = (Employee) target;

        BigInteger employeeId = employee.getEmployeeId();
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        String fullName = employee.getFullName();

        Optional<BigInteger> optId=Optional.ofNullable(employeeId);
        Optional<String> optFirstName = Optional.ofNullable(firstName);
        Optional<String> optLastName = Optional.ofNullable(lastName);

        if ((optId.isEmpty() && optFirstName.isEmpty()&& optLastName.isEmpty())||
             optId.isEmpty() && Objects.requireNonNull(firstName)
                                       .isBlank() && Objects.requireNonNull(lastName)
                                                            .isBlank())
        {
            errors.reject("required.employee.employeeIdName");
            logger.warn("직원번호와 직원이름을 입력해주세요 !");
        }
        else if (Optional.ofNullable(employeeId)
                         .isEmpty())
        {
            errors.reject("required.employee.employeeId");
            logger.warn("직원번호를 입력해주세요 !");
        }
        else if (!CustomerControllerUtils.checkName(fullName))
        {
            errors.reject("typeMismatch.employee.name");
            logger.warn("직원이름을 문자로 입력해주세요 !");
        }
        else if (firstName.isBlank()&&lastName.isBlank())
        {
            errors.reject("required.employee.fullName");
            logger.warn("직원이름을 입력해주세요 !");
        }
        else if (firstName.trim()
                          .isEmpty())
        {
            errors.reject("required.employee.firstName");
            logger.warn("직원 firstName 을 입력해주세요 !");
        }
        else if (lastName.trim()
                         .isEmpty())
        {
            errors.reject("required.employee.lastName");
            logger.warn("직원 lastName 을 입력해주세요 !");
        }
        else if (!isEmployee(employee))
        {
            logger.warn("notFound.employee->직원을 찾지 못했습니다 !");
            errors.reject("notFound.employee");
        }
    }

    private boolean isEmployee(Employee target)
    {
        final DomainUtils.TABLE TABLE = EMPLOYEES;
        BigInteger id = target.getEmployeeId();

        Employee employee = null;
        try
        {
            if (!crud.isExist(TABLE, id))
                throw new TableNotFoundException(TABLE);
            employee = (Employee) crud.select(TABLE, id);
        }
        catch (NullPointerException | DomainNotFoundException | ClassNotFoundException e)
        {
            //            e.printStackTrace();
            return false;
        }
        catch (Exception e)
        {
            //            e.printStackTrace();
            return false;
        }
        return employee.equals(target);

    }
}
