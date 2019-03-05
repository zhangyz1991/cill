package com.cill.sso.client.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(AuthFilter.class);

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        log.info("Into AuthFilter!");

        if(request.getSession().getAttribute("username")==null){
            log.info("ticket:"+request.getParameter("ticket"));
            if(request.getParameter("ticket")!=null){
                String ticket = request.getParameter("ticket");
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.postForObject("http://www.sso.com:8080/sso/checkToken",ticket, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode readTree = objectMapper.readTree(result);
                JsonNode validNode = readTree.get("valid");
                if(validNode!=null && validNode.asBoolean()){
                    request.getSession().setAttribute("username",readTree.get("username").asText());
                    log.info("set sessionAttribute('username')");
                }else{
                    response.sendRedirect("http://www.sso.com:8080/sso/login.html?source=http://www.red.com:8081/red/index");
                    return;
                }
            }else{
                response.sendRedirect("http://www.sso.com:8080/sso/login.html?source=http://www.red.com:8081/red/index");
                return;
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}

