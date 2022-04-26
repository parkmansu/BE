package com.sparta.odict.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String age;

    @Column(nullable = false)
    private String profileImage;

    @Column
    private Long postCount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(unique = true)
    private Long kakaoId;

//    @OneToMany(mappedBy = "user")
//    private List<Post> post; test



    public User(String username, String nickname, String enPassword, String age, String profileImage, Long postCount, UserRoleEnum role) {
        this.username = username;
        this.nickname = nickname;
        this.pwd = enPassword;
        this.age = age;
        this.profileImage = profileImage;
        this.postCount = postCount;
        this.role = role;
        this.kakaoId = null;
    }

    public User(String username, String nickname, String enPassword, String age, String profileImage, Long postCount, UserRoleEnum role, Long kakaoId) {
        this.username = username;
        this.nickname = nickname;
        this.pwd = enPassword;
        this.age = age;
        this.profileImage = profileImage;
        this.postCount = postCount;
        this.role = role;
        this.kakaoId = kakaoId;
    }
}
