package com.teamuta.userinfoserver.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "comment")
public class Comment {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private String comment;

    @Field("room_id")
    private String roomId;

    private Date createdAt;
}