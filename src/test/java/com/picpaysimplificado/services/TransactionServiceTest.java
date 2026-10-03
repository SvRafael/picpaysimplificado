package com.picpaysimplificado.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.repositories.TransactionRepository;

public class TransactionServiceTest {

    @Mock  
    private UserService userService;
    @Mock  
    private TransactionRepository transactionRepository;
    @Mock 
    private NotificationService notificationService;
    @Mock 
    private AuthorizationService authService;
    @Autowired
    @InjectMocks 
    private TransactionService transactionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create transaction sucessfully when everything is OK")
    void createTransactionCase1() throws Exception{
        User sender = new User(1L, "Rafael", "Souza", "44521587456", "rafael@gmail.com","1234", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Daniela", "Souza", "44521587452", "daniela@gmail.com","1234", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);
        when(authService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request =  new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(request);

        verify(transactionRepository, times(1)).save(any());

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso");
        verify(notificationService, times(1)).sendNotification(receiver, "Transação recebida com sucesso");


    }

    @Test
    @DisplayName("Should throw Exception when Transaction is not allowed")
    void createTransactionCase2() throws Exception{
        User sender = new User(1L, "Rafael", "Souza", "44521587456", "rafael@gmail.com","1234", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Daniela", "Souza", "44521587452", "daniela@gmail.com","1234", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);
        when(authService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            TransactionDTO request =  new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(request);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }

}
