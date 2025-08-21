package com.recouvtech.recouvback.service;

// Importing required classes
import java.io.File;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
import com.recouvtech.recouvback.entity.EmailDetails;
import com.recouvtech.recouvback.entity.Relance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// Annotation
@Service
// Class
// Implementing EmailService interface
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    /**
     * Get client email from the relance RECENTLY ADDED
     */
    private String getClientEmail(Relance relance) {
        if (relance.getCreance() != null &&
                relance.getCreance().getClient() != null) {
            return relance.getCreance().getClient().getEmail();
        }
        return null;
    }/**  RECENTLY ADDED*/

    // Method 1
    // To send a simple email
    public String sendSimpleMail(EmailDetails details)
    {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    // Method 2
    // To send reminder emails
    public String envoyerRelance(Relance relance)
    {
        try {
/**  RECENTLY ADDED*/
            // Get client email - throw exception if not available
            String clientEmail = getClientEmail(relance);
            if (clientEmail == null || clientEmail.trim().isEmpty()) {
                throw new RuntimeException("Email du client non disponible pour la facture: " +
                        relance.getCreance().getNumFacture());
            }
/**  RECENTLY ADDED*/
            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(clientEmail);
            mailMessage.setText(relance.getMessage());
            mailMessage.setSubject("Relance - Facture N°" + relance.getCreance().getNumFacture());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Relance envoyée avec succès";
        }
        catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Erreur détaillée lors de l'envoi de la relance: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de la relance: " + e.getMessage(), e);
        }
    }
}