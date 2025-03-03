package com.example.Project_Online_market.service;
import  com.example.Project_Online_market.Model.EmailDetails;



public interface EmailService {
    
    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
