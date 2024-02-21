package ro.bogdanmierloiu.Oauth2APIFlow.config;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Role;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.User;
import ro.bogdanmierloiu.Oauth2APIFlow.service.UserService;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TokenIntrospection implements OpaqueTokenIntrospector {
    private final JwtDecoder jwtDecoder;
    private final Map<String, LinkedList<TrustedIssuer>> trustedIssuers;
    private final UserService userService;

    @Value("${local.issuer.uri}")
    private String issuerUriLocal;

    public TokenIntrospection(UserService userService) {
        this.userService = userService;
        this.trustedIssuers = new LinkedHashMap<>();
        this.jwtDecoder = new NimbusJwtDecoder(new ParseOnlyJWTProcessor());
    }

    /**
     * This method introspects the token and returns the principal object representing the user.
     * It verifies the issuer of the token and delegates the introspection to the trusted issuer.
     *
     * @param token The token to introspect
     * @return The principal object representing the user
     */
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        Jwt jwt = this.jwtDecoder.decode(token);
        User user = userService.verifyExistAndSave(jwt);

        String issuer = jwt.getClaimAsString("iss");

        if (issuer.equals(issuerUriLocal)) {
            return introspectToken(jwt, issuer, user.getRoles());
        }
        throw new OAuth2IntrospectionException("Issuer not trusted");
    }

    /**
     * This method introspects the token and returns the principal object representing the user.
     * It verifies the issuer of the token and delegates the introspection to the trusted issuer.
     *
     * @param jwt    The JWT object containing the user data.
     * @param issuer The issuer of the token.
     * @param roles  The roles of the user.
     * @return The principal object representing the user
     */
    private OAuth2AuthenticatedPrincipal introspectToken(Jwt jwt, String issuer, Set<Role> roles) {
        LinkedList<TrustedIssuer> clientsList = trustedIssuers.get(issuer);
        if (clientsList == null) {
            throw new OAuth2IntrospectionException("Issuer not trusted");
        }
        for (TrustedIssuer trustedIssuer : clientsList) {
            try {
                OpaqueTokenIntrospector delegate =
                        new NimbusOpaqueTokenIntrospector(trustedIssuer.getUri() +
                                trustedIssuer.getIntrospectionEndpoint(), trustedIssuer.getClientId(), trustedIssuer.getClientSecret());
                delegate.introspect(jwt.getTokenValue());
                return new DefaultOAuth2AuthenticatedPrincipal(jwt.getClaims(), roleConverter(roles));
            } catch (Exception ex) {
                log.info("Issuer {} failed to validate the token", trustedIssuer.getUri());
            }
        }
        throw new OAuth2IntrospectionException("Invalid client");
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
