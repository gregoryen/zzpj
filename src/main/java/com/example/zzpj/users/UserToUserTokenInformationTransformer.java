package com.example.zzpj.users;

import com.example.zzpj.common.Transformer;
import org.springframework.stereotype.Service;

@Service
public class UserToUserTokenInformationTransformer implements Transformer<User, UserTokenInformation> {
    @Override
    public UserTokenInformation transform(User user) {
        return UserTokenInformation.builder()
                .login(user.getLogin())
                .steamId(user.getSteamId())
                .build();
    }
}
