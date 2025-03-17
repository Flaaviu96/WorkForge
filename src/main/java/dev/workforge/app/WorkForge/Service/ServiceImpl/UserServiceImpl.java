package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Repository.UserRepository;
import dev.workforge.app.WorkForge.Security.SecurityImpl.SecurityUserImpl;
import dev.workforge.app.WorkForge.Service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
        return new SecurityUserImpl(appUser);
    }

    @Override
    public List<AppUser> getUsersByUsernames(List<String> usernames) {
        return List.of();
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return null;
    }
}
