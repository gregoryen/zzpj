package com.example.zzpj.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    User getById(Long id);
    User getBySteamId(Long steamID);
    User getByLogin(String email);
    List<User> getAllById(long id);



}
