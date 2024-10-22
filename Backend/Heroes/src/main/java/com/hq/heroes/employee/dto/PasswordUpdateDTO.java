package com.hq.heroes.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordUpdateDTO {
    
    private String currentPassword; // 현재 비밀번호
    private String newPassword;  // 새 비밀번호

}
