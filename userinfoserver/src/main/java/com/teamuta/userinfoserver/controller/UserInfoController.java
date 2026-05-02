package com.teamuta.userinfoserver.controller;

import com.teamuta.userinfoserver.dto.CustomerDTO;
import com.teamuta.userinfoserver.dto.UserInfoDTO;
import com.teamuta.userinfoserver.dto.WatchHistoryDTO;
import com.teamuta.userinfoserver.service.CustomerService;
import com.teamuta.userinfoserver.service.FollowsService;
import com.teamuta.userinfoserver.service.WatchHistoryService;
import org.springframework.web.bind.annotation.*;

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

    public UserInfoController(FollowsService followsService, CustomerService customerService, WatchHistoryService watchHistoryService) {
        this.followsService = followsService;
        this.customerService = customerService;
        this.watchHistoryService = watchHistoryService;
    }

    @PostMapping("/{userId}/follow")
    public boolean subscribeUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {

        String myUserId = request.get("user_id");
        String streamerId = request.get("streamerId");

        return followsService.subscribeUser(myUserId, streamerId);
    }

    @DeleteMapping("/{userId}/follow/{streamerId}")
    public boolean unsubscribeUser(
            @PathVariable String userId,
            @PathVariable String streamerId) {

        return followsService.unsubscribeUser(userId, streamerId);
    }

    @PutMapping("/{userId}/profile")
    public CustomerDTO updateUserInfo(@PathVariable("userId") String userId, @RequestBody CustomerDTO entity) {
        customerService.updateCustomerInfo(userId, entity);

        return entity;
    }
    

    @GetMapping("/info/{userId}")
    public UserInfoDTO getUserInfo(@PathVariable String userId) {
        return UserInfoDTO.builder()
                .userId(userId)
                .userName(customerService.getCustomerById(userId))
                .followers(followsService.getFollowedCount(userId))
                .following(followsService.getFollowingCount(userId))
                .streams(-1) //일단 스트림 횟수 0으로 해놓고 나중에 해결하기.
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
    public List<WatchHistoryDTO> getWatchHistory(@PathVariable String userId, @PathVariable int offset, @PathVariable int limit) {
        return watchHistoryService.getRecentWatchHistoriesByUserId(userId, offset, limit);
    }

    @GetMapping("/{userId}/followingI/{page}/{size}")
    public List<CustomerDTO> getFollowingMe(
            @PathVariable String userId, @PathVariable int page, @PathVariable int size) {
        return followsService.getFollowerList(userId, page, size);
    }

    //시청기록 조회기능 추가해야함.. 
}
