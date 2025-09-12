package it.dtd.note.service;

import it.dtd.note.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String key) throws UsernameNotFoundException {
        it.dtd.note.model.User u = userRepository.findByEmail(key).orElseThrow(() -> new UsernameNotFoundException(key));
        return User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole().toString()).build();
    }
}
