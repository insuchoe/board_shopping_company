package com.brandedCompany.controller.customer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class CustomerIndexController
{
    @GetMapping
    public String goLoginPage()
    {
        return "redirect:/customer/loginOut/login";
    }
}
