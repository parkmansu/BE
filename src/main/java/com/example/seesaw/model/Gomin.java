package com.example.seesaw.model;

import com.example.seesaw.dto.GominRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
@Entity
@AllArgsConstructor
public class Gomin extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Column
    private Long viewCount;

    @OneToMany(mappedBy = "gomin", cascade = CascadeType.REMOVE)
    private List<GominImage> imageUrls;

    @OneToMany(mappedBy = "gomin", cascade = CascadeType.REMOVE)
    private List<GominTag> gominTags;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public Gomin(String title, String content, String question, String answer, Long viewCount, User user){
        this.title = title;
        this.content = content;
        this.question = question;
        this.answer = answer;
        this.viewCount = viewCount;
        this.user = user;
    }

    public void update(GominRequestDto gominRequestDto) {
        this.title = gominRequestDto.getTitle();
        this.content = gominRequestDto.getContents();
        this.question = gominRequestDto.getQuestion();
        this.answer = gominRequestDto.getAnswer();
    }
}
