package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.UserMapper;
import com.susstore.pojo.Users;
import com.susstore.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public boolean updateUserWithPhoto(MultipartFile photo,String newName,String newEmail,String newPhone,int id){
        String path = Constants.USER_UPLOAD_PATH + id + "/image/";
        String picturePath = null;
        if (!photo.isEmpty()) {
            //String realPath = path.replace('/', '\\').substring(1,path.length());//linux系统再弄
            String realPath = path;
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
                    picturePath = "/user/" + id + "/image/face" + contentType;
                    ImageUtil.zoomImage(srcImage, realPath + "face" + contentType, 100, 100);
                } catch (Exception e) {
                    System.out.println("读取图片文件出错！" + e.getMessage());
                    e.printStackTrace();
                }
            }else{
                return false;
            }
        }
        Users user = new Users();
        user.setUserId(id);
        user.setUserName(newName);
        user.setEmail(newEmail);
        if (newPhone != null && isInteger(newPhone)){
            user.setPhone(Long.parseLong(newPhone));
        }
        user.setPicturePath(picturePath);
        userMapper.updateUser(user);
        return true;
    }


    public int deleteUser(int id){
        return userMapper.deleteUser(id);
    }



    public Integer queryUserByEmail(String email){
        return userMapper.queryUserByEmail(email);
    }


    public Integer updateToken(String token,Integer id){
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("userId",id);
        return userMapper.updateToken(map);
    }

}
