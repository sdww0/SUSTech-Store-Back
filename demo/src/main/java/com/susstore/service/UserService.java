package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.pojo.*;
import com.susstore.util.CommonUtil;
import com.susstore.util.ImageUtil;
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

@Service
public interface UserService {

    public List<Users> queryUserList();

    public Users queryUserById(int id);

    public Integer addUser(Users user);


    public boolean updateUserWithPhoto(MultipartFile photo, Users users);

    public boolean addUserComplain(Integer userId,String content,MultipartFile picture,Integer complainerId);

    public int deleteUser(int id);

    public Users ifExistById(int id);

    public Boolean ifActivatedById(int id);

    public Integer queryUserIdByEmail(String email);



    public Users getUserByEmail(String email);


    public Integer updateUserEmail(String oldEmail,String newEmail);


    public Integer updateUserByEmail(Users users);

    public Integer updateUserById(Users users);

    public Integer getActivateUser(String activateCode);
    public Integer activateUser(Integer userId);


    public Integer getUserCheckCodeByEmail(String email);


    public Integer getUserCheckCodeById(Integer id);


    public Integer clearUserCheckCodeByEmail(String email);

    public Float getUserMoney(Integer userId);

    public Integer changeUserMoney(Integer userId, Float delta, HttpServletRequest request, Integer dealId);

    public List<Consume> getConsumeList(Integer userId);

    public Boolean checkUserHasInputAddress(Integer userId,Integer addressId);

    public List<Users> searchUsers(String userName, int pageSize, int pageIndex);

    public List<UsersComment> getUsersComment(Integer userId);

    public Integer changeUserCheckCodeById(Integer userId,Integer checkCode);

    public Integer deactivateUsers(Integer userId,String newEmail);

    public List<Integer> getUserRole(String email);

    public List<GoodsAbbreviation> getUsersCollection(Integer userId);

    public Integer addCollection(Integer userId,Integer goodsId);

    public Integer deleteCollection(Integer userId,Integer goodsId);
    public      Boolean isInUserCollection(Integer goodsId,String email);

    public Integer searchUsersAmount(String userName) ;

    public Integer getUserCredit(String email);

    public Integer addCharge(Integer userId, Float money, HttpServletRequest request);
    public      Integer changeUserCredit(Integer userId,Integer change);

    public Charge getChargeUser(Integer chargeId,Integer chargeUserId);

    public Charge getCharge(Integer chargeId);

    public List<Charge> getChargeByUser(Integer userId);

    public Boolean isCharge(Integer chargeId,Integer chargeUserId);

    public Integer setCharge(Integer chargeId);

    public String getUserEmail(Integer userId);

    public Users getUserNameAndPictureById(Integer userId);







}
