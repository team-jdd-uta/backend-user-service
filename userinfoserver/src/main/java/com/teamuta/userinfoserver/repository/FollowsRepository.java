package com.teamuta.userinfoserver.repository;

import com.teamuta.userinfoserver.dto.CustomerDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FollowsRepository {
	int selectFollowingCount(@Param("userId") String userId);
	int selectFollowedCount(@Param("userId") String userId);
	int insertFollow(@Param("followingUserId") String followingUserId,
					 @Param("followedUserId") String followedUserId,
					 @Param("followedAt") LocalDateTime followedAt);
	List<CustomerDTO> selectFollowingByUserId(@Param("userId") String userId,
											  @Param("offset") int offset,
											  @Param("limit") int limit);
	List<CustomerDTO> selectFollowedByUserId(@Param("userId") String userId,
											 @Param("offset") int offset,
											 @Param("limit") int limit);
}
