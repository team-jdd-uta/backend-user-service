package com.teamuta.userinfoserver.service;

import com.teamuta.userinfoserver.entity.Comment;
import com.teamuta.userinfoserver.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public List<Comment> getUserComments(String userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return comments;
    }

    public List<Comment> getCommentsWithUserIdRoomId(String userId, String roomId) {
        return commentRepository.findByUserIDAndRoomId(userId, roomId);
    }
    public List<Comment> getCommentsWithRoomId(String roomId) {
        return commentRepository.findByRoomId(roomId);
    }

    public List<Comment> getCommentsWithRoomIdAndDateRange( String roomId, String startDate, String endDate) {
        return commentRepository.findByRoomIdAndCreatedAtBetween( roomId, startDate, endDate);
    }
}
