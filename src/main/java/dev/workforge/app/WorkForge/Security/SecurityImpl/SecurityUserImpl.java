package dev.workforge.app.WorkForge.Security.SecurityImpl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Model.Permission;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;


public class SecurityUserImpl implements SecurityUser {
    private final long id;
    private final String username;
    private final String password;
    private Map<Long, Set<Permission>> permissionMap = new HashMap<>();


    public SecurityUserImpl(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    @JsonCreator
    public SecurityUserImpl(
                        @JsonProperty("id") long id,
                        @JsonProperty("username") String username,
                        @JsonProperty("password") String password,
                        @JsonProperty("permissionMap") Map<Long, Set<Permission>> permissionMap
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permissionMap = permissionMap != null ? permissionMap : new HashMap<>();
    }

    public String computeChecksum() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ObjectMapper mapper = new ObjectMapper();

            String input = username + mapper.writeValueAsString(new TreeMap<>(permissionMap));

            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error computing checksum", e);
        }
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Map<Long, Set<Permission>> getPermissionMap() {
        return Collections.unmodifiableMap(permissionMap);
    }

    public long getId() {
        return id;
    }
}
