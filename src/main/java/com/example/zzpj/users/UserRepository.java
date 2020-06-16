package com.example.zzpj.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getById(Long id);
    User getBySteamId(Long steamID);
    User getByLogin(String email);
    List<User> getAllById(long id);
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
}
