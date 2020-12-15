package com.bloxico.ase.userservice.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class Temp {

    @GetMapping("/hello")
    public ResponseEntity<String> hello(@RequestParam(name = "token") String token) {
        return ResponseEntity.of(Optional.of("Hello from Social! " + token));
    }

}
