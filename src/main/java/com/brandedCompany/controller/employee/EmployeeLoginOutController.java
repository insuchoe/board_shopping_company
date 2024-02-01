package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationNotFoundException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.concurrentConnection.EmployeeConcurrentConnectionManager;
import com.brandedCompany.concurrentConnection.EmployeeConcurrentConnectionObserver;
import com.brandedCompany.concurrentConnection.exception.ConCurrentConnectionNotFoundException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.exception.EmployeeNotFoundException;
import com.brandedCompany.serivce.CRUDService;
import com.brandedCompany.util.DomainUtils;
import com.brandedCompany.util.EmployeeControllerUtils;
import com.brandedCompany.util.ImageUtils;
import com.brandedCompany.validator.EmployeeLoginValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Optional;


@Controller
@RequestMapping("/employee")
public class EmployeeLoginOutController
{
    final Logger logger = LoggerFactory.getLogger(EmployeeLoginOutController.class);

    private final EmployeeLoginValidator validator;
    private final EmployeeAuthenticationManager authManager;
    private final EmployeeConcurrentConnectionManager connectionManager;
    private final EmployeeControllerUtils utils;
private final CRUDService service;
    private final EmployeeConcurrentConnectionObserver observer;


    @Autowired
    public EmployeeLoginOutController(EmployeeLoginValidator validator, EmployeeControllerUtils utils, EmployeeAuthenticationManager authManager, EmployeeConcurrentConnectionManager connectionManager,
                                      @Qualifier("CRUDServiceImpl")
                                          CRUDService service, EmployeeConcurrentConnectionObserver observer)
    {
        this.validator = validator;
        this.utils = utils;
        this.authManager = authManager;
        this.connectionManager = connectionManager;
        this.service = service;
        this.observer = observer;

    }


    @InitBinder
    private void initBinder(WebDataBinder binder)
    {
        binder.setValidator(validator);
    }


    // 로그인 화면보기
    @GetMapping("/loginOut/login")
    public String login(HttpSession session)
    {
        logger.info("직원 로그인 페이지 요청");
        Optional<Object> opt = Optional.ofNullable(session.getAttribute("employee"));
        if (opt.isPresent())
        {
            Employee employee= (Employee) opt.get();
            BigInteger employeeId = employee.getEmployeeId();
            if (authManager.hasAuthentication(employeeId))
            {
                logger.info("["+ employeeId +" 접속중");
                return "redirect:/employee/" + employeeId + "/myPage";
            }

        }
            return "employeeLogin";

    }

    // 로그인 요청
    @PostMapping("/loginOut/login")
    public String login(@Valid Employee employee, BindingResult result, boolean rememberId, HttpServletRequest request, HttpServletResponse response) throws AuthenticationRuntimeException, ConcurrentConnectionRuntimeException, Exception
    {
        //        LoginValidator 검증
        if (result.hasErrors())
        {
            logger.error("employeeLoginValidator has error");
            throw new EmployeeNotFoundException(utils.findLastErrorCode(result), employee);
        }

        // 이미 접속 상태라면 ( = 동시 접속)
        BigInteger employeeId = employee.getEmployeeId();
        if (authManager.hasAuthentication(employeeId))
            observer.update(authManager.getAuthentication(employeeId));
        else
            authManager.addAuthentication(new EmployeeAuthentication(employee));

        HttpSession session = request.getSession();
        employee=(Employee) service.select(DomainUtils.TABLE.EMPLOYEES, employeeId);
        session.setAttribute("employee", employee);
        utils.checkRememberId(employee,rememberId,request,response);
        logger.info("[" + employeeId + "] 로그인 요청");

        return "redirect:/employee/" + employeeId + "/myPage";
    }

    //    로그 아웃
    @GetMapping("/{employeeId}/loginOut/logout")
    public String logout(@PathVariable("employeeId") BigInteger employeeId,
                         HttpServletRequest request) throws AuthenticationRuntimeException, ConCurrentConnectionNotFoundException
    {
        String log = "[" + employeeId + "] 로그아웃";
        try
        {
            if (!authManager.hasAuthentication(employeeId))
                throw new AuthenticationNotFoundException("[" + employeeId + "]not found authentication");

            // 직원인증
            Authentication authentication = authManager.getAuthentication(employeeId);

            // 직원 데이터 삭제
            request.getSession().removeAttribute("employee");

            // 동시접속 이력 삭제
            if (connectionManager.has(authentication))
                connectionManager.remove(authentication);

            // 인증삭제
            authManager.removeAuthentication(authentication);
            logger.info(log);

        }
        catch (Exception | ConcurrentConnectionRuntimeException e)
        {
            //   e.printStackTrace();
            logger.error(log);
        }

        return "redirect:/employee/loginOut/login";
    }


}
