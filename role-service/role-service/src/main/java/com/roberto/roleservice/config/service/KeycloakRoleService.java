package com.roberto.roleservice.config.service;

import com.roberto.roleservice.config.KeycloakManager;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class KeycloakRoleService {

    private final KeycloakManager keyCloakManager;

    public Boolean createRole(RoleRepresentation roleRepresentation) {

        if (getKeycloakRoleByName(roleRepresentation.getName()).isPresent()) {
            throw new IllegalArgumentException("There is already a role with this name.");
        }
        int lastSize = keyCloakManager.getKeyCloakInstanceWithRealm().roles().list().size();
        keyCloakManager.getKeyCloakInstanceWithRealm().roles().create(roleRepresentation);
        int newSize = keyCloakManager.getKeyCloakInstanceWithRealm().roles().list().size();

        return lastSize < newSize;
    }

    public void updateRole(RoleRepresentation roleRepresentation) {

        Optional<RoleRepresentation> currentRoleRepresentation = getKeycloakRoleByName(roleRepresentation.getName());

        if (currentRoleRepresentation.isPresent() && !Objects.equals(roleRepresentation.getId(),
                currentRoleRepresentation.get().getId())) {
            throw new IllegalArgumentException("There is already a roles with this name.");
        }

        keyCloakManager.getKeyCloakInstanceWithRealm().roles().get(roleRepresentation.getId()).update(roleRepresentation);
    }

    public Boolean deleteRole(String name) {

        Optional<RoleRepresentation> userRepresentationOptional = getKeycloakRoleByName(name);

        if (userRepresentationOptional.isPresent()) {
            int lastSize = keyCloakManager.getKeyCloakInstanceWithRealm().roles().list().size();

            this.keyCloakManager.getKeyCloakInstanceWithRealm().roles()
                    .deleteRole(userRepresentationOptional.get().getId());

            int newSize = keyCloakManager.getKeyCloakInstanceWithRealm().roles().list().size();

            return lastSize > newSize;
        } else {
            throw new IllegalArgumentException("There is no role with this name.");
        }

    }

    public UserRepresentation getKeycloakUserByAuthId(String authId) {
        try {
            UserResource userResource = keyCloakManager.getKeyCloakInstanceWithRealm().users().get(authId);
            return userResource.toRepresentation();
        } catch (Exception e) {
            throw new RuntimeException("User not found.");
        }
    }

    public Optional<RoleRepresentation> getKeycloakRoleByName(String email) {
        return Optional.of(
                keyCloakManager.getKeyCloakInstanceWithRealm().roles().get(email).toRepresentation()
        );
    }
}
