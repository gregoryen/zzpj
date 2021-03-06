package com.example.zzpj.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStats {
    String login;
    int games;
    long playtime;
    long playtime2Weeks;
    String mostPlayedGame;
    String mostPlayedGame2Weeks;
}
