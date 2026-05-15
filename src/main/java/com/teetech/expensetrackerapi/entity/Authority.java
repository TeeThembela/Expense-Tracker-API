package com.teetech.expensetrackerapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "authorities",
    uniqueConstraints = @UniqueConstraint(columnNames = "name"),
    indexes = {
        @Index(name = "idx_authorities_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority other)) return false;
        return name != null && name.equals(other.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
