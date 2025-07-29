package com.recouvtech.recouvback.service;

// Importing required classes
import com.recouvtech.recouvback.entity.EmailDetails;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    }