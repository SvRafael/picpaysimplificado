package com.picpaysimplificado.picpaysimplificado.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    
    @Test
    void findUserByDocument() {

    }

}
