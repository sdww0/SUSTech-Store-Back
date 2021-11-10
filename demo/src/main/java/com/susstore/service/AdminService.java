package com.susstore.service;

import com.susstore.mapper.AdminMapper;
import com.susstore.pojo.GoodsState;
import com.susstore.pojo.process.AppealingDeal;
import com.susstore.pojo.process.ComplainGoods;
import com.susstore.pojo.process.ComplainUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public List<ComplainUsers> getAllNotProcessComplainUser(){
        return adminMapper.getAllNotProcessComplainUser();
    }

    public List<ComplainUsers> getAllComplainUser(){
        return adminMapper.getAllComplainUser();
    }

    public List<ComplainGoods> getAllNotProcessComplainGoods(){
        return adminMapper.getAllNotProcessComplainGoods();
    }


    public List<ComplainGoods> getAllComplainGoods(){
        return adminMapper.getAllComplainGoods();
    }

    public List<AppealingDeal> getAllNotProcessAppealingDeal(){
        return adminMapper.getAllNotProcessAppealingDeal();
    }

    public List<AppealingDeal> getAllAppealingDeal(){
        return adminMapper.getAllAppealingDeal();
    }

    public ComplainUsers getComplainUser(Integer recordId){
        return adminMapper.getComplainUser(recordId);
    }

    public ComplainGoods getComplainGoods(Integer recordId){
        return adminMapper.getComplainGoods(recordId);
    }

    public AppealingDeal getAppealingDeal(Integer recordId){
        return adminMapper.getAppealingDeal(recordId);
    }

    public Integer processComplainGoods(Integer recordId){
        return adminMapper.processComplainGoods(recordId);
    }

    public Integer processComplainUsers(Integer recordId){
        return adminMapper.processComplainUsers(recordId);
    }

    public Integer processAppealingDeal(Integer recordId){
        return adminMapper.processAppealingDeal(recordId);
    }

    public Integer banGoods(Integer goodsId){
        return adminMapper.banGoods(GoodsState.BANNED.ordinal(),goodsId);
    }

    public Integer banUser(Integer userId){
        return adminMapper.banUser(userId);
    }

    public Integer updateDealState(Integer stage,Integer dealId){
        return adminMapper.updateDealState(stage,dealId);
    }


}