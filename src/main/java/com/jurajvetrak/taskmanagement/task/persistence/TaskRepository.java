package com.jurajvetrak.taskmanagement.task.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

  Optional<TaskEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

  boolean existsByOwnerId(UUID ownerId);
}
