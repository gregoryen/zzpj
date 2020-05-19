package com.example.zzpj.queue;

import com.example.zzpj.game.Game;
import com.example.zzpj.users.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;


import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
//@Builder
@Table(name="game_queue")
public class GameQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gameName;

    @ManyToMany(mappedBy = "queues")
    private List<User> playersInQueue;

    public GameQueue() {
        playersInQueue = new LinkedList<>();
    }
    public GameQueue(String gameName){
        this.gameName = gameName;
        playersInQueue = new LinkedList<>();
    }

    public void addPlayerToQueue(User user){
        playersInQueue.add(user);
        user.getQueues().add(this);
    }

    public void removePlayerFromQueue(User user){
        playersInQueue.remove(user);
        user.getQueues().remove(this);
    }



}
