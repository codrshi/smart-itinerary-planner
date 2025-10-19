package com.codrshi.smart_itinerary_planner.entity;

import com.codrshi.smart_itinerary_planner.common.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
@Getter
@Setter
@ToString
public class User extends Audit{
    @Id
    private String docId;
    private String username;
    private String email;
    private String password;
    private List<UserRole> roles;
}
