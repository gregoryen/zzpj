package com.example.zzpj.users;



import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSignUpPOJO {

    private String login;
    private String password;
    private long steamId;
}
