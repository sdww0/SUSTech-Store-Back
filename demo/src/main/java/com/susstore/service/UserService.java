package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.UserMapper;
import com.susstore.pojo.Users;
import com.susstore.util.ImageUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.susstore.config.Constants.RANDOM_STRING_SIZE;
import static com.susstore.util.CommonUtil.isInteger;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public List<Users> queryUserList(){
        return userMapper.queryUserList();
    }

    public Users queryUserById(int id){
        return userMapper.queryUserById(id);
    }

    public Integer addUser(Users user){
        return userMapper.addUser(user);
    }


    public boolean updateUserWithPhoto(MultipartFile photo,Users users){
        Integer id = userMapper.queryUserByEmail(users.getEmail());
        String path = Constants.USER_UPLOAD_PATH + id + "/image/"+ RandomStringUtils.random(RANDOM_STRING_SIZE)+".png";
        users.setPicturePath(path);
        if (!photo.isEmpty()) {
            //获取文件的名称
            final String fileName = photo.getOriginalFilename();
            //限制文件上传的类型
            String contentType = fileName.substring(fileName.lastIndexOf("."));
            if (contentType.length() == 0) {
                return false;
            }
            if (".jpeg".equals(contentType) || ".jpg".equals(contentType) || ".png".equals(contentType)) {
                //完成文件的上传
                BufferedImage srcImage = null;
                try {
                    FileInputStream in = (FileInputStream) photo.getInputStream();
                    srcImage = javax.imageio.ImageIO.read(in);
                    ImageUtil.zoomImage(srcImage, path, 100, 100);
                } catch (Exception e) {
                    System.out.println("读取图片文件出错！" + e.getMessage());
                    e.printStackTrace();
                }
            }else{
                return false;
            }
        }
        userMapper.updateUser(users);
        return true;
    }


    public int deleteUser(int id){
        return userMapper.deleteUser(id);
    }



    public Integer queryUserByEmail(String email){
        return userMapper.queryUserByEmail(email);
    }



    public Users getUserByEmail(String email){
        return userMapper.getUserByEmail(email);
    }


    public Integer updateUserEmail(String oldEmail,String newEmail){
        return userMapper.updateUserEmail(oldEmail,newEmail);
    }


    public Integer updateUserByEmail(Users users){
        return userMapper.updateUserByEmail(users);
    }

    public Integer getActivateUser(String activateCode){
        return userMapper.getActivateUser(activateCode);
    }

    public Integer activateUser(Integer userId){
        return userMapper.activateUser(userId);
    }


    /**
     * 根据邮箱获取验证码
     * @param email 邮箱
     * @return 验证码
     */
    public Integer getUserCheckCodeByEmail(String email){
        return userMapper.getUserCheckCodeByEmail(email);
    }

    /**
     * 根据用户邮箱清除用户的验证码(设为-1)
     * @param email 邮箱
     * @return --
     */

    public Integer clearUserCheckCodeByEmail(String email){
        return userMapper.clearUserCheckCodeByEmail(email);
    }


}
