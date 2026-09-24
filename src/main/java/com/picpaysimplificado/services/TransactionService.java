package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;

@Service 
public class TransactionService {

    @Autowired 
    private UserService userService;
    @Autowired 
    private TransactionRepository transactionRepository;
    @Autowired 
    private RestTemplate restTemplate;
    @Autowired
    private NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception{
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());
        
        boolean isAuthorized = this.authorizeTransaction(sender, transactionDTO.value());
        if(!isAuthorized){
            throw new Exception("Transação não autoirzada");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        transactionRepository.save(transaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transação realizada cpm sucesso");
        this.notificationService.sendNotification(receiver, "Transação recebida cpm sucesso");

        return transaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizarionResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if(authorizarionResponse.getStatusCode() == HttpStatus.OK){
            String message = (String)authorizarionResponse.getBody().get("status");
            return "success".equalsIgnoreCase(message);
        }else return false;
    }

}
