package ma.ens.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the OAuth2 / OpenID Connect client demo.
 */
@SpringBootApplication
public class OAuth2ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ClientApplication.class, args);
    }
}
