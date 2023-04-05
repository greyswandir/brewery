package guru.sfg.brewery.web.controllers.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncodingTests {
    static final String PASSWORD = "password";

    @Test
    void testLdap() {
        PasswordEncoder bCrypt = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 17);
        System.out.println(bCrypt.encode(PASSWORD));
        System.out.println(bCrypt.encode(PASSWORD));
        String encodedPass = bCrypt.encode(PASSWORD);

        PasswordEncoder bCrypt2 = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 13);
        assertTrue(bCrypt2.matches(PASSWORD, encodedPass));
    }

    @Test
    void testNoOp() {
        PasswordEncoder noop = NoOpPasswordEncoder.getInstance();
        System.out.println(noop.encode(PASSWORD));
    }

    @Test
    void passwordHashTest() {
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        String salted = PASSWORD + "ThsiIsMySaltValue";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }
}
