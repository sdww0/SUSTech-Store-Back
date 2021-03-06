package com.susstore.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import java.io.File;


public class ImageUtil {

    public static void zoomImage(BufferedImage bufImg,String dest,int w,int h) throws Exception {

        double wr=0,hr=0;
        File destFile = new File(dest);
        if (destFile.exists()){
            destFile.delete(); // here i'm checking if file exists and if yes then i'm deleting it but its not working
        }

        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void storeImage(MultipartFile multipartFile, String dest){
        File destFile = new File(dest);
        destFile.mkdirs();
        if (destFile.exists()){
            destFile.delete(); // here i'm checking if file exists and if yes then i'm deleting it but its not working
        }
        try {
            ImageIO.write(ImageIO.read(multipartFile.getInputStream()),dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void storeImage(BufferedImage bufferedImage,String dest){
        File destFile = new File(dest);
        destFile.mkdirs();
        if (destFile.exists()){
            destFile.delete(); // here i'm checking if file exists and if yes then i'm deleting it but its not working
        }
        try {
            ImageIO.write(bufferedImage,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
