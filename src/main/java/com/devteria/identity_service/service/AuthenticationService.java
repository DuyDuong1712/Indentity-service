package com.devteria.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.devteria.identity_service.dto.reponse.AuthenticationResponse;
import com.devteria.identity_service.dto.reponse.IntrospectReponse;
import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.IntrospectRequest;
import com.devteria.identity_service.dto.request.LogoutRequest;
import com.devteria.identity_service.dto.request.RefreshRequest;
import com.devteria.identity_service.entity.InvalidatedToken;
import com.devteria.identity_service.entity.RoleEntity;
import com.devteria.identity_service.entity.UserEntity;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.repository.InvalidatedTokenRepository;
import com.devteria.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESH_DURATION;

    //    protected static final String SIGNER_KEY = "eDvTlohzG6dHiJU9GdHzVGBq3T9b/UaLG9yqif0EuejdhEFOCuV4YHBIQjSNLG5Z";

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            // Lấy ID của token (jit) và thời gian hết hạn từ token.
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expirationTime)
                    .build();

            // Lưu token vào invalidatedTokenRepository để đánh dấu là token vô hiệu.
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    // Xác thực người dùng - Đăng nhập
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UserEntity userEntity = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), userEntity.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Tạo JWT token
        String token = generateToken(userEntity);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    // RefreshToken: Xác minh refresh token và tạo token JWT mới.
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // Kiem tra hieu luc cua token
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        // Tạo JWT token moi
        String token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    // Xác thực token:  kiểm tra xem token có hợp lệ và còn hạn hay không.
    public IntrospectReponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();

        //        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        //
        //        SignedJWT signedJWT = SignedJWT.parse(token);
        //
        //        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        //
        //        boolean verified = signedJWT.verify(verifier);

        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException exception) {
            isValid = false;
        }

        return IntrospectReponse.builder().valid(isValid).build();
    }

    // JwtService nên nằm ở JwtService
    private String generateToken(UserEntity userEntity) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userEntity.getUsername())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(userEntity))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        // Ký token
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cant create Token", e);
            throw new RuntimeException(e);
        }
    }

    // Tạo chuỗi các vai trò và quyền
    private String buildScope(UserEntity userEntity) {
        StringJoiner joiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(userEntity.getRoles())) {
            Set<RoleEntity> roles = userEntity.getRoles();
            userEntity.getRoles().forEach(roleEntity -> {
                joiner.add("ROLE_" + roleEntity.getCode());
                if (!CollectionUtils.isEmpty(roleEntity.getPermissions())) {
                    roleEntity.getPermissions().forEach(permissionEntity -> {
                        joiner.add(permissionEntity.getCode());
                    });
                }
            });
        }

        return joiner.toString();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESH_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (!(verified && expirationTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Nếu token tồn tại trong bảng InvalidateToken
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
