package com.susstore.mapper;

import com.susstore.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UsersMapper {

     /**
      * 查询所有用户
      * @return 用户列表
      */
     List<Users> queryUserList();

     /**
      * 根据用户id查询用户
      * @param id id
      * @return 用户
      */
     Users queryUserById(int id);

     /**
      * 添加用户
      * @param user 用户
      * @return 用户id
      */
     Integer addUser(Users user);

     /**
      * 更新用户数据
      * @param user 用户
      * @return --
      */
     int updateUser(Users user);

     /**
      * 更新用户邮箱
      * @param oldEmail 旧邮箱
      * @param newEmail 新邮箱
      * @return --
      */
     Integer updateUserEmail(String oldEmail,String newEmail);

     /**
      * 根据用户邮箱更新用户（id为null）
      * @param users 用户
      * @return --
      */
     Integer updateUserByEmail(Users users);

     /**
      * 根据用户id更新用户
      * @param users 用户
      * @return --
      */
     Integer updateUserById(Users users);

     /**
      * 根据id删除用户
      * @param id id
      * @return --
      */
     int deleteUser(int id);


     Users ifExistById(int userId);

     Boolean ifActivatedById(int id);
     /**
      * 根据邮箱查询用户id
      * @param email 邮箱
      * @return 用户id
      */
     Integer queryUserIdByEmail(String email);

     /**
      * 根据用户邮箱查询用户
      * @param email 邮箱
      * @return 用户
      */
     Users getUserByEmail(String email);

     /**
      * 获得用户的验证码，根据用户id
      * @param id id
      * @return 验证码
      */
     Integer getUserCheckCodeById(Integer id);

     /**
      * 根据邮箱获取验证码
      * @param email 邮箱
      * @return 验证码
      */
     Integer getUserCheckCodeByEmail(String email);

     /**
      * 根据用户邮箱清除用户的验证码(设为-1)
      * @param email 邮箱
      * @return --
      */

     Integer clearUserCheckCodeByEmail(String email);

     Integer changeUserCheckCodeById(Integer checkCode,Integer userId);

     Integer deactivateUsers(Integer userId,String activateCode);

     /**
      * 根据用户id清除用户的验证码(设为-1)
      * @param id id
      * @return --
      */
     Integer clearUserCheckCodeById(String id);

     /**
      * 根据激活码激活用户，如果用户为已激活则返回-2,如果不存在则返回-1,如果未激活且存在则返回用户id
      * @param activateCode 激活码
      * @return 状态
      */
     Integer getActivateUser(String activateCode);

     /**
      * 激活用户，将未激活false改成激活true
      * @param userId 用户id
      * @return --
      */
     Integer activateUser(Integer userId);

     Float getUserMoney(Integer userId);

     Integer changeUserMoney(Integer userId,Float delta);

     List<Users> searchUsers(Map<String,Object> map);

     Boolean checkUserHasInputAddress(Integer userId,Integer addressId);

     Integer changeUserCredit(Integer userId,Integer change);

     List<UsersComment> getUsersComment(Integer userId);

     Integer addUserComplain(Integer userId, String content, String picturePath, Integer complainerId);

     List<Integer> getUserRole(String email);

     Integer registerRole(Integer userId,Integer role);

     List<GoodsAbbreviation> getUsersCollection(Integer userId);

     Integer addCollection(Integer userId,Integer goodsId);

     Integer deleteCollection(Integer userId,Integer goodsId);

     Boolean isInUserCollection(Integer goodsId,String email);

     List<UsersLabel> getUserVisitedLabels(Integer userId);

    Integer searchUsersAmount(String userName);

    Integer getUserCredit(String email);

    Integer addNewCharge(Charge charge);

    Charge getChargeUser(Integer chargeId,Integer chargeUserId);

    Charge getCharge(Integer chargeId);

    Boolean isCharge(Integer chargeId,Integer chargeUserId);

    Integer setCharge(Integer chargeId, Date date);

    List<Charge> getChargeByUser(Integer userId);

    String getUserEmail(Integer userId);

    Users getUserNameAndPictureById(Integer userId);
}
