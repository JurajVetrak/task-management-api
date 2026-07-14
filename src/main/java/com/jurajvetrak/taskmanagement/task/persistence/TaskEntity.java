package com.jurajvetrak.taskmanagement.task.persistence;

import com.jurajvetrak.taskmanagement.user.persistence.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskEntity implements Persistable<UUID> {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id = UUID.randomUUID();

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TaskStatus status = TaskStatus.NEW;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "owner_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_tasks_owner")
  )
  private UserEntity owner;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Transient
  @Getter(AccessLevel.NONE)
  private boolean newEntity = true;

  public TaskEntity(String title, String description, UserEntity owner) {
    this.title = Objects.requireNonNull(title);
    this.description = description;
    this.owner = Objects.requireNonNull(owner);
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
