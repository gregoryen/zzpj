package com.example.zzpj.queue;


import com.example.zzpj.game.Game;
import com.example.zzpj.queue.exception.GameQueueException;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service("gameQueueService")
public class GameQueueService {

    GameQueueRepository gameQueueRepository;
    UserRepository userRepository;

    @Autowired
    public GameQueueService(GameQueueRepository gameQueueRepository, UserRepository userRepository) {
        this.gameQueueRepository = gameQueueRepository;
        this.userRepository = userRepository;
    }

    public void addPlayerToQueue(String login, String gameName){

        User user = findUserByLogin(login);

        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);

        if (user!= null) {
            GameQueue newQueue;
            if (optionalGameQueue.isPresent()) {
                optionalGameQueue.get().addPlayerToQueue(user);
                newQueue = optionalGameQueue.get();
                gameQueueRepository.save(optionalGameQueue.get());
            } else {
                newQueue = new GameQueue(gameName);
                newQueue.addPlayerToQueue(user);
                gameQueueRepository.save(newQueue);
            }
//            user.getQueues().add(newQueue);
//            userRepository.save(user);
        }

    }

    public List<GameQueue> findAllGameQueue(){
        return gameQueueRepository.findAll();
    }

    public GameQueue findGameQueue(String gameName){
        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        return optionalGameQueue.orElse(null);
    }


    public void removePlayerFromQueue(String login, String gameName){

        User user = findUserByLogin(login);

        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        //..
        //optionalGameQueue.ifPresent(gameQueue -> gameQueue.removePlayerFromQueue(user));
        if(user!=null) {

            GameQueue queue;

            if (optionalGameQueue.isPresent()) {

                queue = optionalGameQueue.get();
                user.removeQueue(queue);

                queue.removePlayerFromQueue(user);
                userRepository.save(user);
                if (optionalGameQueue.get().getPlayersInQueue().size()  == 0) {

                    gameQueueRepository.delete(queue);

                } else {
                    gameQueueRepository.save(queue);
                }


            }
        }


    }

    public void createQueue(String gameName){

        gameQueueRepository.save(new GameQueue(gameName));

    }

    public void removeQueue(String gameName) throws GameQueueException{

        Optional<GameQueue> optionalGameQueue = Optional
                .ofNullable(gameQueueRepository
                        .findByGameName(gameName))
                .orElseThrow(() -> new GameQueueException("Queue for this game doesn't exist")
                );

        gameQueueRepository.deleteGameQueueByGameName(gameName);

    }

    private User findUserByLogin(String login){

        Optional<User> optionalUser = userRepository.findByLogin(login);

        return optionalUser.orElse(null);

    }




}
