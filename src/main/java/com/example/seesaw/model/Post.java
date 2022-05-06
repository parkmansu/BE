package com.example.seesaw.model;

import com.example.seesaw.dto.PostRequestDto;
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Column
    private List<Image> imageList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Column
    private List<Tag> tags;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    public Post(String title, String contents, String videoUrl, User user) {
        this.title = title;
        this.contents = contents;
        this.videoUrl = videoUrl;
        this.user = user;
    }

    public void update(PostRequestDto requestDto, User user) {
        this.contents = requestDto.getContents();
        this.videoUrl = requestDto.getVideoUrl();
        this.user = user;
    }

    public void add(Image imageUrl) {
        imageList.add(imageUrl);
    }

}
