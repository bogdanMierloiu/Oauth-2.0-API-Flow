package ro.bogdanMierloiu.Oauth2APIFlow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "RoleResponse", description = "Role response for retrieving a role")
@Builder
public record RoleResponse(
        @Schema(description = "The role's name (required).")
        String name
) {
}
