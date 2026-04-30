package com.teamuta.userinfoserver.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.teamuta.userinfoserver.config.CustomerShardContext;
import com.teamuta.userinfoserver.dto.CustomerDTO;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService {

    private final SqlSession sqlSession;

    public CustomerService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public CustomerDTO updateCustomerInfo(String userId, CustomerDTO entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("name", entity.getUsername());
        params.put("email", entity.getEmail());
        CustomerShardContext.useShard(resolveShardByCustomerId(userId));
        try {
            sqlSession.update("com.teamuta.userinfoserver.repository.CustomerRepository.updateCustomerInfo", params);
        } finally {
            CustomerShardContext.clear();
        }
        return entity;
    }

    public String getCustomerById(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        CustomerShardContext.useShard(resolveShardByCustomerId(userId));
        try {
            return sqlSession.selectOne("com.teamuta.userinfoserver.repository.CustomerRepository.selectCustomerNameByUserId", params);
        } finally {
            CustomerShardContext.clear();
        }
    }

    private String resolveShardByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return CustomerShardContext.SHARD_3307;
        }

        int bucket = Math.floorMod(customerId.hashCode(), 2);
        return bucket == 0 ? CustomerShardContext.SHARD_3307 : CustomerShardContext.SHARD_3309;
    }
}
