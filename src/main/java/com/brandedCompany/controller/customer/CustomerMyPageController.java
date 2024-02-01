package com.brandedCompany.controller.customer;

import com.brandedCompany.domain.Customer;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.util.DomainUtils;
import com.brandedCompany.util.ImageUtils;
import jdk.jshell.execution.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;

import static com.brandedCompany.util.DomainUtils.TABLE.CUSTOMERS;

@Controller
@RequestMapping("/customer/{customerId}/myPage")
public class CustomerMyPageController {
    PagingAndSortingService service;
    private final Logger logger = LoggerFactory.getLogger(CustomerMyPageController.class);
    private final ImageUtils imageUtils;

    @Autowired
    public CustomerMyPageController(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service, ImageUtils imageUtils) throws NullPointerException {
        this.service = service;
        this.imageUtils = imageUtils;
    }
    @PostMapping("/uploadImg")
    public String updateImg(
        @PathVariable("customerId")
            BigInteger customerId, MultipartFile uploadFile,RedirectAttributes rattr ,HttpSession session) throws IOException
    {
        imageUtils.uploadImage(CUSTOMERS, customerId,uploadFile,rattr);
        String log = "[" + customerId + "] go to myPage.";
        logger.info(log);
        String base64ImageCode = imageUtils.getBase64ImageCode(CUSTOMERS, customerId);
        Customer customer = (Customer) session.getAttribute("customer");
        customer.setImageBase64(base64ImageCode);
        return "redirect:/customer/" + customerId + "/myPage";
    }
    @PostMapping("/removeImg")
    public String removeImg(@PathVariable("customerId") BigInteger customerId, HttpSession session) throws InterruptedException, IOException
    {
        if (imageUtils.removeImg(CUSTOMERS, customerId))
        {
            Customer customer = (Customer) session.getAttribute("customer");
            customer.setImageBase64(imageUtils.getNoImageCode(CUSTOMERS));
        }
        return "redirect:/customer/"+customerId+"/myPage";
    }

    @GetMapping
    public String goMyPage(@PathVariable BigInteger customerId,HttpSession session,Model model) throws IOException
    {
        Customer customer = (Customer) session.getAttribute("customer");
        String base64ImageCode = imageUtils.getBase64ImageCode(CUSTOMERS, customerId);
        customer.setImageBase64(base64ImageCode);
        String log = "[" + customerId + "] go to myPage.";
        logger.info(log);
        model.addAttribute("noImg",imageUtils.getNoImageCode(CUSTOMERS));
        return "customerMyPage";

    }


}
