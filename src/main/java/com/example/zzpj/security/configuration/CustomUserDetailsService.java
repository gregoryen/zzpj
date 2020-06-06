package com.example.zzpj.security.configuration;


import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.getByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + login);
        }

        boolean enabled = true;
        boolean accountNotExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), enabled, accountNotExpired, credentialsNonExpired, accountNonLocked, getAuthorities(user));
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return Arrays.asList(new SimpleGrantedAuthority("user"));
    }

}
