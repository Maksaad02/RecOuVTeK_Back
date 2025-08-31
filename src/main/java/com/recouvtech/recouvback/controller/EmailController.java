package com.recouvtech.recouvback.controller;

// Importing required classes
import com.recouvtech.recouvback.entity.EmailDetails;
import com.recouvtech.recouvback.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Annotation
@RestController
@RequestMapping("/api")

// Class
public class EmailController {

    @Autowired private EmailService emailService;

    // Sending a simple Email
    @PostMapping("/sendMail")
    public String
    sendMail(@RequestBody EmailDetails details)
    {
        String status
                = emailService.sendSimpleMail(details);

        return status;
    }


}