package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.UsersMapper;
import com.susstore.pojo.Users;
import com.susstore.pojo.UsersComment;
import com.susstore.util.CommonUtil;
import com.susstore.util.ImageUtil;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.susstore.config.Constants.*;

@Service
public class UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private MailServiceThread mailService;


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
        String random = UUID.randomUUID().toString();
        String path = Constants.USER_UPLOAD_PATH + id + "/image/"+ random+".png";
        users.setPicturePath("user/"+id+"/image/"+random+".png");
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
        users.setUserId(id);
        usersMapper.updateUser(users);
        return true;
    }

    public boolean addUserComplain(Integer userId,String content,MultipartFile picture,Integer complainerId) {
        String random = UUID.randomUUID().toString();
        String path = USER_COMPLAIN_PATH + complainerId + "/image/" + random + ".png";
        if (!picture.isEmpty()) {
            //获取文件的名称
            final String fileName = picture.getOriginalFilename();
            //限制文件上传的类型
            String contentType = fileName.substring(fileName.lastIndexOf("."));
            if (contentType.length() == 0) {
                return false;
            }
            if (".jpeg".equals(contentType) || ".jpg".equals(contentType) || ".png".equals(contentType)) {
                //完成文件的上传
                BufferedImage srcImage = null;
                try {
                    FileInputStream in = (FileInputStream) picture.getInputStream();
                    srcImage = ImageIO.read(in);
                    ImageUtil.storeImage(srcImage, path);
                } catch (Exception e) {
                    System.out.println("读取图片文件出错！" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        }
        mailService.sendAttachmentMail(Constants.COMPLAIN_HANDING_EMAIL,"您有一份新的举报信息待处理！",
                "\n举报内容:"+content
                        +"\n 举报人："+complainerId
                        +"\n 被举报人："+userId+"\n 举报图片见附件.",path);

        usersMapper.addUserComplain(userId, content, path, complainerId);
        return true;
    }

    public int deleteUser(int id){
        return usersMapper.deleteUser(id);
    }

    public Users ifExistById(int id){
        return usersMapper.ifExistById(id);
    }

    public Boolean ifActivatedById(int id){
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

    public List<UsersComment> getUsersComment(Integer userId){
        return usersMapper.getUsersComment(userId);
    }

    public Integer changeUserCheckCodeById(Integer userId,Integer checkCode){
        return usersMapper.changeUserCheckCodeById(checkCode,userId);
    }

    public Integer deactivateUsers(Integer userId,String newEmail){
        String activateCode = newEmail+ CommonUtil.getRandomString(RANDOM_STRING_SIZE);
        mailService.sendSimpleMail(newEmail,"请激活你的账户",
                "激活链接:"+Constants.WEBSITE_LINK+"/user/activate?activateCode="+activateCode);
        return usersMapper.deactivateUsers(userId,activateCode);
    }

}
