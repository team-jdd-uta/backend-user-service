package com.teamuta.userinfoserver.service;

import com.teamuta.userinfoserver.dto.CustomerDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowsService {

    private final SqlSession sqlSession;

    public FollowsService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }



    public int getFollowingCount(String customerId) {
        //내가 팔로우하는 사람들 수
        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);
        return sqlSession.selectOne("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowingCount", params);
    }

    public int getFollowedCount(String customerId) {
        //나를 팔로우하는 사람들 수
        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);
        return sqlSession.selectOne("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowedCount", params);
    }

    public boolean subscribeUser(String fromCustomerId, String toCustomerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("followingUserId", fromCustomerId);
        params.put("followedUserId", toCustomerId);
        params.put("followedAt", LocalDateTime.now());
        return sqlSession.insert("com.teamuta.userinfoserver.repository.FollowsRepository.insertFollow", params) > 0;
    }

    public List<CustomerDTO> getFollowingList(String customerId, int offset, int limit) {
        //내가 팔로우하는 사람들
        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);
        params.put("offset", offset);
        params.put("limit", limit);

        System.out.println("Getting following list for userId: " + customerId + ", offset: " + offset + ", limit: " + limit);
        System.out.println(params);
        return sqlSession.selectList("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowingByUserId", params);
    }

    public List<CustomerDTO> getFollowerList(String customerId, int offset, int limit) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", customerId);
        params.put("offset", offset);
        params.put("limit", limit);

        System.out.println(params);

        System.out.println("Getting follower list for userId: " + customerId + ", offset: " + offset + ", limit: " + limit);

        return sqlSession.selectList("com.teamuta.userinfoserver.repository.FollowsRepository.selectFollowedByUserId", params);
    }

}
