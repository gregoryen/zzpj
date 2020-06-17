package com.example.zzpj.ranking;

import com.example.zzpj.exception.ApiRequestException;
import com.example.zzpj.squad.Squad;
import com.example.zzpj.squad.SquadRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RateService {

    private RateRepository rateRepository;
    private UserRepository userRepository;
    private SquadRepository squadRepository;

    @Autowired
    public RateService(RateRepository rateRepository, UserRepository userRepository, SquadRepository squadRepository) {
        this.rateRepository = rateRepository;
        this.userRepository = userRepository;
        this.squadRepository = squadRepository;
    }

    public void rateUser(final Rate rate) {
        String validationMessage = validateInput(rate);
        if (validationMessage.equals("")) {
            Optional<Rate> optionalRate = rateRepository.findByFkUserIdAndFkSquadId(rate.getFkUserId(), rate.getFkSquadId());
            if (optionalRate.isPresent()) {
                updateValueOfRate(rate.getRateValue(), optionalRate.get());
            } else {
                rateRepository.save(rate);
            }
        } else {
            throw new ApiRequestException(validationMessage);
        }
    }

    private void updateValueOfRate(double rateValue, Rate rate) {
        double oldRateValue = rate.getRateValue();
        double newRateValue = oldRateValue == Rate.NO_RATE ? rateValue : (oldRateValue + rateValue) / 2.0;
        rate.setRateValue(newRateValue);
        rateRepository.save(rate);
    }

    private String validateInput(Rate rate) {
        String result = "";
        Optional<Squad> optionalSquad = squadRepository.findById(rate.getFkSquadId());
        Optional<User> optionalUser = userRepository.findById(rate.getFkUserId());
        if (!optionalUser.isPresent())
            result += "User with provided id = " + rate.getFkUserId() + " doesn't exist;";
        if (!optionalSquad.isPresent())
            result += "Squad with provided id = " + rate.getFkSquadId() + " doesn't exist;";
        else
            result += validateIfLoggedUserBelongsToSquad(optionalSquad.get());
        if (optionalUser.isPresent() && optionalSquad.isPresent()) {
            result += validateIfUserBelongsToSquad(rate);
            result += validateRateValue(rate);
        }
        return result;
    }

    private String validateIfLoggedUserBelongsToSquad(Squad squad) {
        String result = "";
        String name = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        if (!squad.getUsers()
                .contains(userRepository.getByLogin(name)))
            result += "You can't rate anyone, because you don't belong to the squad;";
        return result;
    }

    private String validateIfUserBelongsToSquad(Rate rate) {
        String result = "";
        if (!squadRepository.findById(rate.getFkSquadId()).get().
                getUsers().contains(userRepository.findById(rate.getFkUserId()).get()))
            result += "User with provided id = " + rate.getFkUserId() + " doesn't belong to squad with id = " + rate.getFkSquadId() + ";";
        return result;
    }

    private String validateRateValue(Rate rate) {
        String result = "";
        if (rate.getRateValue() < 1.0 || rate.getRateValue() > 10.0)
            result += "Rate value should be in [1.0; 10.0]. Provided rate is " + rate.getRateValue() + ";";
        return result;
    }

    public List<JSONObject> getRankingBySquadId(final long fkSquadId) {
        List<Optional<Rate>> optionals = rateRepository.findAllByFkSquadId(fkSquadId);
        List<Rate> rates = optionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(Rate::getRateValue).reversed())
                .collect(Collectors.toList());
        return parseRatesToJSONObjects(rates);
    }

    private List<JSONObject> parseRatesToJSONObjects(List<Rate> rates) {
        List<JSONObject> entities = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            JSONObject entity = new JSONObject();
            entity.put("place: ", i);
            entity.put("user: ", userRepository.getById(rates.get(i).getFkUserId()).getLogin());
            entity.put("rate: ", rates.get(i).getRateValue());
            entities.add(entity);
        }
        return entities;
    }

    public Rate getRateById(final long id) {
        return rateRepository.findById(id).orElseThrow();
    }

}