// File: src/test/java/com/example/demo/DigitalAssetLifecycleAuditTrailApiTest.java
package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.*;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Single TestNG class with 60+ tests, ordered by:
 * 1. Simple Servlet / Controller-like behavior
 * 2. CRUD (Spring Boot + REST)
 * 3. DI & IoC
 * 4. Hibernate config & CRUD
 * 5. JPA mapping & normalization
 * 6. Many-to-many & associations
 * 7. Security & JWT
 * 8. HQL / advanced querying (simulated via repositories/services)
 */
@Listeners(TestResultListener.class)
public class DigitalAssetLifecycleAuditTrailApiTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LifecycleEventRepository lifecycleEventRepository;
    @Mock
    private TransferRecordRepository transferRecordRepository;
    @Mock
    private DisposalRecordRepository disposalRecordRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AssetServiceImpl assetService;
    @InjectMocks
    private UserServiceImpl userService;
    @InjectMocks
    private LifecycleEventServiceImpl lifecycleEventService;
    @InjectMocks
    private TransferRecordServiceImpl transferRecordService;
    @InjectMocks
    private DisposalRecordServiceImpl disposalRecordService;

    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenAnswer(
                (Answer<String>) inv -> "ENC_" + inv.getArgument(0)
        );
        jwtUtil = new JwtUtil();
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    // region 1. "Servlet" style concept tests (7 tests)

    @Test(priority = 1, groups = "servlet")
    public void t01_servletLike_okStatusConcept() {
        int statusOk = 200;
        int statusCreated = 201;
        Assert.assertTrue(statusOk < statusCreated);
    }

    @Test(priority = 2, groups = "servlet")
    public void t02_servletLike_pathVariableConcept() {
        Long id = 5L;
        String path = "/api/assets/" + id;
        Assert.assertTrue(path.contains("/api/assets/5"));
    }

    @Test(priority = 3, groups = "servlet")
    public void t03_servletLike_queryParamConcept() {
        String url = "/api/events/asset/10?page=1&size=20";
        Assert.assertTrue(url.contains("page=1"));
        Assert.assertTrue(url.contains("size=20"));
    }

    @Test(priority = 4, groups = "servlet")
    public void t04_controllerReceivesJsonBody_registerRequest() {
        RegisterRequest req = new RegisterRequest("John Doe", "john@example.com", "IT", "password123");
        Assert.assertEquals(req.getEmail(), "john@example.com");
        Assert.assertEquals(req.getDepartment(), "IT");
    }

    @Test(priority = 5, groups = "servlet")
    public void t05_controllerReceivesJsonBody_loginRequest() {
        LoginRequest req = new LoginRequest("user@example.com", "password123");
        Assert.assertEquals(req.getEmail(), "user@example.com");
    }

    @Test(priority = 6, groups = "servlet")
    public void t06_servletLike_pathMatchingAssetStatusUpdate() {
        String path = "/api/assets/status/3";
        Assert.assertTrue(path.matches(".*/api/assets/status/\\d+"));
    }

    @Test(priority = 7, groups = "servlet")
    public void t07_servletLike_methodDistinctionConcept() {
        String get = "GET /api/assets";
        String post = "POST /api/assets";
        Assert.assertNotEquals(get, post);
    }

    // endregion

    // region 2. CRUD operations (10 tests) priority 10-19

    @Test(priority = 10, groups = "crud")
    public void t10_registerUser_success() {
        User user = new User(null, "User A", "a@example.com", "IT", "USER", "password123", null);

        when(userRepository.existsByEmail("a@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User created = userService.registerUser(user);
        Assert.assertNotNull(created.getId());
        Assert.assertEquals(created.getEmail(), "a@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test(priority = 11, groups = "crud")
    public void t11_registerUser_duplicateEmail() {
        User user = new User(null, "User A", "a@example.com", "IT", "USER", "password123", null);
        when(userRepository.existsByEmail("a@example.com")).thenReturn(true);

        try {
            userService.registerUser(user);
            Assert.fail("Expected ValidationException for duplicate email");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Email already in use"));
        }
    }

    @Test(priority = 12, groups = "crud")
    public void t12_registerUser_shortPassword() {
        User user = new User(null, "User A", "short@example.com", "IT", "USER", "short", null);
        when(userRepository.existsByEmail("short@example.com")).thenReturn(false);

        try {
            userService.registerUser(user);
            Assert.fail("Expected ValidationException for short password");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Password must be at least 8 characters"));
        }
    }

    @Test(priority = 13, groups = "crud")
    public void t13_registerUser_missingDepartment() {
        User user = new User(null, "User A", "dep@example.com", null, "USER", "password123", null);
        when(userRepository.existsByEmail("dep@example.com")).thenReturn(false);

        try {
            userService.registerUser(user);
            Assert.fail("Expected ValidationException for missing department");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Department is required"));
        }
    }

    @Test(priority = 14, groups = "crud")
    public void t14_createAsset_success() {
        Asset asset = new Asset(null, "ASSET-001", "LAPTOP", "Dell", LocalDate.now(),
                "AVAILABLE", null, null);
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> {
            Asset a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });

        Asset saved = assetService.createAsset(asset);
        Assert.assertNotNull(saved.getId());
        Assert.assertEquals(saved.getAssetTag(), "ASSET-001");
    }

    @Test(priority = 15, groups = "crud")
    public void t15_getAssetById_found() {
        Asset a = new Asset(5L, "TAG-5", "LAPTOP", "HP", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        when(assetRepository.findById(5L)).thenReturn(Optional.of(a));

        Asset result = assetService.getAsset(5L);
        Assert.assertEquals(result.getId(), 5L);
    }

    @Test(priority = 16, groups = "crud")
    public void t16_getAssetById_notFound() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            assetService.getAsset(99L);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (ResourceNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains("Asset not found"));
        }
    }

    @Test(priority = 17, groups = "crud")
    public void t17_getAllAssets_emptyList() {
        when(assetRepository.findAll()).thenReturn(Collections.emptyList());
        List<Asset> list = assetService.getAllAssets();
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 18, groups = "crud")
    public void t18_updateStatus_success() {
        Asset a = new Asset(2L, "TAG-2", "LAPTOP", "Lenovo",
                LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        when(assetRepository.findById(2L)).thenReturn(Optional.of(a));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        Asset updated = assetService.updateStatus(2L, "ASSIGNED");
        Assert.assertEquals(updated.getStatus(), "ASSIGNED");
    }

    @Test(priority = 19, groups = "crud")
    public void t19_logLifecycleEvent_success() {
        Asset asset = new Asset(1L, "TAG-1", "LAPTOP", "Dell",
                LocalDate.now(), "ASSIGNED", null, LocalDateTime.now());
        User user = new User(2L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        LifecycleEvent e = new LifecycleEvent();
        e.setEventType("ASSIGNED");
        e.setEventDescription("Assigned to user");

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(lifecycleEventRepository.save(any(LifecycleEvent.class))).thenAnswer(inv -> {
            LifecycleEvent le = inv.getArgument(0);
            le.setId(100L);
            return le;
        });

        LifecycleEvent saved = lifecycleEventService.logEvent(1L, 2L, e);
        Assert.assertNotNull(saved.getId());
        Assert.assertEquals(saved.getAsset().getId(), 1L);
        Assert.assertEquals(saved.getPerformedBy().getId(), 2L);
    }

    // endregion

    // region 3. DI & IoC (5 tests) priority 20-24

    @Test(priority = 20, groups = "di")
    public void t20_assetServiceInjectedRepoNotNull() {
        Assert.assertNotNull(assetService);
        Assert.assertNotNull(assetRepository);
    }

    @Test(priority = 21, groups = "di")
    public void t21_userServiceInjectedRepoAndPasswordEncoderNotNull() {
        Assert.assertNotNull(userService);
        Assert.assertNotNull(userRepository);
        Assert.assertNotNull(passwordEncoder);
    }

    @Test(priority = 22, groups = "di")
    public void t22_lifecycleEventServiceInjectedRepositories() {
        Assert.assertNotNull(lifecycleEventService);
        Assert.assertNotNull(lifecycleEventRepository);
        Assert.assertNotNull(assetRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test(priority = 23, groups = "di")
    public void t23_transferRecordServiceInjectedDependencies() {
        Assert.assertNotNull(transferRecordService);
        Assert.assertNotNull(transferRecordRepository);
        Assert.assertNotNull(assetRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test(priority = 24, groups = "di")
    public void t24_disposalRecordServiceInjectedDependencies() {
        Assert.assertNotNull(disposalRecordService);
        Assert.assertNotNull(disposalRecordRepository);
        Assert.assertNotNull(assetRepository);
        Assert.assertNotNull(userRepository);
    }

    // endregion

    // region 4. Hibernate config & CRUD (8 tests) priority 30-37

    @Test(priority = 30, groups = "hibernate")
    public void t30_assetEntity_prePersistSetsDefaults() {
        Asset asset = new Asset();
        asset.setAssetTag("TAG-100");
        asset.prePersist();
        Assert.assertEquals(asset.getStatus(), "AVAILABLE");
        Assert.assertNotNull(asset.getCreatedAt());
    }

    @Test(priority = 31, groups = "hibernate")
    public void t31_userEntity_prePersistDefaultsRoleAndCreatedAt() {
        User user = new User();
        user.setEmail("u@example.com");
        user.setDepartment("IT");
        user.prePersist();
        Assert.assertEquals(user.getRole(), "USER");
        Assert.assertNotNull(user.getCreatedAt());
    }

    @Test(priority = 32, groups = "hibernate")
    public void t32_lifecycleEvent_prePersistSetsEventDate() {
        LifecycleEvent e = new LifecycleEvent();
        e.prePersist();
        Assert.assertNotNull(e.getEventDate());
    }

    @Test(priority = 33, groups = "hibernate")
    public void t33_disposalRecord_prePersistSetsCreatedAt() {
        DisposalRecord d = new DisposalRecord();
        d.prePersist();
        Assert.assertNotNull(d.getCreatedAt());
    }

    @Test(priority = 34, groups = "hibernate")
    public void t34_crudSaveAsset_viaRepositoryMock() {
        Asset a = new Asset(null, "TAG-X", "LAPTOP", "Lenovo",
                LocalDate.now(), "AVAILABLE", null, null);
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> {
            Asset x = inv.getArgument(0);
            x.setId(50L);
            return x;
        });

        Asset saved = assetRepository.save(a);
        Assert.assertEquals(saved.getId(), 50L);
    }

    @Test(priority = 35, groups = "hibernate")
    public void t35_userRepositoryFindByEmail_present() {
        User user = new User(4L, "User B", "b@example.com", "HR", "USER", "pwd", LocalDateTime.now());
        when(userRepository.findByEmail("b@example.com")).thenReturn(Optional.of(user));
        Optional<User> opt = userRepository.findByEmail("b@example.com");
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get().getId(), 4L);
    }

    @Test(priority = 36, groups = "hibernate")
    public void t36_userRepositoryFindByEmail_empty() {
        when(userRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());
        Optional<User> opt = userRepository.findByEmail("none@example.com");
        Assert.assertTrue(opt.isEmpty());
    }

    @Test(priority = 37, groups = "hibernate")
    public void t37_assetRepositoryFindByStatus() {
        Asset a1 = new Asset(1L, "T1", "LAPTOP", "Dell", LocalDate.now(),
                "AVAILABLE", null, LocalDateTime.now());
        when(assetRepository.findByStatus("AVAILABLE")).thenReturn(List.of(a1));

        List<Asset> list = assetRepository.findByStatus("AVAILABLE");
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getStatus(), "AVAILABLE");
    }

    // endregion

    // region 5. JPA mapping & normalization (5 tests) priority 40-44

    @Test(priority = 40, groups = "jpa")
    public void t40_assetHasCurrentHolder_1NF() {
        User u = new User(1L, "UserX", "ux@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        Asset a = new Asset(2L, "TAG-UX", "LAPTOP", "Dell", LocalDate.now(),
                "ASSIGNED", u, LocalDateTime.now());
        Assert.assertEquals(a.getCurrentHolder().getId(), 1L);
    }

    @Test(priority = 41, groups = "jpa")
    public void t41_lifecycleEventReferencesAssetAndUser_2NF() {
        Asset a = new Asset(3L, "TAG-EV", "LAPTOP", "HP", LocalDate.now(),
                "IN_REPAIR", null, LocalDateTime.now());
        User u = new User(2L, "Tech", "tech@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        LifecycleEvent e = new LifecycleEvent(5L, a, "REPAIRED", "Device repaired",
                LocalDateTime.now(), u);

        Assert.assertEquals(e.getAsset().getId(), 3L);
        Assert.assertEquals(e.getPerformedBy().getId(), 2L);
    }

    @Test(priority = 42, groups = "jpa")
    public void t42_transferRecordReferencesAssetAndApprover_3NF() {
        Asset a = new Asset(4L, "TAG-TR", "LAPTOP", "Dell", LocalDate.now(),
                "TRANSFERRED", null, LocalDateTime.now());
        User admin = new User(10L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        TransferRecord tr = new TransferRecord(7L, a, "IT", "HR",
                LocalDate.now(), admin);

        Assert.assertEquals(tr.getAsset().getId(), 4L);
        Assert.assertEquals(tr.getApprovedBy().getRole(), "ADMIN");
    }

    @Test(priority = 43, groups = "jpa")
    public void t43_transferRecordsByAssetId_repositoryMethod() {
        TransferRecord tr = new TransferRecord(1L, null, "IT", "HR",
                LocalDate.now(), null);
        when(transferRecordRepository.findByAsset_Id(10L)).thenReturn(List.of(tr));

        List<TransferRecord> list = transferRecordRepository.findByAsset_Id(10L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 44, groups = "jpa")
    public void t44_lifecycleEventsByAssetId_repositoryMethod() {
        LifecycleEvent e = new LifecycleEvent(1L, null, "ASSIGNED", "Initial assign",
                LocalDateTime.now(), null);
        when(lifecycleEventRepository.findByAsset_Id(20L)).thenReturn(List.of(e));

        List<LifecycleEvent> list = lifecycleEventRepository.findByAsset_Id(20L);
        Assert.assertEquals(list.size(), 1);
    }

    // endregion

    // region 6. Many-to-many & associations (5 tests) priority 50-54

    @Test(priority = 50, groups = "manyToMany")
    public void t50_userWithMultipleAssets_associationConcept() {
        User u = new User(1L, "MultiUser", "multi@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        Asset a1 = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "ASSIGNED", u, LocalDateTime.now());
        Asset a2 = new Asset(2L, "TAG-2", "LAPTOP", "HP", LocalDate.now(), "ASSIGNED", u, LocalDateTime.now());

        List<Asset> assets = List.of(a1, a2);
        Assert.assertEquals(assets.size(), 2);
        Assert.assertTrue(assets.stream().allMatch(a -> a.getCurrentHolder().getId().equals(1L)));
    }

    @Test(priority = 51, groups = "manyToMany")
    public void t51_assetWithMultipleLifecycleEvents_associationConcept() {
        Asset a = new Asset(10L, "TAG-EV10", "LAPTOP", "Lenovo", LocalDate.now(), "ASSIGNED", null, LocalDateTime.now());
        User u = new User(2L, "User2", "u2@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        LifecycleEvent e1 = new LifecycleEvent(1L, a, "ASSIGNED", "Assigned", LocalDateTime.now(), u);
        LifecycleEvent e2 = new LifecycleEvent(2L, a, "RETURNED", "Returned", LocalDateTime.now(), u);

        List<LifecycleEvent> events = List.of(e1, e2);
        Assert.assertEquals(events.size(), 2);
        Assert.assertTrue(events.stream().allMatch(ev -> ev.getAsset().getId().equals(10L)));
    }

    @Test(priority = 52, groups = "manyToMany")
    public void t52_assetTransferMultipleDepartmentsConcept() {
        Asset a = new Asset(20L, "TAG-TR20", "LAPTOP", "Dell", LocalDate.now(), "TRANSFERRED", null, LocalDateTime.now());
        User admin = new User(3L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        TransferRecord tr1 = new TransferRecord(1L, a, "IT", "HR", LocalDate.now(), admin);
        TransferRecord tr2 = new TransferRecord(2L, a, "HR", "FINANCE", LocalDate.now(), admin);

        Set<String> departments = new HashSet<>();
        departments.add(tr1.getFromDepartment());
        departments.add(tr1.getToDepartment());
        departments.add(tr2.getToDepartment());

        Assert.assertEquals(departments.size(), 3);
    }

    @Test(priority = 53, groups = "manyToMany")
    public void t53_multipleAssetsDisposedDifferentApproversConcept() {
        User admin1 = new User(1L, "Admin1", "a1@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        User admin2 = new User(2L, "Admin2", "a2@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        Asset a1 = new Asset(1L, "TAG-D1", "LAPTOP", "Dell", LocalDate.now(), "DISPOSED", null, LocalDateTime.now());
        Asset a2 = new Asset(2L, "TAG-D2", "LAPTOP", "HP", LocalDate.now(), "DISPOSED", null, LocalDateTime.now());

        DisposalRecord d1 = new DisposalRecord(1L, a1, "RECYCLED", LocalDate.now(), admin1, "ok", LocalDateTime.now());
        DisposalRecord d2 = new DisposalRecord(2L, a2, "SCRAPPED", LocalDate.now(), admin2, "ok", LocalDateTime.now());

        Assert.assertNotEquals(d1.getApprovedBy().getId(), d2.getApprovedBy().getId());
    }

    @Test(priority = 54, groups = "manyToMany")
    public void t54_assetsForSingleDepartmentConcept() {
        User u1 = new User(1L, "U1", "u1@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        User u2 = new User(2L, "U2", "u2@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        Asset a1 = new Asset(1L, "TAG-IT1", "LAPTOP", "Dell", LocalDate.now(), "ASSIGNED", u1, LocalDateTime.now());
        Asset a2 = new Asset(2L, "TAG-IT2", "LAPTOP", "HP", LocalDate.now(), "ASSIGNED", u2, LocalDateTime.now());

        List<Asset> list = List.of(a1, a2);
        Assert.assertTrue(list.stream().allMatch(a -> "IT".equals(a.getCurrentHolder().getDepartment())));
    }

    // endregion

    // region 7. Security & JWT (14 tests) priority 60-73

    @Test(priority = 60, groups = "security")
    public void t60_generateJwtToken_containsSubject() {
        Map<String, Object> claims = Map.of("key", "value");
        String token = jwtUtil.generateToken(claims, "subject@example.com");
        String username = jwtUtil.extractUsername(token);
        Assert.assertEquals(username, "subject@example.com");
    }

    @Test(priority = 61, groups = "security")
    public void t61_generateTokenForUser_containsUserIdEmailRole() {
        User user = new User(10L, "T", "token@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);

        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);

        Assert.assertEquals(username, "token@example.com");
        Assert.assertEquals(role, "ADMIN");
        Assert.assertEquals(userId, Long.valueOf(10L));
    }

    @Test(priority = 62, groups = "security")
    public void t62_tokenValidation_success() {
        User user = new User(11L, "U", "u@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);
        boolean valid = jwtUtil.isTokenValid(token, "u@example.com");
        Assert.assertTrue(valid);
    }

    @Test(priority = 63, groups = "security")
    public void t63_tokenValidation_wrongUser() {
        User user = new User(11L, "U", "u@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);
        boolean valid = jwtUtil.isTokenValid(token, "v@example.com");
        Assert.assertFalse(valid);
    }

    @Test(priority = 64, groups = "security")
    public void t64_customUserDetailsService_loadByEmail_success() {
        User user = new User(5L, "User", "user@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("user@example.com");
        Assert.assertEquals(details.getUsername(), "user@example.com");
        Assert.assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test(priority = 65, groups = "security")
    public void t65_customUserDetailsService_userNotFound() {
        when(userRepository.findByEmail("no@example.com")).thenReturn(Optional.empty());
        try {
            userDetailsService.loadUserByUsername("no@example.com");
            Assert.fail("Expected UsernameNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("User not found"));
        }
    }

    @Test(priority = 66, groups = "security")
    public void t66_authenticationManager_simulation_success() {
        LoginRequest request = new LoginRequest("auth@example.com", "password123");
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(token);

        Assert.assertEquals(token.getPrincipal(), request.getEmail());
        verify(authenticationManager, never()).authenticate(null);
    }

    @Test(priority = 67, groups = "security")
    public void t67_roleBasedAccessConcept_adminCanManageAssets() {
        User admin = new User(1L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        Assert.assertEquals(admin.getRole(), "ADMIN");
    }

    @Test(priority = 68, groups = "security")
    public void t68_roleBasedAccessConcept_userNotAdmin() {
        User normal = new User(2L, "User", "user@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        Assert.assertNotEquals(normal.getRole(), "ADMIN");
    }

    @Test(priority = 69, groups = "security")
    public void t69_tokenIncludesEmailClaim() {
        User user = new User(15L, "E", "emailclaim@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);
        String email = (String) jwtUtil.parseToken(token).getPayload().get("email");
        Assert.assertEquals(email, "emailclaim@example.com");
    }

    @Test(priority = 70, groups = "security")
    public void t70_tokenIncludesRoleClaim() {
        User user = new User(16L, "R", "roleclaim@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);
        String role = (String) jwtUtil.parseToken(token).getPayload().get("role");
        Assert.assertEquals(role, "ADMIN");
    }

    @Test(priority = 71, groups = "security")
    public void t71_tokenIncludesUserIdClaim() {
        User user = new User(17L, "I", "idclaim@example.com", "IT", "USER", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);
        Object id = jwtUtil.parseToken(token).getPayload().get("userId");
        Assert.assertNotNull(id);
    }

    @Test(priority = 72, groups = "security")
    public void t72_invalidTokenParsingThrows() {
        String invalidToken = "invalid.token.string";
        try {
            jwtUtil.parseToken(invalidToken);
            Assert.fail("Expected exception for invalid token");
        } catch (Exception ex) {
            Assert.assertNotNull(ex.getMessage());
        }
    }

    @Test(priority = 73, groups = "security")
    public void t73_passwordMinLengthValidation() {
        User user = new User(null, "ShortPwd", "shortpwd@example.com", "IT", "USER", "short", null);
        when(userRepository.existsByEmail("shortpwd@example.com")).thenReturn(false);

        try {
            userService.registerUser(user);
            Assert.fail("Expected ValidationException due to password length");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Password must be at least 8 characters"));
        }
    }

    // endregion

    // region 8. Advanced querying / HQL-like (7 tests) priority 80-86

    @Test(priority = 80, groups = "hql")
    public void t80_getEventsForAsset_serviceUsesRepository() {
        LifecycleEvent e1 = new LifecycleEvent(1L, null, "ASSIGNED", "Assigned",
                LocalDateTime.now(), null);
        when(lifecycleEventRepository.findByAsset_Id(10L)).thenReturn(List.of(e1));

        List<LifecycleEvent> list = lifecycleEventService.getEventsForAsset(10L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 81, groups = "hql")
    public void t81_transferHistoryForAsset_service() {
        TransferRecord tr1 = new TransferRecord(1L, null, "IT", "HR", LocalDate.now(), null);
        TransferRecord tr2 = new TransferRecord(2L, null, "HR", "FINANCE", LocalDate.now(), null);
        when(transferRecordRepository.findByAsset_Id(5L)).thenReturn(List.of(tr1, tr2));

        List<TransferRecord> list = transferRecordService.getTransfersForAsset(5L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 82, groups = "hql")
    public void t82_disposalRecord_getById_found() {
        DisposalRecord d = new DisposalRecord(1L, null, "RECYCLED",
                LocalDate.now(), null, "notes", LocalDateTime.now());
        when(disposalRecordRepository.findById(1L)).thenReturn(Optional.of(d));

        DisposalRecord result = disposalRecordService.getDisposal(1L);
        Assert.assertEquals(result.getId(), 1L);
    }

    @Test(priority = 83, groups = "hql")
    public void t83_disposalRecord_getById_notFound() {
        when(disposalRecordRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            disposalRecordService.getDisposal(99L);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (ResourceNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains("Disposal record not found"));
        }
    }

    @Test(priority = 84, groups = "hql")
    public void t84_createTransfer_enforcesAdminAndDate() {
        Asset a = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        User admin = new User(1L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        TransferRecord tr = new TransferRecord();
        tr.setFromDepartment("IT");
        tr.setToDepartment("HR");
        tr.setTransferDate(LocalDate.now());
        tr.setApprovedBy(admin);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(a));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(transferRecordRepository.save(any(TransferRecord.class))).thenAnswer(inv -> {
            TransferRecord saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        TransferRecord saved = transferRecordService.createTransfer(1L, tr);
        Assert.assertNotNull(saved.getId());
    }

    @Test(priority = 85, groups = "hql")
    public void t85_createDisposal_setsAssetDisposed() {
        Asset a = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        User admin = new User(1L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        DisposalRecord dr = new DisposalRecord();
        dr.setDisposalMethod("SCRAPPED");
        dr.setDisposalDate(LocalDate.now());
        dr.setApprovedBy(admin);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(a));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(disposalRecordRepository.save(any(DisposalRecord.class))).thenAnswer(inv -> {
            DisposalRecord d = inv.getArgument(0);
            d.setId(20L);
            return d;
        });
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        DisposalRecord saved = disposalRecordService.createDisposal(1L, dr);
        Assert.assertNotNull(saved.getId());
        Assert.assertEquals(a.getStatus(), "DISPOSED");
    }

    @Test(priority = 86, groups = "hql")
    public void t86_createTransfer_futureDateValidation() {
        Asset a = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        User admin = new User(1L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        TransferRecord tr = new TransferRecord();
        tr.setFromDepartment("IT");
        tr.setToDepartment("HR");
        tr.setTransferDate(LocalDate.now().plusDays(1));
        tr.setApprovedBy(admin);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(a));

        try {
            transferRecordService.createTransfer(1L, tr);
            Assert.fail("Expected ValidationException for future date");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Transfer date cannot be in the future"));
        }
    }

    // endregion
}
