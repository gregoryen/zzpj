package com.example.zzpj.users;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTokenInformation {
    private String login;
    private Long steamId;
}




