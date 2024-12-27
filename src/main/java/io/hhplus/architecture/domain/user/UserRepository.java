package io.hhplus.architecture.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean existsById(Long userId);

    void save(User user);

    void saveAll(List<User> users);

    Optional<User> findById(Long userId);

}
