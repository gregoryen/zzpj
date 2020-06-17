package com.example.zzpj.queue;


import com.example.zzpj.game.Game;
import com.example.zzpj.queue.exception.GameNotFoundInUserCollectionException;
import com.example.zzpj.queue.exception.GameQueueException;
import com.example.zzpj.queue.exception.GameQueueNotExistException;
import com.example.zzpj.queue.exception.UserAlreadyInQueueException;
import com.example.zzpj.ranking.Rate;
import com.example.zzpj.ranking.RateRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.exceptions.UserException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("gameQueueService")
public class GameQueueService {

    GameQueueRepository gameQueueRepository;
    UserRepository userRepository;
    RateRepository rateRepository;


    @Autowired
    public GameQueueService(GameQueueRepository gameQueueRepository,
                            UserRepository userRepository,
                            RateRepository rateRepository) {
        this.gameQueueRepository = gameQueueRepository;
        this.userRepository = userRepository;
        this.rateRepository = rateRepository;
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
                jsonQueues.add(parseQueueToJsonObject(sortUsersByRating(queue)));
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
            jsonQueue = parseQueueToJsonObject(sortUsersByRating(optionalGameQueue.get()));
            return jsonQueue;
        } else {
            throw new GameQueueNotExistException("Queue for this game doesn't exist");
            //return null;
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
            GameQueue  gameQueue = optionalGameQueue.get();
            for(int i =0; i< gameQueue.getPlayersInQueue().size();i++){
                removePlayerFromQueue(gameQueue.getPlayersInQueue().get(i).getLogin(),gameName);
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

    private GameQueue sortUsersByRating(GameQueue queue) {
        List<User> users = queue.getPlayersInQueue();
        List<Rate> rates = new ArrayList<>();

        for (User user : users) {
            Optional<Rate> optionalRate = rateRepository.findByFkUserId(user.getId());
            optionalRate.ifPresent(rates::add);
        }

        List<Rate> sorted = rates.stream()
                .sorted(Comparator
                        .comparing(Rate::getRateValue)
                        .reversed())
                .collect(Collectors.toList());

        List<User> users2 = new ArrayList<>();

        for (Rate r : sorted) {
            for (User user : users) {
                if(user.getId() == r.getFkUserId()) {
                    users2.add(user);
                }
            }
        }

        if (!rates.isEmpty()){
            queue.setPlayersInQueue(users2);
        }

         return queue;
    }

    private JSONObject parseQueueToJsonObject(GameQueue queue){

        List<JSONObject> playersList = new ArrayList<>();

        for (User user : queue.getPlayersInQueue()) {
            JSONObject entity = new JSONObject();
            entity.put("steamId", user.getSteamId());
            entity.put("login", user.getLogin());
            entity.put("id", user.getId());

            playersList.add(entity);
        }

        JSONObject object = new JSONObject();
        object.put("playersInQueue", playersList);
        object.put("gameName", queue.getGameName());
        object.put("id", queue.getId());

        return object;
    }

    public double getOverallRateForUser(String userLogin){
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByLogin(userLogin));
        double rates = -1;
        if(optionalUser.isPresent()) {

            List<Optional<Rate>> optionalList = rateRepository.findAllByFkUserId(optionalUser.get().getId());

            rates = optionalList.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .mapToDouble(Rate::getRateValue)
                    .average().getAsDouble();
        }
        return  rates;
    }

}
