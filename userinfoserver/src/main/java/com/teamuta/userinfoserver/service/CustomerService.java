package com.teamuta.userinfoserver.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService {

    private final SqlSession sqlSession;

    public CustomerService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public String getCustomerById(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return sqlSession.selectOne("com.teamuta.userinfoserver.repository.CustomerRepository.selectCustomerNameByUserId", params);
    }
}
