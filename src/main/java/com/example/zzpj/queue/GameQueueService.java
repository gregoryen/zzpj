package com.example.zzpj.queue;


import com.example.zzpj.game.Game;
import com.example.zzpj.queue.exception.GameNotFoundInUserCollectionException;
import com.example.zzpj.queue.exception.GameQueueException;
import com.example.zzpj.queue.exception.GameQueueNotExistException;
import com.example.zzpj.queue.exception.UserAlreadyInQueueException;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.exceptions.UserException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
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

    public void addPlayerToQueue(String login, String gameName) throws GameNotFoundInUserCollectionException, UsernameNotFoundException, UserAlreadyInQueueException{

        User user = findUserByLogin(login);
        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        if (user!= null) {
            if(user.getGames().stream().filter(game -> game.getName().equals(gameName)).findAny().isPresent()) {
                if (optionalGameQueue.isPresent()) {
                    if(optionalGameQueue.get().getPlayersInQueue().stream().filter(user1 -> user1.getLogin().equals(user.getLogin())).findAny().isEmpty()) {
                        optionalGameQueue.get().addPlayerToQueue(user);
                        gameQueueRepository.save(optionalGameQueue.get());
                    }
                    else
                        throw new UserAlreadyInQueueException(user.getLogin());
                    } else {
                    GameQueue newQueue;
                    newQueue = new GameQueue(gameName);
                    newQueue.addPlayerToQueue(user);
                    gameQueueRepository.save(newQueue);
                }
            }
            else
                throw new GameNotFoundInUserCollectionException(gameName);
        }
        else
            throw new UsernameNotFoundException(login);


    }

    public List<JSONObject> findAllGameQueue() throws GameQueueNotExistException{
        List<GameQueue> listOfQueues = gameQueueRepository.findAll();
        if (!listOfQueues.isEmpty()) {
            List<JSONObject> jsonQueues = new ArrayList<>();
            for (GameQueue queue : listOfQueues) {
                jsonQueues.add(parseQueueToJsonObject(queue));
            }
            return jsonQueues;
        } else {
            throw new GameQueueNotExistException("There is no queues available");
        }

    }

    public JSONObject findGameQueue(String gameName) throws GameQueueNotExistException{
        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        JSONObject jsonQueue;
        if(optionalGameQueue.isPresent()){
            jsonQueue = parseQueueToJsonObject(optionalGameQueue.get());
            return jsonQueue;
        } else {
            throw new GameQueueNotExistException("Queue for this game doesn't exist");
        }
    }


    public void removePlayerFromQueue(String login, String gameName) throws UsernameNotFoundException{

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
            else
                throw new UsernameNotFoundException("Username "+ login + " not found");
        }
        else
            throw new UsernameNotFoundException("Username "+ login + " not found");
    }

    public void createQueue(String gameName){

        gameQueueRepository.save(new GameQueue(gameName));

    }

    public void removeQueue(String gameName) throws GameQueueNotExistException{
        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        if(optionalGameQueue.isPresent()){
            for(User user : optionalGameQueue.get().getPlayersInQueue()){
                removePlayerFromQueue(user.getLogin(),gameName);
            }
            gameQueueRepository.deleteGameQueueByGameName(gameName);
        }
        else
            throw new GameQueueNotExistException("Queue for this game doesn't exist");
    }

    private User findUserByLogin(String login){

        Optional<User> optionalUser = userRepository.findByLogin(login);

        return optionalUser.orElse(null);
    }

    private JSONObject parseQueueToJsonObject(GameQueue queue){

        List<JSONObject> playersList = new ArrayList<>();

        for (User user : queue.getPlayersInQueue()) {
            JSONObject entity = new JSONObject();
            entity.put("id", user.getId());
            entity.put("login", user.getLogin());
            entity.put("steamId", user.getSteamId());
            playersList.add(entity);
        }

        JSONObject object = new JSONObject();
        object.put("id", queue.getId());
        object.put("gameName", queue.getGameName());
        object.put("playersInQueue", playersList);

        return object;
    }




}
