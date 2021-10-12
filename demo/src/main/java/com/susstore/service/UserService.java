package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.UsersMapper;
import com.susstore.pojo.Users;
import com.susstore.util.ImageUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import static com.susstore.config.Constants.RANDOM_STRING_SIZE;
import static com.susstore.config.Constants.SEARCH_PAGE_SIZE;

@Service
public class UserService {

    @Autowired
    private UsersMapper usersMapper;


    public List<Users> queryUserList(){
        return usersMapper.queryUserList();
    }

    public Users queryUserById(int id){
        return usersMapper.queryUserById(id);
    }

    public Integer addUser(Users user){
        return usersMapper.addUser(user);
    }


    public boolean updateUserWithPhoto(MultipartFile photo,Users users){
        Integer id = usersMapper.queryUserByEmail(users.getEmail());
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
        usersMapper.updateUser(users);
        return true;
    }


    public int deleteUser(int id){
        return usersMapper.deleteUser(id);
    }

    public Users ifExistById(int id){
        return usersMapper.ifExistById(id);
    }

    public boolean ifActivatedById(int id){
        return usersMapper.ifActivatedById(id);
    }

    public Integer queryUserByEmail(String email){
        return usersMapper.queryUserByEmail(email);
    }



    public Users getUserByEmail(String email){
        return usersMapper.getUserByEmail(email);
    }


    public Integer updateUserEmail(String oldEmail,String newEmail){
        return usersMapper.updateUserEmail(oldEmail,newEmail);
    }


    public Integer updateUserByEmail(Users users){
        return usersMapper.updateUserByEmail(users);
    }

    public Integer updateUserById(Users users){
        return usersMapper.updateUserById(users);
    }

    public Integer getActivateUser(String activateCode){
        return usersMapper.getActivateUser(activateCode);
    }

    public Integer activateUser(Integer userId){
        return usersMapper.activateUser(userId);
    }


    /**
     * 根据邮箱获取验证码
     * @param email 邮箱
     * @return 验证码
     */
    public Integer getUserCheckCodeByEmail(String email){
        return usersMapper.getUserCheckCodeByEmail(email);
    }

    /**
     * 根据邮箱获取验证码
     * @param id 用户id
     * @return 验证码
     */
    public Integer getUserCheckCodeById(Integer id){
        return usersMapper.getUserCheckCodeById(id);
    }

    /**
     * 根据用户邮箱清除用户的验证码(设为-1)
     * @param email 邮箱
     * @return --
     */

    public Integer clearUserCheckCodeByEmail(String email){
        return usersMapper.clearUserCheckCodeByEmail(email);
    }

    public Float getUserMoney(Integer userId){
        return  usersMapper.getUserMoney(userId);
    }

    public Integer changeUserMoney(Integer userId,Float delta){
        return usersMapper.changeUserMoney(userId,delta);
    }

    public Boolean checkUserHasInputAddress(Integer userId,Integer addressId){
        return usersMapper.checkUserHasInputAddress(userId, addressId);
    }


    public List<Users> searchUsers(String userName, int pageSize, int pageIndex){
        return usersMapper.searchUsers(Map.of("userName",userName,"pageSize",pageSize,"pageIndex",pageIndex));
    }
}
