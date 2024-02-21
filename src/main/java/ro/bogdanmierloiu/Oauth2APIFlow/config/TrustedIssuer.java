package ro.bogdanmierloiu.Oauth2APIFlow.config;

import lombok.Data;

@Data
public class TrustedIssuer {

    private String uri;

    private String introspectionEndpoint;

    private String clientId;

    private String clientSecret;

}