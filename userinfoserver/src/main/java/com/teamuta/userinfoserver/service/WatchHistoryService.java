package com.teamuta.userinfoserver.service;

import com.teamuta.userinfoserver.config.CustomerShardContext;
import com.teamuta.userinfoserver.dto.WatchHistoryDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WatchHistoryService {
    private final SqlSession sqlSession;

    public WatchHistoryService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<WatchHistoryDTO> getRecentWatchHistoriesByUserId(String userId, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        params.put("offset", offset);

        CustomerShardContext.useShard(resolveShardByCustomerId(userId));
        List<WatchHistoryDTO> histories;
        try {
            histories = sqlSession.selectList("com.teamuta.userinfoserver.repository.WatchHistoryRepository.selectRecentWatchHistoriesByUserId", params);
        } finally {
            CustomerShardContext.clear();
        }

        for (WatchHistoryDTO history : histories) {
            history.setVideoName(getVideoNameFromPrimary(history.getVideoId()));
        }
        return histories;
    }

    @Transactional
    public boolean insertWatchHistory(WatchHistoryDTO watchHistory) {
        CustomerShardContext.useShard(resolveShardByCustomerId(watchHistory.getUserId()));
        int result;
        try {
            result = sqlSession.insert("com.teamuta.userinfoserver.repository.WatchHistoryRepository.insertWatchHistory", watchHistory);
        } finally {
            CustomerShardContext.clear();
        }
        System.out.println("Inserted WatchHistory: " + watchHistory + ", Result: " + result);
        if(result > 0) {
            return true;
        }
        return false;
    }

    private String resolveShardByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return CustomerShardContext.SHARD_3307;
        }

        int bucket = Math.floorMod(customerId.hashCode(), 2);
        return bucket == 0 ? CustomerShardContext.SHARD_3307 : CustomerShardContext.SHARD_3309;
    }

    private String getVideoNameFromPrimary(Long videoId) {
        if (videoId == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("videoId", videoId);

        CustomerShardContext.useShard(CustomerShardContext.SHARD_3307);
        try {
            return sqlSession.selectOne("com.teamuta.userinfoserver.repository.WatchHistoryRepository.selectVideoNameByVideoId", params);
        } finally {
            CustomerShardContext.clear();
        }
    }
}
