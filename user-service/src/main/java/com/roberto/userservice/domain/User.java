package com.roberto.userservice.domain;

import com.roberto.userservice.dto.UserDTO;
import lombok.*;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authId;

    private String email;

    private String identification;

    private Boolean activated;

    public User(UserDTO userDTO) {
        BeanUtils.copyProperties(userDTO, this);
    }
}
