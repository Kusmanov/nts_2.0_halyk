package com.example.nts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NtsApplication {
    public static void main(String[] args) {
        SpringApplication.run(NtsApplication.class, args);

        System.out.println("""
                      ╔══╗        ╔═════╗
                ╔═══╗ ║██╠════════╣█████║
                ║███╚╗║██║████████║██║██║
                ╚╗███╚╣██║██║██║██║██╚══╩╗
                 ║██║█║██╠══╣██╠══╣██████║
                ╔╝██╠╗███╚═╦╝██╚╦═╩═══███║
                ║███║╚╗████║████║████████║
                ╚═══╝ ╚════╩════╩════════╝ 2.1
                
                http://localhost:8081
                """);
    }
}