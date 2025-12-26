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

    // ... (All the @Test methods as provided in Rule 3) ...
    // Note: Ensure t69, t70, t71 use .getPayload() as per your JJWT 0.12.x version
    
    @Test(priority = 1, groups = "servlet")
    public void t01_servletLike_okStatusConcept() {
        int statusOk = 200;
        int statusCreated = 201;
        Assert.assertTrue(statusOk < statusCreated);
    }

    // [Method headers for brevity - keep all methods from your source]
    // t02 to t86...
}