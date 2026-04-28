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
public class Video {

    private Long videoId;

    private String videoName;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private Long categoryId;
}

