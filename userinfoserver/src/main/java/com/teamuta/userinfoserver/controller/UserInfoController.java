package com.teamuta.userinfoserver.controller;

import com.teamuta.userinfoserver.dto.CustomerDTO;
import com.teamuta.userinfoserver.dto.UserInfoDTO;
import com.teamuta.userinfoserver.dto.WatchHistoryDTO;
import com.teamuta.userinfoserver.service.CustomerService;
import com.teamuta.userinfoserver.service.FollowsService;
import com.teamuta.userinfoserver.service.RoomStatsService;
import com.teamuta.userinfoserver.service.WatchHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;


/**
 * 
 * my page 관련 컨트롤러.
 * 사용자 정보 조회, 팔로우/팔로워 조회, 시청 기록 조회 기능
 */
@RestController
@RequestMapping("/users")
public class UserInfoController {

    private final FollowsService followsService;
    private final CustomerService customerService;
    private final WatchHistoryService watchHistoryService;
    private final RoomStatsService roomStatsService;
    private final boolean gatewayAuthRequired;

    public UserInfoController(FollowsService followsService,
                              CustomerService customerService,
                              WatchHistoryService watchHistoryService,
                              RoomStatsService roomStatsService,
                              @Value("${gateway.auth.required:false}") boolean gatewayAuthRequired) {
        this.followsService = followsService;
        this.customerService = customerService;
        this.watchHistoryService = watchHistoryService;
        this.roomStatsService = roomStatsService;
        this.gatewayAuthRequired = gatewayAuthRequired;
    }

    @PostMapping("/{userId}/follow")
    public boolean subscribeUser(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId,
            @RequestBody Map<String, String> request) {

        String myUserId = resolveActorUserId(userId, authenticatedUserId, request.get("user_id"));
        String streamerId = request.get("streamerId");

        return followsService.subscribeUser(myUserId, streamerId);
    }

    @DeleteMapping("/{userId}/follow/{streamerId}")
    public boolean unsubscribeUser(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId,
            @PathVariable String streamerId) {

        String actorUserId = resolveActorUserId(userId, authenticatedUserId, userId);
        return followsService.unsubscribeUser(actorUserId, streamerId);
    }

    @PutMapping("/{userId}/profile")
    public CustomerDTO updateUserInfo(@PathVariable("userId") String userId,
                                      @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId,
                                      @RequestBody CustomerDTO entity) {
        String actorUserId = resolveActorUserId(userId, authenticatedUserId, userId);
        customerService.updateCustomerInfo(actorUserId, entity);

        return entity;
    }
    

    @GetMapping("/info/{userId}")
    public UserInfoDTO getUserInfo(@PathVariable String userId) {
        return UserInfoDTO.builder()
                .userId(userId)
                .userName(customerService.getCustomerById(userId))
                .email(customerService.getCustomerEmailById(userId))
                .followers(followsService.getFollowedCount(userId))
                .following(followsService.getFollowingCount(userId))
                .streams(roomStatsService.countStreamsByBroadcaster(userId))
                .build();
    }

   /*
   * 내가 팔로우 하는 사람들 불러오기
   * */
    @GetMapping("/{userId}/Ifollowing/{page}/{size}")
    public List<CustomerDTO> getMyFollowing(
            @PathVariable String userId, @PathVariable int page, @PathVariable int size) {
        return followsService.getFollowingList(userId, page, size);
    }

    @GetMapping("/{userId}/watch_history/{offset}/{limit}")
    public List<WatchHistoryDTO> getWatchHistory(@PathVariable String userId,
                                                 @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId,
                                                 @PathVariable int offset,
                                                 @PathVariable int limit) {
        String actorUserId = resolveActorUserId(userId, authenticatedUserId, userId);
        return watchHistoryService.getRecentWatchHistoriesByUserId(actorUserId, offset, limit);
    }

    @GetMapping("/{userId}/followingI/{page}/{size}")
    public List<CustomerDTO> getFollowingMe(
            @PathVariable String userId, @PathVariable int page, @PathVariable int size) {
        return followsService.getFollowerList(userId, page, size);
    }

    //시청기록 조회기능 추가해야함.. 
    private String resolveActorUserId(String pathUserId, String authenticatedUserId, String fallbackUserId) {
        if (authenticatedUserId != null && !authenticatedUserId.isBlank()) {
            if (pathUserId != null && !pathUserId.isBlank() && !pathUserId.equals(authenticatedUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "authenticated user does not match requested user");
            }
            return authenticatedUserId;
        }
        if (gatewayAuthRequired) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "gateway authentication is required");
        }
        return fallbackUserId;
    }
}
