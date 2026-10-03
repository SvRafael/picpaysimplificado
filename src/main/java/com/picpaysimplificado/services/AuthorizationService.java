package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.user.User;

@Service 
public class AuthorizationService {
    
    @Autowired 
    private RestTemplate restTemplate;

    
    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizarionResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if(authorizarionResponse.getStatusCode() == HttpStatus.OK){
            String message = (String)authorizarionResponse.getBody().get("status");
            return "success".equalsIgnoreCase(message);
        }else return false;
    }

}
