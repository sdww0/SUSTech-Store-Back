package com.susstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 邮件服务，采用了多线程，不阻塞原来
 */
@Component
public class MailServiceThread {

    @Autowired
    private MailService mailService;

    private ExecutorService executorService = Executors.newFixedThreadPool(20);


    public void sendSimpleMail(String to, String subject, String content) {
        executorService.submit(() -> mailService.sendSimpleMail(to,subject,content));
    }


    public void sendHtmlMail(String to, String subject, String content) {
        executorService.submit(() -> mailService.sendHtmlMail(to,subject,content));

    }


    public void sendAttachmentMail(String to, String subject, String content, String filePath) {
        executorService.submit(() -> mailService.sendAttachmentMail(to,subject,content,filePath));
    }


    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
        executorService.submit(() -> mailService.sendInlineResourceMail(to,subject,content,rscPath,rscId));

    }


}
