package com.susstore.service;

import com.susstore.pojo.Event;
import com.susstore.pojo.GoodsState;
import com.susstore.pojo.process.AppealingDeal;
import com.susstore.pojo.process.ComplainGoods;
import com.susstore.pojo.process.ComplainUsers;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface AdminService {

    public List<ComplainUsers> getAllNotProcessComplainUser();

    public List<ComplainUsers> getAllComplainUser();

    public List<ComplainGoods> getAllNotProcessComplainGoods();


    public List<ComplainGoods> getAllComplainGoods();

    public List<AppealingDeal> getAllNotProcessAppealingDeal();

    public List<AppealingDeal> getAllAppealingDeal();

    public ComplainUsers getComplainUser(Integer recordId);

    public ComplainGoods getComplainGoods(Integer recordId);

    public AppealingDeal getAppealingDeal(Integer recordId);

    public Integer processComplainGoods(Integer recordId);

    public Integer processComplainUsers(Integer recordId);

    public Integer processAppealingDeal(Integer recordId);

    public Integer banGoods(Integer goodsId);

    public Integer banUser(Integer userId);

    public Integer updateDealState(Integer stage,Integer dealId);


    public Integer addEvent(Event event);

    public Integer deleteEvent(Integer eventId);

    public Event getEvent(Integer eventId);

    public List<Event> getEventWithTimeConstrain(Date minDate, Date maxDate);



}
