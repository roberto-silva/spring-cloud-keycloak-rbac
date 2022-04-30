package com.roberto.userservice.keycloak.config.service;

import com.roberto.userservice.keycloak.config.KeycloakManager;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class KeycloakUserService {

    private final KeycloakManager keyCloakManager;

    public Integer createUser(UserRepresentation userRepresentation) {

        if (getKeycloakUserByEmail(userRepresentation.getEmail()).isPresent()) {
            throw new IllegalArgumentException("There is already a user with this email address.");
        }

        Response response = keyCloakManager.getKeyCloakInstanceWithRealm().users().create(userRepresentation);
        return response.getStatus();
    }

    public void updateUser(UserRepresentation userRepresentation) {

        Optional<UserRepresentation> userRepresentationOptional = getKeycloakUserByEmail(userRepresentation.getEmail());

        if (userRepresentationOptional.isPresent() && !Objects.equals(userRepresentation.getId(), userRepresentationOptional.get().getId())) {
            throw new IllegalArgumentException("There is already a user with this email address.");
        }

        keyCloakManager.getKeyCloakInstanceWithRealm().users().get(userRepresentation.getId()).update(userRepresentation);
    }

    public Integer deleteUser(String email) {

        Optional<UserRepresentation> userRepresentationOptional = getKeycloakUserByEmail(email);

        if (userRepresentationOptional.isPresent()) {
            return this.keyCloakManager.getKeyCloakInstanceWithRealm().users()
                    .delete(userRepresentationOptional.get().getId())
                    .getStatus();
        } else {
            throw new IllegalArgumentException("There is no user with this email.");
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

    public Optional<UserRepresentation> getKeycloakUserByEmail(String email) {
        return keyCloakManager.getKeyCloakInstanceWithRealm().users().search(email).stream().findFirst();
    }
}
