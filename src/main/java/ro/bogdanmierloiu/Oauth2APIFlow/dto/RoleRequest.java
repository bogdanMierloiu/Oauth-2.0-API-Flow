package ro.bogdanmierloiu.Oauth2APIFlow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RoleRequest", description = "Role request for creating a new role")
public record RoleRequest(
        @Schema(description = "The role's name (required).")
        @NotBlank(message = "Role's name cannot be null or empty")
        @Size(min = 3, max = 50, message = "Role's name must be between 3 and 50 characters")
        String name
) {
}
