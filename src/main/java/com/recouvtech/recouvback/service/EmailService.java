package com.recouvtech.recouvback.service;

// Importing required classes
import com.recouvtech.recouvback.entity.EmailDetails;
import com.recouvtech.recouvback.entity.Relance;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);
    
    // Method to send reminder emails
    String envoyerRelance(Relance relance);
}