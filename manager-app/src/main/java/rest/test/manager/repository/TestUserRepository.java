package rest.test.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rest.test.manager.entity.TestUser;

import java.util.Optional;

public interface TestUserRepository extends JpaRepository<TestUser, Integer> {

    Optional<TestUser> findByUsername(String username);

}
