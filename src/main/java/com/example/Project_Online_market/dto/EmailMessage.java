package com.example.Project_Online_market.dto;

import java.io.Serializable;

public class EmailMessage implements Serializable {
    private String recipient;
    private String subject;
    private String content;
    
    // Default constructor required for Jackson
    public EmailMessage() {}
    
    public EmailMessage(String recipient, String subject, String content) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }
    
    // Getters and setters
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @Override
    public String toString() {
        return "EmailMessage [recipient=" + recipient + ", subject=" + subject + "]";
    }
}
