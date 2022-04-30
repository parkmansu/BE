package com.sparta.odict.security.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.odict.security.UserDetailsServiceImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormLoginFilter extends UsernamePasswordAuthenticationFilter {
    final private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    public FormLoginFilter(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest;

        try {
            JsonNode requestBody = objectMapper.readTree(request.getInputStream());
            System.out.println("username : " + requestBody.get("username").asText());
            System.out.println("password : " + requestBody.get("pwd").asText());
            String username = requestBody.get("username").asText();
            String password = requestBody.get("pwd").asText();
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
        } catch (Exception e) {
            response.setHeader("400","username, password 입력이 필요합니다. (JSON)");
            //message body
            response.getWriter().write("username, password 입력이 필요합니다.");
            throw new IllegalArgumentException("username, password 입력이 필요합니다. (JSON)");
        }

        try {
            JsonNode requestBody = objectMapper.readTree(request.getInputStream());
            System.out.println("username : " + requestBody.get("username").asText());
            //System.out.println("password : " + requestBody.get("pwd").asText());
            String username = requestBody.get("username").asText();
            //String password = requestBody.get("pwd").asText();
            userDetailsServiceImpl.findUser(username);

        } catch (Exception e) {
            response.setHeader("400", "can't find username.");
            //message body
            response.getWriter().println("can't find username.");
            throw new IllegalArgumentException("username 입력이 잘못되었습니다.");
        }

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
