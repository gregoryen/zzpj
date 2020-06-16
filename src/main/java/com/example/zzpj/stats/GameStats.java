package com.example.zzpj.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameStats {
    private long appid;
    private long playtime2Weeks;
    private long playtimeForever;
    private long playtimeWindows;
    private long playtimeMac;
    private long playtimeLinux;
}
