package com.cill.sso.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Value("http://${sso.server.address}:${sso.server.port}${sso.server.server.context-path}")
    public String ssoServer;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        log.info("Into AuthFilter!");
        String sourceUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        /*if(!StringUtils.isEmpty(queryString)){
            sourceUrl += queryString;
        }*/
        if(request.getSession().getAttribute("username")==null){
            if(request.getParameter("ticket")!=null){
                String ticket = request.getParameter("ticket");
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.postForObject(ssoServer+"/checkToken",ticket, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode readTree = objectMapper.readTree(result);
                JsonNode validNode = readTree.get("valid");
                if(validNode!=null && validNode.asBoolean())
                    request.getSession().setAttribute("username", readTree.get("userinfo").get("username").asText());
                else{
                    response.sendRedirect(ssoServer+"/login?source="+sourceUrl);
                    return;
                }
            }else{
                response.sendRedirect(ssoServer+"/login?source="+sourceUrl);
                return;
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) {

    }

}

