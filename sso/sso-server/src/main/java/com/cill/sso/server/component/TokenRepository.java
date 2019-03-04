package com.cill.sso.server.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenRepository {
    private Map<String,String> repositoryMap = new HashMap<String,String>();

    public String getToken(String tiket){
        return repositoryMap.get(tiket);
    }

    public void setToken(String tiket, String sessionId){
        repositoryMap.put(tiket,sessionId);
    }
}
