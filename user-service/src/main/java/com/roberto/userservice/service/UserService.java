package com.roberto.userservice.service;

import com.roberto.userservice.domain.User;
import com.roberto.userservice.dto.UserDTO;
import com.roberto.userservice.keycloak.config.service.KeycloakUserService;
import com.roberto.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;

    public Page<UserDTO> getAll(Pageable pageable) {
        return this.userRepository.findAll(pageable).map(UserDTO::new);
    }

    public User getById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found."));
    }

    public User create(UserDTO userDTO) {
        Integer reponseStatus = this.keycloakUserService.createUser(buildUserRepresentationByDTO(userDTO));
        if (reponseStatus == 201) {
            return this.userRepository.save(new User(userDTO));
        }
        throw new RuntimeException("User registration failed.");
    }

    public User update(Long id, UserDTO userDTO) {
        User user = this.getById(id);
        BeanUtils.copyProperties(userDTO, user);

        UserRepresentation userRepresentation = keycloakUserService.getKeycloakUserByAuthId(user.getAuthId());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getEmail());
        keycloakUserService.updateUser(userRepresentation);

        return this.userRepository.save(user);
    }

    public void delete(Long id) {
        User user = this.getById(id);

        if (keycloakUserService.deleteUser(user.getEmail()) == 200) {
            this.userRepository.deleteById(id);
        }
    }

    public UserRepresentation buildUserRepresentationByDTO(UserDTO userDTO) {

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(false);
        userRepresentation.setUsername(userDTO.getEmail());
        userRepresentation.setCredentials(Collections.singletonList(buildCredentialRepresentationByDTO(userDTO)));

        return userRepresentation;
    }

    public CredentialRepresentation buildCredentialRepresentationByDTO(UserDTO userDTO) {

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userDTO.getPassword());
        credentialRepresentation.setTemporary(false);

        return credentialRepresentation;
    }

}
