package com.example.seesaw.model;

import com.example.seesaw.dto.PostDetailResponseDto;
import com.example.seesaw.dto.PostRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String generation;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Column
    private List<PostImage> postImages;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Column
    private List<PostTag> postTags;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    private Long views = 0L;


    public Post(String title, String contents, String videoUrl, String generation, User user) {
        this.title = title;
        this.contents = contents;
        this.videoUrl = videoUrl;
        this.generation = generation;
        this.user = user;

    }

    @Builder
    public Post(Long id, String title, String contents, String generation) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.generation = generation;
    }


    public void update(PostRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.generation = requestDto.getGeneration();
        this.contents = requestDto.getContents();
        this.videoUrl = requestDto.getVideoUrl();
        this.user = user;
    }

}
