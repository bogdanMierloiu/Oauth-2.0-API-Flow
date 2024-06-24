package ro.bogdanmierloiu.Oauth2APIFlow.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.RestTemplate;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Role;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.User;
import ro.bogdanmierloiu.Oauth2APIFlow.service.UserService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TokenIntrospection implements OpaqueTokenIntrospector {
    private final JwtDecoder jwtDecoder;
    private final Map<String, LinkedList<TrustedIssuer>> trustedIssuers;
    private final UserService userService;
    private final RestTemplate restTemplate;

    @Value("${issuer.uri}")
    private String issuerUri;

    @Value("${spring.jwk.file.path}")
    private String springJwkPath;

    @Value("${springAuthServerKeysUri}")
    private String springAuthServerKeysUri;

    public TokenIntrospection(UserService userService) {
        this.restTemplate = new RestTemplateBuilder().build();
        this.userService = userService;
        this.trustedIssuers = new LinkedHashMap<>();
        this.jwtDecoder = new NimbusJwtDecoder(new ParseOnlyJWTProcessor());
    }

    /***
     * This method updates the JWK on startup.
     */

    @PostConstruct
    public void updateJwkOnStartup() {
        springAuthServerUpdateJwk();
    }


    /**
     * This method verify the token and returns the principal object representing the user.
     *
     * @param token The token to verify
     * @return The principal object representing the user from database
     */
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        Jwt jwt = this.jwtDecoder.decode(token);
        User appUser = userService.verifyExistAndSave(jwt);

        String issuer = jwt.getClaimAsString("iss");

        if (issuer.equals(issuerUri)) {
            return springAuthServerIssuer(jwt, appUser.getRoles());
        } else throw new OAuth2IntrospectionException("Issuer not trusted");
    }

    /**
     * This method verifies the token signature for Spring Auth Server.
     * It verifies the signature of the token with public key from JWK.
     * If the verification fails, it updates the JWK and tries again.
     *
     * @jwtToken The JWT object representing the token to verify
     * @role The roles of the user to be added to the principal object
     */
    private OAuth2AuthenticatedPrincipal springAuthServerIssuer(Jwt jwtToken, Set<Role> role) {
        try {
            verifyTokenSignature(jwtToken, springJwkPath);
            return new DefaultOAuth2AuthenticatedPrincipal(jwtToken.getClaims(), roleConverter(role));
        } catch (SignatureVerificationException e) {
            springAuthServerUpdateJwk();
            verifyTokenSignature(jwtToken, springJwkPath);
            return new DefaultOAuth2AuthenticatedPrincipal(jwtToken.getClaims(), roleConverter(role));
        }
    }

    /**
     * This method verifies the token signature.
     *
     * @param jwtToken The JWT object representing the token to verify
     * @param jwkPath  The path to the JWK file
     */
    private void verifyTokenSignature(Jwt jwtToken, String jwkPath) {
        DecodedJWT jwt = JWT.decode(jwtToken.getTokenValue());
        try {
            JWKSet provider = JWKSet.load(new File(jwkPath));
            JWK jwk = provider.getKeyByKeyId(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256(jwk.toRSAKey().toRSAPublicKey(), null);
            algorithm.verify(jwt);
        } catch (Exception e) {
            throw new OAuth2IntrospectionException("Failed to verify token signature");
        }
    }

    /**
     * This method updates the JWK file with the public keys from Spring Auth Server.
     * It sends a GET request to the Spring Auth Server to get the public keys.
     */

    private void springAuthServerUpdateJwk() {
        String response = restTemplate.getForObject(springAuthServerKeysUri, String.class);
        try {
            PrintWriter printWriter = new PrintWriter(springJwkPath);
            log.info("------ Spring Auth Server - JWK updated ------");
            printWriter.println(response);
            printWriter.close();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public void addIssuers(TrustedIssuer... trustedIssuers) {
        for (TrustedIssuer trustedIssuer : trustedIssuers) {
            LinkedList<TrustedIssuer> clientList = this.trustedIssuers.get(trustedIssuer.getUri());
            if (clientList == null) {
                LinkedList<TrustedIssuer> newIssuer = new LinkedList<>();
                newIssuer.add(trustedIssuer);
                this.trustedIssuers.put(trustedIssuer.getUri(), newIssuer);
            } else {
                clientList.add(trustedIssuer);
            }
        }
    }

    /**
     * This method converts the roles of the user to a collection of granted authorities.
     *
     * @param roles The roles of the user.
     * @return A collection of granted authorities representing the roles of the user.
     */
    private Collection<GrantedAuthority> roleConverter(Set<Role> roles) {
        return roles
                .stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * This class is a custom JWT processor that only parses the JWT and returns the claims set.
     */
    private static class ParseOnlyJWTProcessor extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(SignedJWT jwt, SecurityContext context) {
            try {
                return jwt.getJWTClaimsSet();
            } catch (ParseException e) {
                throw new SecurityException("There was an error parsing the JWT");
            }
        }
    }
}
