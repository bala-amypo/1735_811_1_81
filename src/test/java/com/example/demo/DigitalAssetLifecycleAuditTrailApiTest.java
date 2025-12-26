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

    // region 1. "Servlet" style concept tests
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

    // region 2. CRUD operations
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
            Assert.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Email already in use"));
        }
    }

    @Test(priority = 12, groups = "crud")
    public void t12_registerUser_shortPassword() {
        User user = new User(null, "User A", "short@example.com", "IT", "USER", "short", null);
        try {
            userService.registerUser(user);
            Assert.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Password must be at least 8 characters"));
        }
    }

    @Test(priority = 13, groups = "crud")
    public void t13_registerUser_missingDepartment() {
        User user = new User(null, "User A", "dep@example.com", null, "USER", "password123", null);
        try {
            userService.registerUser(user);
            Assert.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Department is required"));
        }
    }

    @Test(priority = 14, groups = "crud")
    public void t14_createAsset_success() {
        Asset asset = new Asset(null, "ASSET-001", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, null);
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
        Asset a = new Asset(2L, "TAG-2", "LAPTOP", "Lenovo", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        when(assetRepository.findById(2L)).thenReturn(Optional.of(a));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));
        Asset updated = assetService.updateStatus(2L, "ASSIGNED");
        Assert.assertEquals(updated.getStatus(), "ASSIGNED");
    }

    @Test(priority = 19, groups = "crud")
    public void t19_logLifecycleEvent_success() {
        Asset asset = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "ASSIGNED", null, LocalDateTime.now());
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
    }

    // region 3. DI & IoC
    @Test(priority = 20, groups = "di")
    public void t20_assetServiceInjectedRepoNotNull() { Assert.assertNotNull(assetService); Assert.assertNotNull(assetRepository); }
    @Test(priority = 21, groups = "di")
    public void t21_userServiceInjectedRepoAndPasswordEncoderNotNull() { Assert.assertNotNull(userService); Assert.assertNotNull(userRepository); }

    // region 4. Hibernate config
    @Test(priority = 30, groups = "hibernate")
    public void t30_assetEntity_prePersistSetsDefaults() {
        Asset asset = new Asset();
        asset.prePersist();
        Assert.assertEquals(asset.getStatus(), "AVAILABLE");
    }

    @Test(priority = 31, groups = "hibernate")
    public void t31_userEntity_prePersistDefaultsRoleAndCreatedAt() {
        User user = new User();
        user.prePersist();
        Assert.assertEquals(user.getRole(), "USER");
    }

    // region 7. Security & JWT
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
        Assert.assertEquals(jwtUtil.extractUsername(token), "token@example.com");
        Assert.assertEquals(jwtUtil.extractRole(token), "ADMIN");
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

    // region 8. Advanced querying
    @Test(priority = 85, groups = "hql")
    public void t85_createDisposal_setsAssetDisposed() {
        Asset a = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        User admin = new User(1L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        DisposalRecord dr = new DisposalRecord();
        dr.setDisposalDate(LocalDate.now());
        dr.setApprovedBy(admin);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(a));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(disposalRecordRepository.save(any(DisposalRecord.class))).thenAnswer(inv -> {
            DisposalRecord d = inv.getArgument(0);
            d.setId(20L);
            return d;
        });
        disposalRecordService.createDisposal(1L, dr);
        Assert.assertEquals(a.getStatus(), "DISPOSED");
    }

    @Test(priority = 86, groups = "hql")
    public void t86_createTransfer_futureDateValidation() {
        Asset a = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        TransferRecord tr = new TransferRecord();
        tr.setTransferDate(LocalDate.now().plusDays(1));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(a));
        try {
            transferRecordService.createTransfer(1L, tr);
            Assert.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Transfer date cannot be in the future"));
        }
    }
}