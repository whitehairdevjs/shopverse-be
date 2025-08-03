package org.biz.shopverse;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;

public class test {
    public static void main(String[] args) {
//        String hashed = passwordEncoder().encode("jskim");
//        System.out.println(hashed);
        makeSecretKey();
    }

    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void makeSecretKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("jwt.secret = " + base64Key);
    }
}
