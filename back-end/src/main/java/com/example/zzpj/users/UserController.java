package com.example.zzpj.users;


import com.example.zzpj.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping(path = "/rate", consumes = "application/json")
    public ResponseEntity<String> rateUser(@RequestParam long userId, @RequestParam Double rate) {
        return userService.rateUser(userId, rate)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>("{\"error\": \"Required rate is from the range of values: <1.0, 10.0>. Value provided is " + rate + "\"}", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}