package com.batch57.gdipsa.group6.lapsbackend;

import com.batch57.gdipsa.group6.lapsbackend.repository.user.userRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LapsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LapsBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner loadData(userRepository userRepo) {
        return args -> {
//            User u1 = new User("ShuaiHao","password", USER_TYPE.ADMIN);
//            userRepo.save(u1);
        };
    }

}
