package com.techcorp.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class EmployeeManagementApplication {
    public static void main(String[] args) {


            String classpath = System.getProperty("java.class.path");
            System.out.println("Classpath: " + classpath);



        SpringApplication.run(EmployeeManagementApplication.class, args);
        System.out.println("✅ Aplikacja Spring Boot działa!");
    }
}
