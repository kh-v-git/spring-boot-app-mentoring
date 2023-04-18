/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppCommandLineRunner {

    @Bean
    public CommandLineRunner CommandLineRunner() {
        return args -> {
            System.out.println("hello world");
        };
    }
}
