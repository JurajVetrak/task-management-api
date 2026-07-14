package com.jurajvetrak.taskmanagement.user.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uq_users_email", columnNames = "email")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity implements Persistable<UUID> {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id = UUID.randomUUID();

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String surname;

  @Column(nullable = false, length = 254)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 60)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Transient
  @Getter(AccessLevel.NONE)
  private boolean newEntity = true;

  public UserEntity(String firstName, String surname, String email, String passwordHash,
      Role role) {
    this.firstName = Objects.requireNonNull(firstName);
    this.surname = Objects.requireNonNull(surname);
    this.email = Objects.requireNonNull(email);
    this.passwordHash = Objects.requireNonNull(passwordHash);
    this.role = Objects.requireNonNull(role);
  }

  @PrePersist
  void initializeTimestamps() {
    var now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
  }

  @PreUpdate
  void updateTimestamp() {
    updatedAt = Instant.now();
  }

  @PostLoad
  @PostPersist
  void markNotNew() {
    newEntity = false;
  }

  @Override
  public boolean isNew() {
    return newEntity;
  }
}
