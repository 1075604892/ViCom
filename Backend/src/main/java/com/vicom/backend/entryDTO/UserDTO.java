package com.vicom.backend.entryDTO;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDTO {
    Long uid;
    String cookie;
}
