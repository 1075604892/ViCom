package com.vicom.backend.entryDTO;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class SubPostDTO {
    private Long pid;
    private Long uid;
    private String content;
    private Integer page;
}
