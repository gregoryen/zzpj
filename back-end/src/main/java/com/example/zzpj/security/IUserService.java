package com.example.zzpj.security;


import com.example.zzpj.users.User;
import com.example.zzpj.users.UserSignUpPOJO;

public interface IUserService{
    User registerNewUserAccount(UserSignUpPOJO account);
}
