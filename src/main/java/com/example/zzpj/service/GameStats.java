package com.example.zzpj.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameStats {
    private long appid;
    private int playtime2Weeks;
    private int playtimeForever;
    private int playtimeWindows;
    private int playtimeMac;
    private int playtimeLinux;
}
