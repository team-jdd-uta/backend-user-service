package com.teamuta.userinfoserver.repository;

import com.teamuta.userinfoserver.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    @Query(value = "{ 'user_id' : ?0 }", sort = "{ 'createdAt' : -1 }")
    List<Comment> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("{ 'room_id' : ?0 }")
    List<Comment> findByRoomId(String roomId);

    @Query("{ 'user_id' : ?0, 'room_id' : ?1 }")
    List<Comment> findByUserIDAndRoomId(String userId, String roomId);

    @Query("{'room_id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Comment> findByRoomIdAndCreatedAtBetween(String roomId, String startDate, String endDate);
}
