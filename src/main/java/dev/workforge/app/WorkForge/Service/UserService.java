package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.UserViewDTO;
import dev.workforge.app.WorkForge.Model.AppUser;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing application users.
 */
public interface UserService {

    /**
     * Retrieves a list of users from the database based on the provided user IDs.
     *
     * @param usernames a list of user IDs to search for
     * @return a list of matching {@link AppUser} objects; returns an empty list if input is empty
     * @throws UserNotFoundException if no users are found in the database
     */
    List<AppUser> getUsersByIds(List<Long> usernames);

    /**
     * Retrieves a user by their username.
     *
     * @param username the unique username of the user
     * @return the corresponding {@link AppUser} object
     * @throws UserNotFoundException if no user with the given username exists
     */
    AppUser getUserByUsername(String username);

    /**
     * Retrieves a list of users whose usernames start with the given prefix.
     *
     * @param prefix the username prefix to filter by
     * @return a list of {@link UserViewDTO} containing users matching the prefix
     */
    List<UserViewDTO> getUsersByPrefix(String prefix);

    /**
     * Retrieves a user by their UUID.
     *
     * @param uuid the UUID of the user
     * @return the corresponding {@link AppUser} object
     * @throws UserNotFoundException if no user with the given UUID exists
     */
    AppUser getUserByUUID(UUID uuid);
}
