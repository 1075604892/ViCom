package com.vicom.backend.entryDTO;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class NameDTO {
    private String name;
    private Long uid;
    private String cookie;
    private Integer page;
}
