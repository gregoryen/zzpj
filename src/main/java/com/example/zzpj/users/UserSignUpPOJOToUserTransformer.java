package com.example.zzpj.users;


import com.example.zzpj.common.Transformer;
import org.springframework.stereotype.Component;

@Component
public class UserSignUpPOJOToUserTransformer implements Transformer<UserSignUpPOJO, User> {

    @Override
    public User transform(UserSignUpPOJO pojo) {

        return User.builder()
                .login(pojo.getLogin())
                .password(pojo.getPassword())
                .steamId(pojo.getSteamId())
                .build();
    }

}
