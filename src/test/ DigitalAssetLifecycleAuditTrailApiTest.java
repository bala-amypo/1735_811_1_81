package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.*;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
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

    // region 1. Servlet Concept Tests
    @Test(priority = 1, groups = "servlet")
    public void t01_servletLike_okStatusConcept() {
        Assert.assertTrue(200 < 201);
    }

    @Test(priority = 4, groups = "servlet")
    public void t04_controllerReceivesJsonBody_registerRequest() {
        RegisterRequest req = new RegisterRequest("John Doe", "john@example.com", "IT", "password123");
        Assert.assertEquals(req.getEmail(), "john@example.com");
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
        verify(userRepository).save(any(User.class));
    }

    @Test(priority = 11, groups = "crud")
    public void t11_registerUser_duplicateEmail() {
        User user = new User(null, "User A", "a@example.com", "IT", "USER", "password123", null);
        when(userRepository.existsByEmail("a@example.com")).thenReturn(true);
        try {
            userService.registerUser(user);
            Assert.fail();
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Email already in use"));
        }
    }

    @Test(priority = 12, groups = "crud")
    public void t12_registerUser_shortPassword() {
        User user = new User(null, "User A", "s@e.com", "IT", "USER", "short", null);
        try {
            userService.registerUser(user);
            Assert.fail();
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Password must be at least 8 characters"));
        }
    }

    @Test(priority = 16, groups = "crud")
    public void t16_getAssetById_notFound() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());
        try {
            assetService.getAsset(99L);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains("Asset not found"));
        }
    }

    // region 7. Security & JWT
    @Test(priority = 61, groups = "security")
    public void t61_generateTokenForUser_containsUserIdEmailRole() {
        User user = new User(10L, "T", "token@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        String token = jwtUtil.generateTokenForUser(user);
        Assert.assertEquals(jwtUtil.extractUsername(token), "token@example.com");
        Assert.assertEquals(jwtUtil.extractRole(token), "ADMIN");
        Assert.assertEquals(jwtUtil.extractUserId(token), Long.valueOf(10L));
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

    // region 8. Business Logic
    @Test(priority = 85, groups = "hql")
    public void t85_createDisposal_setsAssetDisposed() {
        Asset a = new Asset(1L, "TAG-1", "LAPTOP", "Dell", LocalDate.now(), "AVAILABLE", null, LocalDateTime.now());
        User admin = new User(1L, "Admin", "admin@example.com", "IT", "ADMIN", "pwd", LocalDateTime.now());
        DisposalRecord dr = new DisposalRecord();
        dr.setDisposalDate(LocalDate.now());
        dr.setApprovedBy(admin);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(a));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
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
            Assert.fail();
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Transfer date cannot be in the future"));
        }
    }
}