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
                ╚═══╝ ╚════╩════╩════════╝ 3.3
                
                http://localhost:8088/swagger-ui/index.html
                """);
    }
}