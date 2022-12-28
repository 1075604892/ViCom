package com.vicom.backend.entryDTO;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long cid;
    private Long pid;
    private Integer page;
}
