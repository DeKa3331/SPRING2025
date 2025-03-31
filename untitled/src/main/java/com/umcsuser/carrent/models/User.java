package com.umcsuser.carrent.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String login;
    private String passwordHash;
    private String role; // "ADMIN" or "USER"
}