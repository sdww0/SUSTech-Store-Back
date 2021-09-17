package com.susstore.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Mapper
@Repository
public interface BillingAddressMapper {

    Integer add(Map<String,Object> parameterMap);

}