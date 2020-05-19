package com.example.zzpj.configuration;

import org.springframework.context.annotation.Bean;

import java.io.*;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public String getSteamKey() throws IOException {
        File file = new File("SteamApiKey.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String key;
        key = br.readLine();
        System.out.println(key);
        return key;
    }
}

