package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.AppUser;

import java.util.List;

public interface UserService {

    /**
     * Retrieves a list of users from database based on the given usernames
     *
     * @param usernames A list of usernames to search for
     * @return A list of matching objects. Returns an empty list if the input list is empty.
     * @throws UserNotFoundException if no users are found in the database.
     */
    List<AppUser> getUsersByIds(List<Long> usernames);

    AppUser getUserByUsername(String username);
}
