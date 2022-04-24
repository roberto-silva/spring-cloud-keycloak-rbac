package com.roberto.userservice.dto;

import com.roberto.userservice.domain.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserDTO {
    private Long id;

    private String email;

    private String identification;

    private String password;

    private String authId;

    public UserDTO(User user) {
        BeanUtils.copyProperties(user, this, "password");
    }
}
