package com.mind.app.controller;

import com.mind.app.model.JWTRequest;
import com.mind.app.model.JWTResponse;
import com.mind.app.security.JWTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class SpringSecurityController {

    private Logger logger = LoggerFactory.getLogger(SpringSecurityController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;


    @Autowired
    private JWTHelper helper;

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

   /* this method will fetch current user username*/
    @GetMapping("/current-user")
    public String getLoggedInUser(Principal principal){
        return principal.getName();
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {

        logger.info("userName ==> "+ request.getUserName());

        this.doAuthenticate(request.getUserName(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
        String token = this.helper.generateToken(userDetails);

        JWTResponse response = JWTResponse.builder()
                .token(token)
                .userName(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
}
