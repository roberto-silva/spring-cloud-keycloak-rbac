package com.roberto.userservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    private String email;

    private String identification;

    private String password;

    private String authId;

}
