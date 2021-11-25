package com.susstore.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;

@Component
public interface MailService {

    public void sendSimpleMail(String to, String subject, String content) ;

    public void sendHtmlMail(String to, String subject, String content);

    public void sendAttachmentMail(String to, String subject, String content, String filePath) ;

    public void sendAttachmentMailWithFile(String to, String subject, String content, File file) ;

    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId);




}
