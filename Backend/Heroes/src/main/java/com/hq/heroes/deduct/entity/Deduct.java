package com.hq.heroes.deduct.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_deduct")
public class Deduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deduct_id")
    private Long deductId;

    @Column(name = "deduct_name", length = 20)
    private String deductName;

    @Column(name = "deduction_rate")
    private Double deduction_rate;

    @Column(name = "description")
    private String description;
}
