//package dev.workforge.app.WorkForge;
//
//import dev.workforge.app.WorkForge.DTO.PermissionDTO;
//import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
//import dev.workforge.app.WorkForge.Model.*;
//import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
//import dev.workforge.app.WorkForge.Security.UserSessionService;
//import dev.workforge.app.WorkForge.Service.UserPermission.PermissionService;
//import dev.workforge.app.WorkForge.Service.ProjectService;
//import dev.workforge.app.WorkForge.Service.ServiceImpl.UserPermission.UserPermissionServiceImpl;
//import dev.workforge.app.WorkForge.Service.UserPermission.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//public class UserPermissionServiceTests {
//
//    @Mock
//    PermissionService permissionService;
//
//    @Mock
//    private UserPermissionRepository userPermissionRepository;
//
//    @Mock
//    UserService userService;
//
//    @Mock
//    ProjectService projectService;
//
//    @Mock
//    UserSessionService userSessionService;
//
//    @Spy
//    UserPermission firstUser;
//
//    @Spy
//    UserPermission secondUser;
//
//    @InjectMocks
//    UserPermissionServiceImpl  userPermissionService;
//
//    @BeforeEach
//    public void setup() {
//        List<Permission> permissionList = List.of(createPermission(PermissionType.WRITE, 1L), createPermission(PermissionType.READ, 2L));
//        when(permissionService.getPermissionsByPermissionType(anyList())).thenReturn(permissionList);
//
//        List<AppUser> appUsers = List.of(createAppUser("ditas", "ditas",1L), createAppUser("ditas1","diitas", 2L));
//        when(userService.getUsersByIds(anyList())).thenReturn(appUsers);
//
//        Optional<Project> project = Optional.ofNullable(createProjectOnlyWithName("Test", 1L));
//        when(projectService.getProjectByProjectId(anyLong())).thenReturn(project);
//        firstUser = createUserPermission(appUsers.get(0), new HashSet<>(Set.of(createPermission(PermissionType.READ, 2L))), 1L, project.get());
//        secondUser = createUserPermission(appUsers.get(1), new HashSet<>(Set.of(createPermission(PermissionType.WRITE, 1L))), 2L, project.get());
//        List<UserPermission> userPermissions = List.of(firstUser, secondUser);
//        when(userPermissionRepository.findByUsersIdsAndProjectId(anyList(), anyLong())).thenReturn(userPermissions);
//
//        doNothing().when(userSessionService).updatePermissionSession(anyString());
//    }
//
//    @Test
//    public void testAssignNewPermissions() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.WRITE, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.READ, 2L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//
//        Set<Permission> beforeUpdateFromFirstUser = firstUser.getPermissions();
//        Set<Permission> beforeUpdateFromSecondUser = secondUser.getPermissions();
//
//        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);
//
//        assertNotEquals(beforeUpdateFromFirstUser, firstUser.getPermissions(), "Permissions should be updated");
//        assertNotEquals(beforeUpdateFromSecondUser, secondUser.getPermissions(), "Permissions should be updated");
//        verify(userPermissionRepository, times(1)).saveAll(anyList());
//    }
//
//    @Test
//    public void testAssignNewPermissionToOneUser() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.WRITE, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 2L);
//
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//
//        Set<Permission> beforeUpdateFromFirstUser = firstUser.getPermissions();
//        Set<Permission> beforeUpdateFromSecondUser = secondUser.getPermissions();
//
//        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);
//
//        assertNotEquals(beforeUpdateFromFirstUser, firstUser.getPermissions(), "Permissions should be updated");
//        assertEquals(beforeUpdateFromSecondUser, secondUser.getPermissions(), "Permissions should be updated");
//        verify(userPermissionRepository, times(1)).saveAll(anyList());
//    }
//
//    @Test
//    public void testAssignSamePermissions() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 2L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);
//
//        verify(userPermissionRepository, times(0)).save(any());
//    }
//
//    @Test
//    public void testAssignPermissionsWithInvalidProject() {
//        long invalidProjectId = 999L;
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 2L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(invalidProjectId, permissionDTO, permissionDTO1);
//        when(projectService.getProjectByProjectId(invalidProjectId)).thenReturn(Optional.empty());
//
//        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);
//
//        verify(userPermissionRepository, times(0)).save(any());
//    }
//
//    @Test
//    public void testAssignPermissionsWithInvalidUsernames() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 999L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 333L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//        when(userService.getUsersByIds(anyList())).thenReturn(Collections.emptyList());
//        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);
//
//        verify(userPermissionRepository, times(0)).save(any());
//    }
//
//    @Test
//    public void testRemovePermissions() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 2L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//
//        Set<Permission> beforeUpdateFromFirstUser = firstUser.getPermissions();
//        Set<Permission> beforeUpdateFromSecondUser = secondUser.getPermissions();
//        when(userSessionService.hasKey(anyString())).thenReturn(true);
//        userPermissionService.removeProjectPermissionsFromUsers(projectPermissionsDTO);
//        assertNotEquals(beforeUpdateFromFirstUser, firstUser.getPermissions(), "Permissions should be updated");
//        assertNotEquals(beforeUpdateFromSecondUser, secondUser.getPermissions(), "Permissions should be updated");
//
//        verify(userPermissionRepository, times(1)).deleteAll(anyList());
//        verify(userSessionService, times(2)).updatePermissionSession(anyString());
//    }
//
//    @Test
//    public void testRemovePermissionsWithInvalidProject() {
//        long invalidProjectId = 999L;
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 2L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(invalidProjectId, permissionDTO, permissionDTO1);
//        when(projectService.getProjectByProjectId(invalidProjectId)).thenReturn(Optional.empty());
//
//        userPermissionService.removeProjectPermissionsFromUsers(projectPermissionsDTO);
//
//        verify(userPermissionRepository, times(0)).save(any());
//        verify(userSessionService, times(0)).updatePermissionSession(anyString());
//    }
//
//    @Test
//    public void testRemovePermissionsWithOneInvalidUser() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 1L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 999L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//        when(userSessionService.hasKey("ditas")).thenReturn(true);
//        Set<Permission> beforeUpdateFromFirstUser = firstUser.getPermissions();
//
//        userPermissionService.removeProjectPermissionsFromUsers(projectPermissionsDTO);
//        assertNotEquals(beforeUpdateFromFirstUser, firstUser.getPermissions(), "Permissions should be updated");
//
//        verify(userPermissionRepository, times(1)).deleteAll(any());
//        verify(userSessionService, times(1)).updatePermissionSession(anyString());
//    }
//
//    @Test
//    public void testRemovePermissionsWithInvalidUsers() {
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 888L);
//        PermissionDTO permissionDTO1 = createPermissionDTO(PermissionType.WRITE, 999L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO, permissionDTO1);
//
//        userPermissionService.removeProjectPermissionsFromUsers(projectPermissionsDTO);
//
//        verify(userPermissionRepository, times(0)).saveAll(anyList());
//        verify(userSessionService, times(0)).updatePermissionSession(anyString());
//    }
//
//    @Test
//    public void testRemovePermissionsIncludeTheCompleteDelete() {
//        // If in the UserPermission record does not contains any permissions, to be deleted.
//        PermissionDTO permissionDTO = createPermissionDTO(PermissionType.READ, 1L);
//        ProjectPermissionsDTO projectPermissionsDTO = createProjectPermissionsDTO(1L, permissionDTO);
//        when(userSessionService.hasKey(anyString())).thenReturn(true);
//        userPermissionService.removeProjectPermissionsFromUsers(projectPermissionsDTO);
//
//        verify(userPermissionRepository, times(1)).deleteAll(any());
//        verify(userSessionService, times(1)).updatePermissionSession(anyString());
//    }
//
//    @Test
//    public void test() {
//        List<Permission> permissionList = List.of(createPermission(PermissionType.WRITE, 1L), createPermission(PermissionType.READ, 2L));
//        List<List<Permission>> test = List.of(permissionList);
//        List<Permission> permissionList1 = List.of(createPermission(PermissionType.WRITE, 1L), createPermission(PermissionType.READ, 2L), createPermission(PermissionType.TEST, 3L));
//
//        Set<Permission> permissions = permissionList.stream()
//                .filter(permission -> permissionList1.stream()
//                        .allMatch(permission1 -> permission.getPermissionType() == permission1.getPermissionType()))
//                        .collect(Collectors.toSet());
//
//        test.stream()
//                .filter(permissions1 -> permissionList1.stream()
//                        .allMatch(permission -> permissions1.))
//
////        Set<Permission> test = permissionList1.stream()
////                        .filter(permission -> permissionList.stream()
////                                .noneMatch(permission1 -> permission1.getPermissionType() == permission.getPermissionType()))
////                                .collect(Collectors.toSet());
//
//        System.out.println(permissions.toString());
//
//    }
//
//    private Permission createPermission(PermissionType permissionType, long Id) {
//        return Permission.builder()
//                .id(Id)
//                .permissionType(permissionType)
//                .build();
//    }
//
//    private UserPermission createUserPermission(AppUser appUser, Set<Permission> permissions, long id, Project project) {
//        return UserPermission.builder()
//                .user(appUser)
//                .permissions(permissions)
//                .id(id)
//                .project(project)
//                .build();
//    }
//
//    private AppUser createAppUser(String username, String password, long id) {
//        return AppUser.builder()
//                .username(username)
//                .password(password)
//                .id(id)
//                .build();
//    }
//
//    private PermissionDTO createPermissionDTO(PermissionType type, long userId) {
//        return PermissionDTO.builder()
//                .permissionType(type)
//                .userId(userId)
//                .build();
//    }
//
//    private ProjectPermissionsDTO createProjectPermissionsDTO(long projectId, PermissionDTO... permissions) {
//        return ProjectPermissionsDTO.builder()
//                .permissionDTO(List.of(permissions))
//                .projectId(projectId)
//                .build();
//    }
//
//    private Project createProjectOnlyWithName(String projectName, long id) {
//        return Project.builder()
//                .projectName(projectName)
//                .id(id)
//                .build();
//    }
//}
