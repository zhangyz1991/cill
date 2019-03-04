package com.cill.sso.server.controller;

import com.cill.sso.server.component.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes(value = {"username","password"},types={String.class})
public class LoginController {
    private Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/login")
    public String login(HttpSession session, Map<String,Object> map, @RequestParam String username, @RequestParam String password){
        String ticket = UUID.randomUUID().toString();
        map.put("ticket",ticket);
        tokenRepository.setToken(ticket,username);
        map.put("username",username);
        log.info("login:"+username);
        return "redirect:http://www.red.com:8081/red/index?ticket="+ticket;
    }

    @RequestMapping(value = "/checkToken")
    @ResponseBody
    public String checkToken(@RequestBody String ticket){
        Map<String,String> map = new HashMap<String,String>();
        if(tokenRepository.getToken(ticket)!=null){
            map.put("valid","true");
            map.put("ticket",ticket);
            map.put("username",tokenRepository.getToken(ticket));
        }else{
            map.put("valid","false");
            map.put("ticket",ticket);
        }

        String result = "";
        try{
            result = objectMapper.writeValueAsString(map);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return result;
    }
}

