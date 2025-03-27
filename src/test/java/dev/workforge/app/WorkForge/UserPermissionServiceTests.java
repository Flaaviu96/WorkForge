package dev.workforge.app.WorkForge;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.PermissionService;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.ServiceImpl.UserPermissionServiceImpl;
import dev.workforge.app.WorkForge.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserPermissionServiceTests {

    @Mock
    PermissionService permissionService;

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    UserService userService;

    @Mock
    ProjectService projectService;

    @Mock
    UserSessionService userSessionService;

    @Spy
    UserPermission firstUser;

    @Spy
    UserPermission secondUser;

    @InjectMocks
    UserPermissionServiceImpl  userPermissionService;

    @BeforeEach
    public void setup() {
        List<Permission> permissionList = List.of(createPermission(PermissionType.WRITE, 1L), createPermission(PermissionType.READ, 2L));
        when(permissionService.getPermissionsByPermissionType(anyList())).thenReturn(permissionList);

        List<AppUser> appUsers = List.of(createAppUser("ditas", "ditas",1L), createAppUser("ditas1","diitas", 2L));
        when(userService.getUsersByUsernames(anyList())).thenReturn(appUsers);

        Optional<Project> project = Optional.ofNullable(createProjectOnlyWithName("Test", 1L));
        when(projectService.getProjectByProjectId(anyLong())).thenReturn(project);
        firstUser = createUserPermission(appUsers.get(0), Set.of(createPermission(PermissionType.READ, 2L)), 1L, project.get());
        secondUser = createUserPermission(appUsers.get(1), Set.of(createPermission(PermissionType.WRITE, 1L)), 2L, project.get());
        List<UserPermission> userPermissions = List.of(firstUser, secondUser);
        when(userPermissionRepository.findByUsersIdsAndProjectId(anyList(), anyLong())).thenReturn(userPermissions);

        doNothing().when(userSessionService).updatePermissionSession(anyString());
    }

    @Test
    public void testAssignNewPermissions() {
        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.WRITE, "ditas");
        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.READ, "ditas1");
        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);

        verify(userPermissionRepository, times(2)).save(any());
    }

    @Test
    public void testAssignSamePermissions() {
        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, "ditas");
        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, "ditas1");
        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);

        verify(userPermissionRepository, times(0)).save(any());
    }

    @Test
    public void testAssignPermissionsWithInvalidProject() {
        long invalidProjectId = 999L;
        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, "ditas");
        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, "ditas1");
        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(invalidProjectId, permissionDTO, permissionDTO1);
        when(projectService.getProjectByProjectId(invalidProjectId)).thenReturn(Optional.empty());

        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);

        verify(userPermissionRepository, times(0)).save(any());
    }

    @Test
    public void testAssignPermissionsWithInvalidUsernames() {
        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, "unknown");
        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, "unknown1");
        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
        when(userService.getUsersByUsernames(anyList())).thenReturn(Collections.emptyList());
        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);

        verify(userPermissionRepository, times(0)).save(any());
    }

    private Permission createPermission(PermissionType permissionType, long Id) {
        return Permission.builder()
                .id(Id)
                .permissionType(permissionType)
                .build();
    }

    private AppUser createAppUser(String username, String password, long id) {
        return AppUser.builder()
                .username(username)
                .password(password)
                .id(id)
                .build();
    }




    private PermissionDTO createPermissionDTO(PermissionType type, String username) {
        return PermissionDTO.builder()
                .permissionType(type)
                .userName(username)
                .build();
    }

    private ProjectPermissionsDTO createProjectPermissionsDTO(long projectId, PermissionDTO... permissions) {
        return ProjectPermissionsDTO.builder()
                .permissionDTO(List.of(permissions))
                .projectId(projectId)
                .build();
    }

    private Project createProjectOnlyWithName(String projectName, long id) {
        return Project.builder()
                .projectName(projectName)
                .id(id)
                .build();
    }
}
