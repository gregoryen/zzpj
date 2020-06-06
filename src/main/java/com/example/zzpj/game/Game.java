package com.example.zzpj.game;

import com.example.zzpj.squad.Squad;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.zzpj.users.User;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy="game", cascade = CascadeType.MERGE)
    private Set<Squad> squads;

//    @JsonIgnore
//    @ManyToMany(mappedBy="games")
//    private List<Squad> squads;
}
