package com.gujo.uminity.role.entity;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@IdClass(UserRole.UserRoleId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * IdClass용 복합키 정의
     */
    public static class UserRoleId implements Serializable {
        private UUID userId;
        private Integer roleId;

        public UserRoleId() {
        }

        public UserRoleId(UUID userId, Integer roleId) {
            this.userId = userId;
            this.roleId = roleId;
        }
        // equals & hashCode (생략 가능하나 권장)
    }
}
