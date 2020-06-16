package com.example.zzpj;

import com.example.zzpj.game.Game;
import com.example.zzpj.users.User;

import java.util.ArrayList;
import java.util.HashSet;

public class InitTestObjects {

    public static  User initUser(){
        User user = new User();
        user.setLogin("test1");
        user.setPassword("1234");
        user.setSteamId(76561198253700224L);
        user.setGames(new ArrayList<>());
        user.setQueues(new ArrayList<>());
        user.setSquads(new ArrayList<>());
    return user;

    }
    public static Game initGame(){
        Game game = new Game();
        game.setUsers(new ArrayList<>());
        game.setSquads(new HashSet<>());
        game.setAppid(730L);
        game.setName("Counter-Strike: Global Offensive");
        return game;
    }

}
