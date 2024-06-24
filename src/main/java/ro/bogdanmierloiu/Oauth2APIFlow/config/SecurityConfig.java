package ro.bogdanmierloiu.Oauth2APIFlow.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import ro.bogdanmierloiu.Oauth2APIFlow.service.UserService;

import java.util.Collections;
import java.util.List;

import static ro.bogdanmierloiu.Oauth2APIFlow.config.Endpoint.*;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    /**
     * This method creates a SecurityFilterChain that is responsible for processing the incoming requests.
     * It sets the session management policy to STATELESS, disables CSRF, and configures the CORS policy.
     * It also sets the authorization policy for the incoming requests.
     * It configures the OAuth2 resource server to use the opaque token introspection.
     *
     * @param http the HttpSecurity object
     * @return the SecurityFilterChain object
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(1800L);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(permitAllEndpoints()).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                        oauth2ResourceServerCustomizer.opaqueToken(opaqueTokenConfigurer ->
                                opaqueTokenConfigurer.introspector(tokenIntrospection())));
        return http.build();
    }

    /**
     * This method creates an OpaqueTokenIntrospector bean that is responsible for introspecting the opaque token.
     * It uses the TokenIntrospection class to introspect the token.
     *
     * @return the OpaqueTokenIntrospector object
     */
    @Bean
    public OpaqueTokenIntrospector tokenIntrospection() {
        TokenIntrospection tokenIntrospection = new TokenIntrospection(userService);
        tokenIntrospection.addIssuers(localAuthServerIssuer());
        return tokenIntrospection;
    }

    /**
     * This method creates a TrustedIssuer bean that is responsible for storing the trusted issuers.
     * It uses the localAuthServerIssuer() method to create the TrustedIssuer object.
     *
     * @return the TrustedIssuer object
     */
    @Bean
    @ConfigurationProperties(prefix = "issuer")
    public TrustedIssuer localAuthServerIssuer() {
        return new TrustedIssuer();
    }

    public String[] permitAllEndpoints() {
        return new String[]{
                SWAGGER_UI.getUrl(),
                API_DOCS.getUrl(),
                ACTUATOR.getUrl(),
        };
    }

}
