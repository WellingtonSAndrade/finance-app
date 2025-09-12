package com.pagar.finance_api.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    @Column(unique = true)
    private String email;
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<Card> card = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Tag> tags = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<OcrAnalyzer> analyzers = new ArrayList<>();

    public void addRole(Role role) {
        roles.add(role);
    }
}
