package com.roberto.userservice.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authId;

    private String identification;

    private Boolean activated;
}
