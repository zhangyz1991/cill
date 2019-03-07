package com.cill.red;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan({"com.cill.red","com.cill.sso.filter"})
public class RedSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedSiteApplication.class, args);
    }

}
