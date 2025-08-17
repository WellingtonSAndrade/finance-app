package com.pagar.finance_api.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_installment")
public class Installment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Boolean isInstallment;
    private Integer numberInstallments;
    @Column(precision = 15, scale = 2)
    private BigDecimal installmentValue;
}