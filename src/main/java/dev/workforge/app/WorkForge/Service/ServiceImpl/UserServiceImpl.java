package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Exceptions.UserNotFoundException;
import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Repository.UserRepository;
import dev.workforge.app.WorkForge.Security.SecurityImpl.SecurityUserImpl;
import dev.workforge.app.WorkForge.Service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        if (usernames.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppUser> appUsers = userRepository.findUsersByUsername(usernames);
        if (appUsers.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        return appUsers;
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }
}
