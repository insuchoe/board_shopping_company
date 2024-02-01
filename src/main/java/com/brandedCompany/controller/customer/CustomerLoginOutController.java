package com.brandedCompany.controller.customer;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.CustomerAuthentication;
import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationNotFoundException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.concurrentConnection.CustomerConcurrentConnectionManager;
import com.brandedCompany.concurrentConnection.CustomerConcurrentConnectionObserver;
import com.brandedCompany.concurrentConnection.exception.ConCurrentConnectionNotFoundException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.domain.Customer;
import com.brandedCompany.exception.CustomerNotFoundException;
import com.brandedCompany.serivce.CRUDService;
import com.brandedCompany.util.CustomerControllerUtils;
import com.brandedCompany.util.DomainUtils;
import com.brandedCompany.validator.CustomerLoginValidator;
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
@RequestMapping("/customer")
public class CustomerLoginOutController
{
    final Logger logger = LoggerFactory.getLogger(CustomerLoginOutController.class);

    private final CustomerLoginValidator validator;
    private final CustomerAuthenticationManager authManager;
    private final CustomerConcurrentConnectionManager connectionManager;
    private final CustomerControllerUtils utils;
    private final CustomerConcurrentConnectionObserver observer;
    private final CRUDService service;

    @Autowired
    public CustomerLoginOutController(CustomerLoginValidator validator, CustomerControllerUtils utils, CustomerAuthenticationManager authManager, CustomerConcurrentConnectionManager connectionManager, CustomerConcurrentConnectionObserver observer,
                                      @Qualifier("CRUDServiceImpl")
                                          CRUDService service)
    {
        this.validator = validator;
        this.utils = utils;
        this.authManager = authManager;
        this.connectionManager = connectionManager;
        this.observer = observer;
        this.service = service;
    }


    @InitBinder
    private void initBinder(WebDataBinder binder)
    {
        binder.setValidator(validator);
    }

    //   로그인 페이지 요청
    @GetMapping("/loginOut/login")
    public String login(HttpSession session)
    {
        logger.info("고객 로그인 페이지 요청");
        Optional<Object> opt = Optional.ofNullable(session.getAttribute("customer"));
        if (opt.isPresent())
        {
            Customer customer = (Customer) opt.get();
            BigInteger customerId = customer.getCustomerId();
            if (authManager.hasAuthentication(customerId))
            {
                logger.info("[" + customerId + "] 접속중");
                return "redirect:/customer/" + customerId + "/myPage";
            }
        }
        return "customerLogin";
    }

    // 로그인 요청
    @PostMapping("/loginOut/login")
    public String login(@Valid Customer customer, BindingResult result, boolean rememberId, HttpServletRequest request, HttpServletResponse response) throws AuthenticationRuntimeException, ConcurrentConnectionRuntimeException, Exception
    {
        //        LoginValidator 검증
        if (result.hasErrors())
        {
            logger.error("customerLoginValidator has error");
            throw new CustomerNotFoundException(utils.findLastErrorCode(result), customer);
        }

        // 이미 접속 상태라면 ( = 동시 접속)
        BigInteger customerId = customer.getCustomerId();
        if (authManager.hasAuthentication(customerId))
            observer.update(authManager.getAuthentication(customerId));
        else
            authManager.addAuthentication(new CustomerAuthentication(customer));

        HttpSession session = request.getSession();
        customer = (Customer) service.select(DomainUtils.TABLE.CUSTOMERS, customerId);
        session.setAttribute("customer", customer);
        utils.checkRememberId(customer, rememberId, request, response);
        logger.info("[" + customerId + "] 로그인 요청");
        return "redirect:/customer/" + customerId + "/myPage";
    }


    //  로그 아웃 요청
    @GetMapping("/{customerId}/loginOut/logout")
    public String logout(
        @PathVariable
            BigInteger customerId, HttpServletRequest request) throws AuthenticationRuntimeException, ConCurrentConnectionNotFoundException
    {
        String log = "[" + customerId + "] 로그아웃";
        try
        {
            if (!authManager.hasAuthentication(customerId))
                throw new AuthenticationNotFoundException("[" + customerId + "] authentication not found");

            // 고객 인증
            Authentication authentication = authManager.getAuthentication(customerId);

            //고객 데이터 삭제
            request.getSession()
                   .removeAttribute("customer");

            // 동시접속 이력삭제
            if (connectionManager.has(authentication))
                connectionManager.remove(authentication);

            // 인증 삭제
            authManager.removeAuthentication(authentication);
            logger.info(log);
        }
        catch (Exception | ConcurrentConnectionRuntimeException e)
        {
            //      e.printStackTrace();
            logger.error(log);
        }

        return "redirect:/customer/loginOut/login";
    }
}
