    package com.pagar.finance_api.domain.entities;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.util.UUID;

    @Getter
    @Setter
    @NoArgsConstructor
    @Entity
    @Table(name = "tb_role")
    public class Role{

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;
        private String authority;

        public Role(UUID roleId, String authority) {
            this.id = roleId;
            this.authority = authority;
        }
    }
