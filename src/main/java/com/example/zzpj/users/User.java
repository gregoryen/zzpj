package com.example.zzpj.users;

import com.example.zzpj.squad.Squad;
import com.example.zzpj.queue.GameQueue;
import com.example.zzpj.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = "Games")
@Builder(access = AccessLevel.PUBLIC)
@Entity
@Transactional
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String login;

    @NonNull
    private String password;

    @NonNull
    @Column(unique = true)
    private long steamId;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_games",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "game_app_id", referencedColumnName = "app_id"))
    private Collection<Game> Games;

    @JsonManagedReference
    @ManyToMany
    //@JsonIgnore
    @JoinTable(name = "user_queue",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "queue_id"))
    private List<GameQueue> queues;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "user_squad",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "squad_id"))
    private List<Squad> squads;

    public User() {
        this.queues = new ArrayList<>();
    }

    public void addQueue(GameQueue game) {
        queues.add(game);
        game.getPlayersInQueue().add(this);
    }

    public void removeQueue(GameQueue game){
        queues.remove(game);
        game.removePlayerFromQueue(this);
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }


}


