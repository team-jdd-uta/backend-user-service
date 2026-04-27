package com.teamuta.userinfoserver.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerRepository {
    String selectCustomerNameByUserId(@Param("userId") String userId);
}
