package com.teamuta.userinfoserver.repository;

import com.teamuta.userinfoserver.dto.WatchHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WatchHistoryRepository {
	List<WatchHistoryDTO> selectRecentWatchHistoriesByUserId(@Param("userId") String userId,
															 @Param("offset") int offset,
															 @Param("limit") int limit);
	int insertWatchHistory(WatchHistoryDTO watchHistory);
	String selectVideoNameByVideoId(@Param("videoId") Long videoId);
}

