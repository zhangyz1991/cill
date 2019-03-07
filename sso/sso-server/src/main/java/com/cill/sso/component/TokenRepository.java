package com.cill.sso.component;

import com.cill.sso.entity.UserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenRepository {

    public TokenRepository(String castgc) {
        this.castgc = castgc;
    }

    private String castgc;

    private UserInfo userInfo;

    private Map<String,String> ticketAppMap = new HashMap<String, String>();


    public String getCastgc() {
        return castgc;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Map<String, String> getTicketAppMap() {
        return ticketAppMap;
    }

    public void setTicketAppMap(Map<String, String> ticketAppMap) {
        this.ticketAppMap = ticketAppMap;
    }

    public String generateTicket(String source) {
        String st_ticket = "ST-"+UUID.randomUUID().toString();
        ticketAppMap.put(st_ticket,source);
        return st_ticket;
    }
}
