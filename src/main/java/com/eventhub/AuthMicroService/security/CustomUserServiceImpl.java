package com.eventhub.AuthMicroService.security;

import com.eventhub.AuthMicroService.dao.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepo;

    public CustomUserServiceImpl(UserRepository userRepo, CustomUserDetails userDetails) {
        this.userRepo = userRepo;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new CustomUserDetails(userRepo.findByUsername(username).get());
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(username);
        }
    }
}
