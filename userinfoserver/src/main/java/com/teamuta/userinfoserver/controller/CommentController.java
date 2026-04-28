package com.teamuta.userinfoserver.controller;

import com.teamuta.userinfoserver.entity.Comment;
import com.teamuta.userinfoserver.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*

Comment랑 관련 있다기보다는 채팅 조회 기능임. 

 */

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsWithUserId(@PathVariable String userId) {
        try {
            List<Comment> comments = commentService.getUserComments(userId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/user/{userId}/room/{roomId}")
    public ResponseEntity<List<Comment>> getCommentsWithUserIdRoomId(@PathVariable String userId,
                                                                     @PathVariable String roomId) {
        try {
            List<Comment> comments = commentService.getCommentsWithUserIdRoomId(userId, roomId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Comment>> getCommentsFromRoomId(@PathVariable String roomId) {
        try {
            List<Comment> comments = commentService.getCommentsWithRoomId(roomId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/room/{roomId}/date/{startDate}/{endDate}")
    public ResponseEntity<List<Comment>> getCommentsWithRoomIdAndDateRange(@PathVariable String roomId,
                                                                                 @PathVariable String startDate,
                                                                                 @PathVariable String endDate) {
        try {
            List<Comment> comments = commentService.getCommentsWithRoomIdAndDateRange( roomId, startDate, endDate);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
