package ro.bogdanmierloiu.Oauth2APIFlow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Product;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Role;

import java.util.Set;
import java.util.UUID;

@Schema(name = "UserResponse", description = "User response for retrieving a user")
@Builder
public record UserResponse(
        @Schema(description = "The user's email")
        String email,
        @Schema(description = "The user's name")
        String name,
        @Schema(description = "The user's surname")
        String surname,
        @Schema(description = "The user's roles")
        Set<RoleResponse> roles,
        @Schema(description = "The user's UUID")
        UUID uuid
) {
}
