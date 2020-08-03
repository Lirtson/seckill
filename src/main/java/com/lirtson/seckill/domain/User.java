package com.lirtson.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="user")
public class User {
    @Id
    private Long id;
    private String nickname;
    @Column(nullable = false)
    private String password;
    private String salt;
}
