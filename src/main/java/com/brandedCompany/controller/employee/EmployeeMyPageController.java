package com.brandedCompany.controller.employee;

import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.CRUDService;
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

import static com.brandedCompany.util.DomainUtils.TABLE.EMPLOYEES;

@Controller
@RequestMapping("/employee/{employeeId}/myPage")
public class EmployeeMyPageController
{

    private CRUDService service;
    private final Logger logger = LoggerFactory.getLogger(EmployeeMyPageController.class);
    private final ImageUtils imageUtils;

    @Autowired

    public EmployeeMyPageController(
        @Qualifier("CRUDServiceImpl")
            CRUDService service, ImageUtils imageUtils)
    {
        this.service = service;
        this.imageUtils = imageUtils;
    }

    @PostMapping("/uploadImg")
    public String updateImg(
        @PathVariable("employeeId")
            BigInteger employeeId, MultipartFile uploadFile,RedirectAttributes rattr ,HttpSession session) throws IOException
    {
        imageUtils.uploadImage(EMPLOYEES, employeeId, uploadFile, rattr);
        String log = "[" + employeeId + "] go to myPage.";
        logger.info(log);
        String base64ImageCode = imageUtils.getBase64ImageCode(EMPLOYEES, employeeId);
        Employee employee = (Employee) session.getAttribute("employee");
        employee.setImageBase64(base64ImageCode);
        return "redirect:/employee/" + employeeId + "/myPage";
    }
    @PostMapping("/removeImg")
    public String removeImg(@PathVariable("employeeId") BigInteger employeeId,HttpSession session) throws InterruptedException, IOException
    {
        if (imageUtils.removeImg(EMPLOYEES, employeeId))
        {
            Employee employee = (Employee) session.getAttribute("employee");
            employee.setImageBase64(imageUtils.getNoImageCode(EMPLOYEES));
        }
        return "redirect:/employee/"+employeeId+"/myPage";
    }

    @GetMapping
    public String goToMyPage(
        @PathVariable
            BigInteger employeeId, HttpSession session, Model model) throws Exception
    {
        Employee employee = (Employee) session.getAttribute("employee");
        String base64ImageCode = imageUtils.getBase64ImageCode(DomainUtils.TABLE.EMPLOYEES, employeeId);
        employee.setImageBase64(base64ImageCode);
        String log = "[" + employeeId + "] go to myPage.";
        logger.info(log);
        model.addAttribute("noImg", imageUtils.getNoImageCode(EMPLOYEES));
        return "employeeMyPage";
    }


}

