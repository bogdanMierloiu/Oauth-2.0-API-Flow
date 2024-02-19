package ro.bogdanMierloiu.Oauth2APIFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.bogdanMierloiu.Oauth2APIFlow.dto.ResponseDto;
import ro.bogdanMierloiu.Oauth2APIFlow.dto.RoleRequest;
import ro.bogdanMierloiu.Oauth2APIFlow.dto.RoleResponse;
import ro.bogdanMierloiu.Oauth2APIFlow.service.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/roles")
@Validated
public class RoleController implements CrudController<RoleRequest, RoleResponse> {

    private final RoleService roleService;

    @Operation(summary = "Get all roles", operationId = "getAllRoles", description = "Retrieves all roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all roles",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access this resource",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @Override
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @Operation(summary = "Get role by UUID", operationId = "getRoleByUuid", description = "Retrieves a role by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the role",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @Override
    @GetMapping("{uuid}")
    public ResponseEntity<RoleResponse> getByUuid(
            @Schema(description = "The UUID of the role to retrieve.")
            @PathVariable UUID uuid) {
        return ResponseEntity.ok(roleService.getByUuid(uuid));
    }


    @Operation(summary = "Create a new role", operationId = "createRole", description = "Creates a new role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the role",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @Override
    @PostMapping
    public ResponseEntity<RoleResponse> save(@RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(roleService.save(roleRequest));
    }

    @Operation(summary = "Update a role", operationId = "updateRole", description = "Updates a role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the role",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @Override
    @PatchMapping("{uuid}")
    public ResponseEntity<RoleResponse> update(
            @PathVariable("uuid")
            UUID objectToUpdateUuid,
            @RequestBody
            RoleRequest roleRequest) {
        return ResponseEntity.ok(roleService.update(objectToUpdateUuid, roleRequest));
    }

    @Operation(summary = "Delete a role", operationId = "deleteRole", description = "Deletes a role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the role",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @Override
    @DeleteMapping("{uuid}")
    public ResponseEntity<Void> delete(@PathVariable("uuid") UUID uuid) {
        return null;
    }
}
