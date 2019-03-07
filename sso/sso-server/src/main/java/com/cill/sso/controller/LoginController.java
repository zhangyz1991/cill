package com.cill.sso.controller;

import com.cill.sso.component.TokenRepository;
import com.cill.sso.entity.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes(value = {"username","password"},types={String.class})
public class LoginController {
    private Logger log = LoggerFactory.getLogger(LoginController.class);
    private final ObjectMapper objectMapper;

    private Map<String,TokenRepository> tokenRepositoryMap = new HashMap<>();
    private Map<String,String> ticketMap = new HashMap<>();

    @Autowired
    public LoginController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RequestMapping(value = "/login",method=RequestMethod.GET)
    public String login(Map<String,Object> map, @RequestParam(required = false) String source, @CookieValue(value = "CASTGC",required = false) String castgc,HttpServletResponse response) {
        TokenRepository tokenRepository = tokenRepositoryMap.get(castgc);
        if(StringUtils.isEmpty(castgc)||null==tokenRepository){
            map.put("source",source);
            castgc = "TGT-"+UUID.randomUUID().toString();
            Cookie cookie = new Cookie("CASTGC",castgc);
            //cookie.setSecure(true); true意味着"指示浏览器仅通过 HTTPS 连接传回 cookie
            cookie.setMaxAge(3000);
            cookie.setHttpOnly(true);
            cookie.setDomain("www.sso.com");
            cookie.setPath("/sso");
            response.addCookie(cookie);
            return "login.html";
        }
        if(StringUtils.isEmpty(source)){
            return "index.html";
        }else{
            String st_ticket = tokenRepository.generateTicket(source);
            ticketMap.put(st_ticket,castgc);
            return "redirect:"+source+"?ticket="+st_ticket;
        }
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@CookieValue(value = "CASTGC") String castgc, @RequestParam(required = false) String source, @RequestParam String username, @RequestParam String password){
        if(!username.equals(password)){
            return "login.html";
        }
        TokenRepository tokenRepository = new TokenRepository(castgc);
        tokenRepositoryMap.put(castgc,tokenRepository);

        UserInfo user = new UserInfo(username);
        tokenRepository.setUserInfo(user);

        if(!StringUtils.isEmpty(source)){
            String st_ticket = tokenRepository.generateTicket(source);
            ticketMap.put(st_ticket,castgc);
            return "redirect:"+source+"?ticket="+st_ticket;
        }

        return "index.html";
    }

    @RequestMapping(value = "/checkToken")
    @ResponseBody
    public String checkToken(@RequestBody String ticket){
        String castgc = ticketMap.get(ticket);
        TokenRepository tokenRepository = tokenRepositoryMap.get(castgc);
        Map<String,Object> map = new HashMap<>();
        if(tokenRepository!=null){
            map.put("valid","true");
            map.put("userinfo",tokenRepository.getUserInfo());
        }else{
            map.put("valid","false");
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

