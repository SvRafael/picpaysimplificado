package com.picpaysimplificado.picpaysimplificado.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should get user sucessfully from DB")
    void findUserByDocumentCase1() {
        String document = "44521365874";
        UserDTO data = new UserDTO("Rafael", "Souza", document, new BigDecimal(10), "rafael@gmail.com", "4444", UserType.COMMON);
        this.createUser(data);

        Optional<User> result =  this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not get user from DB when user not exists")
    void findUserByDocumentCase2() {
        String document = "44521365874";

        Optional<User> result =  this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue();
    }

    private User createUser(UserDTO data){
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }

}
