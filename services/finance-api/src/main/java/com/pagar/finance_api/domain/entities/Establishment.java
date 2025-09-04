package com.pagar.finance_api.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_establishment")
public class Establishment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String cnpj;
    private String name;
    private String fantasy;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category mainActivity;
}