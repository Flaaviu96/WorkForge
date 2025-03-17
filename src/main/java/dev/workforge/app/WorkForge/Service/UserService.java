package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.AppUser;

import java.util.List;

public interface UserService {

    List<AppUser> getUsersByUsernames(List<String> usernames);

    AppUser getUserByUsername(String username);
}
