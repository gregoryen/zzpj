package com.example.zzpj.security.authentication;


import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtResponse;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.service.GameService;
import com.example.zzpj.users.UserSignInPOJO;
import com.example.zzpj.users.UserSignUpPOJO;
import com.example.zzpj.users.UserTokenInformation;
import com.example.zzpj.users.exceptions.BadUserCredentialsException;
import com.example.zzpj.users.exceptions.UserDisabledException;
import com.example.zzpj.users.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController

public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private GameService gameService;

    private JwtUtil jwtUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, GameService gameService, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.gameService = gameService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(path = "signUp", consumes = "application/json")
    @ResponseBody
    public ResponseEntity signUp(@RequestBody UserSignUpPOJO accountDetails) {
        return createUserAccount(accountDetails) ? new ResponseEntity(HttpStatus.CREATED) : new ResponseEntity(HttpStatus.CONFLICT);
    }

    @PostMapping(path = "logIn", consumes = "application/json")
    @ResponseBody
    public ResponseEntity logIn(@RequestBody UserSignInPOJO authenticationDetails, @RequestHeader("User-agent") String device) throws Exception {
        try {
            authenticate(authenticationDetails.getLogin(), authenticationDetails.getPassword());
        } catch (UserException e) {
            return new ResponseEntity(e, HttpStatus.UNAUTHORIZED);
        }

        UserTokenInformation userTokenInformation = userService.getUserDetailsForToken(authenticationDetails.getLogin());
        String token = jwtUtil.generateToken(userTokenInformation, device);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping(path = "verifyToken")
    public ResponseEntity verifyToken(@RequestHeader("User-Agent") String device) throws Exception {
        return new ResponseEntity(HttpStatus.OK);
    }

    private void authenticate(String email, String password) throws Exception {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new Exception("Empty credentials");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException disabled) {
            throw new UserDisabledException("User disabled");
        } catch (BadCredentialsException badCredentials) {
            throw new BadUserCredentialsException("Invalid credentials");
        }
    }

    private boolean createUserAccount(UserSignUpPOJO accountDetails) {
        try {
            userService.registerNewUserAccount(accountDetails);
            gameService.insertUserGamesToDb(Long.toString(accountDetails.getSteamId()));
            return true;
        } catch (UserException ue) {
            return false;
        }
        catch(Exception e){
            return false;
        }
    }
}
