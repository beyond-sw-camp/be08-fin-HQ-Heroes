package com.hq.heroes.auth.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinDTO {

    private String email;
    private String password;
    private String name;

}
