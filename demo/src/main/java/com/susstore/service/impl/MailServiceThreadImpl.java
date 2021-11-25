package com.susstore.service.impl;

import com.susstore.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 邮件服务，采用了多线程，不阻塞原来
 */
@Service("MailServiceThreadImpl")
public class MailServiceThreadImpl implements MailService {

    @Autowired
    private MailServiceImpl mailService;

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


    public void sendAttachmentMailWithFile(String to, String subject, String content, File file) {
        executorService.submit(() -> mailService.sendAttachmentMailWithFile(to,subject,content,file));
    }

    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
        executorService.submit(() -> mailService.sendInlineResourceMail(to,subject,content,rscPath,rscId));

    }


}
