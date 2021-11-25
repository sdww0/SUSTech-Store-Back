package com.susstore.service.impl;


import com.susstore.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
@Slf4j
@Component
public class MailServiceImpl implements MailService {
    
    @Value("${spring.mail.username}")
    //使用@Value注入application.properties中指定的用户名
    private String from;

    @Autowired
    private JavaMailSenderImpl mailSender;


    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);//收信人
        message.setSubject(subject);//主题
        message.setText(content);//内容
        message.setFrom(from);//发信人

        mailSender.send(message);
    }


    public void sendHtmlMail(String to, String subject, String content) {

        log.info("发送HTML邮件开始：{},{},{}", to, subject, content);
        //使用MimeMessage，MIME协议
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        //MimeMessageHelper帮助我们设置更丰富的内容
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);//true代表支持html
            mailSender.send(message);
            log.info("发送HTML邮件成功");
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败：", e);
        }
    }

    public void sendAttachmentMailWithFile(String to, String subject, String content, File file1) {

        log.info("发送带附件邮件开始：{},{},{},{}", to, subject, content, file1);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            //true代表支持多组件，如附件，图片等
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource file = new FileSystemResource(file1);
            String fileName = file.getFilename();
            helper.addAttachment(fileName, file);//添加附件，可多次调用该方法添加多个附件
            mailSender.send(message);
            log.info("发送带附件邮件成功");
        } catch (MessagingException e) {
            log.error("发送带附件邮件失败", e);
        }


    }


    public void sendAttachmentMail(String to, String subject, String content, String filePath) {

        log.info("发送带附件邮件开始：{},{},{},{}", to, subject, content, filePath);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            //true代表支持多组件，如附件，图片等
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            helper.addAttachment(fileName, file);//添加附件，可多次调用该方法添加多个附件
            mailSender.send(message);
            log.info("发送带附件邮件成功");
        } catch (MessagingException e) {
            log.error("发送带附件邮件失败", e);
        }


    }


    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {

        log.info("发送带图片邮件开始：{},{},{},{},{}", to, subject, content, rscPath, rscId);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);//重复使用添加多个图片
            mailSender.send(message);
            log.info("发送带图片邮件成功");
        } catch (MessagingException e) {
            log.error("发送带图片邮件失败", e);
        }
    }




}
