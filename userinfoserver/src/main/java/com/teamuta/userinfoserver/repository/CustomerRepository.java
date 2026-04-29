package com.teamuta.userinfoserver.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerRepository {
    String selectCustomerNameByUserId(@Param("userId") String userId);

    int upsertRegisteredUser(@Param("userId") String userId,
                             @Param("name") String name,
                             @Param("email") String email,
                             @Param("createdAt") java.time.LocalDateTime createdAt);

    int insertConsumedUserEvent(@Param("eventId") String eventId);
}
