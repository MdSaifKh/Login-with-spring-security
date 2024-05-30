package com.mind.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JWTResponse {
    private String userName;
    private String token;
}
