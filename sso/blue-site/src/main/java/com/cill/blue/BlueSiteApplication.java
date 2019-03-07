package com.cill.blue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan({"com.cill.blue","com.cill.sso.filter"})
public class BlueSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlueSiteApplication.class, args);
    }

}
