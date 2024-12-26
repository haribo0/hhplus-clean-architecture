package io.hhplus.architecture.infrastructure.user;

import io.hhplus.architecture.domain.user.User;
import io.hhplus.architecture.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepository.findById(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return jpaRepository.existsById(userId);
    }

    @Override
    public void save(User user) {
        jpaRepository.save(user);
    }

    @Override
    public void saveAll(List<User> users) {
        jpaRepository.saveAll(users);
    }


}