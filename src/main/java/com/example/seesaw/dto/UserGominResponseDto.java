package com.example.seesaw.dto;

import com.example.seesaw.model.Gomin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserGominResponseDto {
    private Long gominCount;
    private List<Gomin> gominList;
}
