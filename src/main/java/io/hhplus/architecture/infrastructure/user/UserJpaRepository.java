package io.hhplus.architecture.infrastructure.user;

import io.hhplus.architecture.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
