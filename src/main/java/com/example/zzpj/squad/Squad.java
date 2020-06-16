package com.example.zzpj.squad;

import com.example.zzpj.game.Game;
import com.example.zzpj.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="squad")
public class Squad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String owner;

    @NotNull
    private String name;

    @NotNull
    private String level;

    @ManyToOne
    @JoinColumn(name="game_id", nullable=false)
    private Game game;

    @ManyToMany(mappedBy = "squads")
    private List<User> users;

    public void addUser(User user) {
        users.add(user);
        user.getSquads().add(this);
    }

}