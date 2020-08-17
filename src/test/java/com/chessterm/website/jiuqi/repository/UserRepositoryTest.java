package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private UserRepository repository;

    @Test
    public void getLastUser() {
        manager.clear();
        manager.persist(new User(10099));
        User answer = new User(10101);
        manager.persist(answer);
        manager.flush();

        User result1 = repository.findByIdGreaterThanEqualOrderByIdDesc(10101);
        Assertions.assertThat(result1).isEqualTo(answer);
        User result2 = repository.findByIdGreaterThanEqualOrderByIdDesc(10102);
        Assertions.assertThat(result2).isNull();
    }
}
