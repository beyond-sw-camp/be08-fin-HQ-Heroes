package com.hq.heroes.education.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_education_category")

public class EducationCategory {
    // 카테고리 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    // 카테고리 명
    @Column(name = "category_name", nullable = false)
    private String categoryName;
}
