package com.vicom.backend.requestEntry;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestPost {
    private Long cid;
    private Integer page;
}
