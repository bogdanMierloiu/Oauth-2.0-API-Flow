package ro.bogdanmierloiu.Oauth2APIFlow.config;

import lombok.Getter;

@Getter
public enum Endpoint {

    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/v3/api-docs/**"),
    ACTUATOR("/actuator/**");

    private final String url;

    Endpoint(final String url) {
        this.url = url;
    }
}
