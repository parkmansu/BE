package com.example.seesaw.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Setter
@Entity
@AllArgsConstructor
public class GominTag {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "GOMIN_ID", nullable = false)
    private Gomin gomin;

    public GominTag(String tagName, User user, Gomin gomin){
        this.tagName = tagName;
        this.user = user;
        this.gomin = gomin;

    }
}
