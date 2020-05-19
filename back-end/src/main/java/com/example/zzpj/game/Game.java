package com.example.zzpj.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.zzpj.users.User;

import javax.persistence.*;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Games")
public class Game {
    @Id
    @Column(name = "app_id")
    private long appid;

    private String name;

    //@JsonBackReference
    @JsonIgnore
    @ManyToMany(mappedBy="Games")
    private Collection<User> users;
}
