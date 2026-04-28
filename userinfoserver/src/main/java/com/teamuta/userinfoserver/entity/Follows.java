package com.teamuta.userinfoserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follows {

    private String followingUserId;

    private String followedUserId;

    private LocalDateTime followedAt;

}

