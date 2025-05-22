package am.itspace.bookshop.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        String secureSecret = "aBvN!12@C#dEfG$hIjK%LmNoPqRsTuVwXyZ0123456789QWERTYUIOPASDFGHJKL";
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", secureSecret);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 3600L);
    }

    @Test
    void generateTokenReturnsValidToken() {
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email);
        assertNotNull(token);
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(email, usernameFromToken);
    }

    @Test
    void validateTokenCorrectTokenReturnsTrue() {
        String email = "user@mail.com";
        String token = jwtTokenUtil.generateToken(email);
        boolean isValid = jwtTokenUtil.validateToken(token, email);
        assertTrue(isValid);
    }

    @Test
    void validateTokenWrongEmailReturnsFalse() {
        String email = "real@mail.com";
        String token = jwtTokenUtil.generateToken(email);
        boolean isValid = jwtTokenUtil.validateToken(token, "fake@mail.com");
        assertFalse(isValid);
    }

    @Test
    void getIssuedAtDateFromTokenReturnsNonNullDate() {
        String token = jwtTokenUtil.generateToken("user@mail.com");
        Date issuedAt = jwtTokenUtil.getIssuedAtDateFromToken(token);
        assertNotNull(issuedAt);
    }

    @Test
    void getExpirationDateFromTokenReturnsValidExpiration() {
        String token = jwtTokenUtil.generateToken("user@mail.com");
        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void refreshTokenReturnsNewTokenWithLaterExpiration() throws InterruptedException {
        String token = jwtTokenUtil.generateToken("refresh@mail.com");
        Thread.sleep(1000);
        String refreshed = jwtTokenUtil.refreshToken(token);
        Date oldIssued = jwtTokenUtil.getIssuedAtDateFromToken(token);
        Date newIssued = jwtTokenUtil.getIssuedAtDateFromToken(refreshed);
        assertTrue(newIssued.after(oldIssued));
        assertEquals(jwtTokenUtil.getUsernameFromToken(token), jwtTokenUtil.getUsernameFromToken(refreshed));
    }
}