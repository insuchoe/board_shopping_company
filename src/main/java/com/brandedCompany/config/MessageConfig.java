package com.brandedCompany.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfig {
    @Bean
    public MessageSource messageSource()
    {
        ReloadableResourceBundleMessageSource messageSource=
                new ReloadableResourceBundleMessageSource();
//        messageSource.setBasename("classpath:/messages");
        messageSource.setBasenames( new String[]{
            "classpath:/messages/customer_cart",
            "classpath:/messages/customer_error",
            "classpath:/messages/customer_loginOut",
            "classpath:/messages/customer_myPage",
            "classpath:/messages/customer_order",
            "classpath:/messages/customer_orderHistory",
            "classpath:/messages/customer_product",
            "classpath:/messages/employee_board",
            "classpath:/messages/employee_error",
            "classpath:/messages/employee_loginOut",
            "classpath:/messages/employee_myPage",
            "classpath:/messages/employee_orderHistory",
            "classpath:/messages/employee_product"});
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


    @Bean
    public MessageSourceAccessor messageSourceAccessor()
    {
        return  new MessageSourceAccessor(messageSource());
    }


}
