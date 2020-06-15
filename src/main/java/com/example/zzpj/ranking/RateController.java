package com.example.zzpj.ranking;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ranking")
public class RateController {

    private RateService rateService;

    @Autowired
    public RateController(final RateService rateService) {
        this.rateService = rateService;
    }

    @PutMapping(path = "/rate", consumes = "application/json")
    public ResponseEntity<String> rateUser(@RequestBody Rate rate) {
        String message = rateService.rateUser(rate);
        return message.equals("")
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>("{\"error\": \"" + message + "\"}", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping(path = "/bySquadId")
    public ResponseEntity<List<JSONObject>> getRankingBySquadId(@RequestParam long squadId) {
        List<JSONObject> rates = rateService.getRankingBySquadId(squadId);
        return rates.size() != 0
                ? new ResponseEntity<>(rates, HttpStatus.OK)
                : new ResponseEntity<>(rates, HttpStatus.NOT_FOUND);
    }
}