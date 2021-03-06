package com.susstore.service.impl;

import com.susstore.config.Constants;
import com.susstore.mapper.UsersMapper;
import com.susstore.pojo.*;
import com.susstore.service.MailService;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import com.susstore.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.susstore.config.Constants.*;

@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    @Qualifier("MailServiceThreadImpl")
    private MailService mailService;



    public List<Users> queryUserList(){
        return usersMapper.queryUserList();
    }

    public Users queryUserById(int id){
        return usersMapper.queryUserById(id);
    }

    public Integer addUser(Users user){
        user.setPicturePath(BACK_END_LINK+"user/user_picture_default.png");

        usersMapper.addUser(user);
        usersMapper.registerRole(user.getUserId(), Role.USER.ordinal());
        if(user.getEmail().endsWith("edu.cn")){
            usersMapper.changeUserCredit(user.getUserId(),Constants.SCHOOL_EMAIL_ADD_CREDIT);
        }
        return user.getUserId();
    }


    public boolean updateUserWithPhoto(MultipartFile photo,Users users){
        Integer id = usersMapper.queryUserIdByEmail(users.getEmail());
        String random = UUID.randomUUID().toString();
        String path = Constants.USER_UPLOAD_PATH + id + "/image/"+ random+".png";
        users.setPicturePath(BACK_END_LINK+"user/"+id+"/image/"+random+".png");
        if (!photo.isEmpty()) {
            //?????????????????????
            final String fileName = photo.getOriginalFilename();
            //???????????????????????????
            String contentType = fileName.substring(fileName.lastIndexOf("."));
            if (contentType.length() == 0) {
                return false;
            }
            if (".jpeg".equals(contentType) || ".jpg".equals(contentType) || ".png".equals(contentType)) {
                //?????????????????????
                BufferedImage srcImage = null;
                try {
                    FileInputStream in = (FileInputStream) photo.getInputStream();
                    srcImage = javax.imageio.ImageIO.read(in);
                    ImageUtil.zoomImage(srcImage, path, 100, 100);
                } catch (Exception e) {
                    System.out.println("???????????????????????????" + e.getMessage());
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
        String path = USER_COMPLAIN_PATH + complainerId + "/" + random + ".png";
        if (!picture.isEmpty()) {
            //?????????????????????
            final String fileName = picture.getOriginalFilename();
            //???????????????????????????
            String contentType = fileName.substring(fileName.lastIndexOf("."));
            if (contentType.length() == 0) {
                return false;
            }
            if (".jpeg".equals(contentType) || ".jpg".equals(contentType) || ".png".equals(contentType)) {
                //?????????????????????
                BufferedImage srcImage = null;
                try {
                    FileInputStream in = (FileInputStream) picture.getInputStream();
                    srcImage = ImageIO.read(in);
                    ImageUtil.storeImage(srcImage, path);
                } catch (Exception e) {
                    System.out.println("???????????????????????????" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        }
        mailService.sendAttachmentMail(Constants.COMPLAIN_HANDING_EMAIL,"??????????????????????????????????????????",
                "\n????????????:"+content
                        +"\n ????????????"+complainerId
                        +"\n ???????????????"+userId+"\n ?????????????????????.",path);
        path = BACK_END_LINK+"user/complain/"+ complainerId + "/" + random + ".png";
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

    public Integer queryUserIdByEmail(String email){
        return usersMapper.queryUserIdByEmail(email);
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
     * ???????????????????????????
     * @param email ??????
     * @return ?????????
     */
    public Integer getUserCheckCodeByEmail(String email){
        return usersMapper.getUserCheckCodeByEmail(email);
    }

    /**
     * ???????????????????????????
     * @param id ??????id
     * @return ?????????
     */
    public Integer getUserCheckCodeById(Integer id){
        return usersMapper.getUserCheckCodeById(id);
    }

    /**
     * ??????????????????????????????????????????(??????-1)
     * @param email ??????
     * @return --
     */

    public Integer clearUserCheckCodeByEmail(String email){
        return usersMapper.clearUserCheckCodeByEmail(email);
    }

    public Float getUserMoney(Integer userId){
        return  usersMapper.getUserMoney(userId);
    }

    public Integer changeUserMoney(Integer userId,Float delta,HttpServletRequest request,Integer dealId){
        if(delta>=0){
            if(request==null) {
                if(dealId!=null) {
                    usersMapper.addNewCharge(
                            Charge.builder().
                                    isCharge(true).
                                    chargeDate(new Date())
                                    .addDealDate(new Date())
                                    .ipAddress("-1").chargeUserId(userId).money(delta).build());
                }else{
                    usersMapper.addNewCharge(
                            Charge.builder().
                                    isCharge(true).
                                    chargeDate(new Date())
                                    .addDealDate(new Date())
                                    .ipAddress("-2").chargeUserId(userId).money(delta).build());
                }
            }
        }else{
            usersMapper.addNewConsume(
                    Consume.builder().consumeDate(new Date())
                            .money(-delta)
                            .ipAddress(CommonUtil.getIpAddress(request))
                            .belongUserId(userId)
                            .relatedDealId(dealId).build()

            );
        }
        return usersMapper.changeUserMoney(userId,delta);
    }

    public List<Consume> getConsumeList(Integer userId){
        return usersMapper.getConsumeList(userId);
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
        mailService.sendSimpleMail(newEmail,"?????????????????????",
                "????????????:"+Constants.WEBSITE_LINK+"/user/activate?activateCode="+activateCode);
        return usersMapper.deactivateUsers(userId,activateCode);
    }

    public List<Integer> getUserRole(String email){
        return usersMapper.getUserRole(email);
    }

    public List<GoodsAbbreviation> getUsersCollection(Integer userId){
        return usersMapper.getUsersCollection(userId);
    }

    public Integer addCollection(Integer userId,Integer goodsId){
        return usersMapper.addCollection(userId,goodsId);
    }

    public Integer deleteCollection(Integer userId,Integer goodsId){
        return usersMapper.deleteCollection(userId,goodsId);
    }

    public      Boolean isInUserCollection(Integer goodsId,String email){
        return usersMapper.isInUserCollection(goodsId, email) != null;
    }

    public Integer searchUsersAmount(String userName) {
        return usersMapper.searchUsersAmount(userName);
    }

    public Integer getUserCredit(String email){
        return usersMapper.getUserCredit(email);
    }

    public Integer addCharge(Integer userId, Float money, HttpServletRequest request){
        Charge charge = Charge.builder()
                .chargeUserId(userId)
                .money(money)
                .ipAddress(CommonUtil.getIpAddress(request))
                .chargeDate(null)
                .addDealDate(new Date())
                .isCharge(false)
                .build();
        usersMapper.addNewCharge(charge);
        return charge.getChargeDealId();
    }

    public      Integer changeUserCredit(Integer userId,Integer change){
        return usersMapper.changeUserCredit(userId,change);
    }

    public Charge getChargeUser(Integer chargeId,Integer chargeUserId){
        return usersMapper.getChargeUser(chargeId,chargeUserId);
    }

    public Charge getCharge(Integer chargeId){
        return usersMapper.getCharge(chargeId);
    }

    public List<Charge> getChargeByUser(Integer userId){
        return usersMapper.getChargeByUser(userId);
    }

    public Boolean isCharge(Integer chargeId,Integer chargeUserId){
        return usersMapper.isCharge(chargeId,chargeUserId);
    }

    public Integer setCharge(Integer chargeId){

        return usersMapper.setCharge(chargeId,new Date());
    }

    public String getUserEmail(Integer userId){
        return usersMapper.getUserEmail(userId);
    }

    public Users getUserNameAndPictureById(Integer userId){
        return usersMapper.getUserNameAndPictureById(userId);
    }

}
