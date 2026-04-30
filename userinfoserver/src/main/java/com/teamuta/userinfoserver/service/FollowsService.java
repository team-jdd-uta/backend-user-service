package com.teamuta.userinfoserver.service;

import com.teamuta.userinfoserver.config.CustomerShardContext;
import com.teamuta.userinfoserver.dto.CustomerDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowsService {

    private final SqlSession sqlSession;
    private final CustomerService customerService;

    public FollowsService(SqlSession sqlSession, CustomerService customerService) {
        this.sqlSession = sqlSession;
        this.customerService = customerService;
    }



    public int getFollowingCount(String customerId) {
        //내가 팔로우하는 사람들 수
        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);

        CustomerShardContext.useShard(resolveShardByCustomerId(customerId));
        try {
            return sqlSession.selectOne("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowingCount", params);
        } finally {
            CustomerShardContext.clear();
        }
    }

    public int getFollowedCount(String customerId) {
        //나를 팔로우하는 사람들 수
        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);

        int total = 0;
        for (String shard : allShards()) {
            CustomerShardContext.useShard(shard);
            try {
                Integer count = sqlSession.selectOne("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowedCount", params);
                total += count == null ? 0 : count;
            } finally {
                CustomerShardContext.clear();
            }
        }
        return total;
    }

    public boolean subscribeUser(String fromCustomerId, String toCustomerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("followingUserId", fromCustomerId);
        params.put("followedUserId", toCustomerId);
        params.put("followedAt", LocalDateTime.now());

        CustomerShardContext.useShard(resolveShardByCustomerId(fromCustomerId));
        try {
            return sqlSession.insert("com.teamuta.userinfoserver.repository.FollowsRepository.insertFollow", params) > 0;
        } finally {
            CustomerShardContext.clear();
        }
    }

    public List<CustomerDTO> getFollowingList(String customerId, int offset, int limit) {
        //내가 팔로우하는 사람들
        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);
        params.put("offset", offset);
        params.put("limit", limit);

        CustomerShardContext.useShard(resolveShardByCustomerId(customerId));
        List<CustomerDTO> customers;
        try {
            customers = sqlSession.selectList("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowingByUserId", params);
        } finally {
            CustomerShardContext.clear();
        }

        for (CustomerDTO customer : customers) {
            customer.setUsername(customerService.getCustomerById(customer.getCustomerId()));
        }
        return customers;
    }

    public List<CustomerDTO> getFollowerList(String customerId, int offset, int limit) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);
        params.put("offset", 0);
        params.put("limit", offset + limit);

        List<CustomerDTO> merged = new ArrayList<>();
        for (String shard : allShards()) {
            CustomerShardContext.useShard(shard);
            try {
                merged.addAll(sqlSession.selectList("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowedByUserId", params));
            } finally {
                CustomerShardContext.clear();
            }
        }

        merged.sort(Comparator.comparing(CustomerDTO::getCustomerId));
        int from = Math.min(offset, merged.size());
        int to = Math.min(offset + limit, merged.size());

        List<CustomerDTO> customers = new ArrayList<>(merged.subList(from, to));
        for (CustomerDTO customer : customers) {
            customer.setUsername(customerService.getCustomerById(customer.getCustomerId()));
        }
        return customers;
    }

    private String resolveShardByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return CustomerShardContext.SHARD_3307;
        }

        int bucket = Math.floorMod(customerId.hashCode(), 2);
        return bucket == 0 ? CustomerShardContext.SHARD_3307 : CustomerShardContext.SHARD_3309;
    }

    private List<String> allShards() {
        return List.of(CustomerShardContext.SHARD_3307, CustomerShardContext.SHARD_3309);
    }

}
