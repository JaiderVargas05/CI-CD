package edu.eci.cvds.task_back.Controller;

import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import edu.eci.cvds.task_back.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.eci.cvds.task_back.Domain.User;

import java.util.Map;


@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserService userService;
    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            userService.createUser(user);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String passwd = loginData.get("passwd");
            String id = userService.authentication(email, passwd) ;
            if(id!=null){
                return new ResponseEntity<>(id, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

