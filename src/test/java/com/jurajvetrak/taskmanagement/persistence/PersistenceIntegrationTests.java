package com.jurajvetrak.taskmanagement.persistence;

import com.jurajvetrak.taskmanagement.support.PostgresIntegrationTest;
import com.jurajvetrak.taskmanagement.task.persistence.TaskEntity;
import com.jurajvetrak.taskmanagement.task.persistence.TaskRepository;
import com.jurajvetrak.taskmanagement.user.persistence.QUserEntity;
import com.jurajvetrak.taskmanagement.user.persistence.Role;
import com.jurajvetrak.taskmanagement.user.persistence.UserEntity;
import com.jurajvetrak.taskmanagement.user.persistence.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PostgresIntegrationTest
class PersistenceIntegrationTests {

  private static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final String NON_SECRET_HASH_PLACEHOLDER = "x".repeat(60);

  private final JdbcTemplate jdbcTemplate;
  private final JPAQueryFactory queryFactory;
  private final UserRepository userRepository;
  private final TaskRepository taskRepository;

  PersistenceIntegrationTests(
      JdbcTemplate jdbcTemplate,
      JPAQueryFactory queryFactory,
      UserRepository userRepository,
      TaskRepository taskRepository
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.queryFactory = queryFactory;
    this.userRepository = userRepository;
    this.taskRepository = taskRepository;
  }

  @Test
  void flywayCreatesSchemaAndLoadsDeterministicDemoData() {
    var successfulMigrations = jdbcTemplate.queryForObject(
        "SELECT count(*) FROM flyway_schema_history WHERE success",
        Integer.class
    );

    assertThat(successfulMigrations).isEqualTo(2);
    assertThat(userRepository.count()).isEqualTo(2);
    assertThat(taskRepository.count()).isEqualTo(4);
    assertThat(userRepository.findByEmail("admin@example.com"))
        .get()
        .extracting(UserEntity::getId, UserEntity::getRole)
        .containsExactly(ADMIN_ID, Role.ADMIN);
    assertThat(userRepository.findByEmail("user@example.com"))
        .get()
        .extracting(UserEntity::getId, UserEntity::getRole)
        .containsExactly(USER_ID, Role.USER);
    assertThat(taskRepository.existsByOwnerId(ADMIN_ID)).isTrue();
    assertThat(taskRepository.existsByOwnerId(USER_ID)).isTrue();
  }

  @Test
  void querydslUsesGeneratedTypesAgainstPostgresql() {
    var user = QUserEntity.userEntity;

    var emails = queryFactory
        .select(user.email)
        .from(user)
        .orderBy(user.email.asc())
        .fetch();

    assertThat(emails).containsExactly("admin@example.com", "user@example.com");
  }

  @Test
  @Transactional
  void repositoriesPersistApplicationGeneratedIdsRelationshipsAndTimestamps() {
    var user = new UserEntity(
        "Persistence",
        "Test",
        "persistence@example.com",
        NON_SECRET_HASH_PLACEHOLDER,
        Role.USER
    );
    var task = new TaskEntity("Verify persistence", null, user);

    userRepository.saveAndFlush(user);
    taskRepository.saveAndFlush(task);

    assertThat(user.getId()).isNotNull();
    assertThat(user.getCreatedAt()).isNotNull();
    assertThat(user.getUpdatedAt()).isNotNull();
    assertThat(task.getId()).isNotNull();
    assertThat(task.getCreatedAt()).isNotNull();
    assertThat(task.getUpdatedAt()).isNotNull();
    assertThat(taskRepository.findByIdAndOwnerId(task.getId(), user.getId())).contains(task);
  }

  @Test
  void duplicateEmailIsRejectedByDatabase() {
    assertThatThrownBy(() -> insertUser(UUID.randomUUID(), "admin@example.com", "USER"))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("uq_users_email");
  }

  @Test
  void uppercaseEmailIsRejectedByDatabase() {
    assertThatThrownBy(() -> insertUser(UUID.randomUUID(), "Uppercase@example.com", "USER"))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("ck_users_email_lowercase");
  }

  @Test
  void unknownRoleIsRejectedByDatabase() {
    assertThatThrownBy(() -> insertUser(UUID.randomUUID(), "manager@example.com", "MANAGER"))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("ck_users_role");
  }

  @Test
  void unknownTaskStatusIsRejectedByDatabase() {
    assertThatThrownBy(() -> jdbcTemplate.update(
        """
            INSERT INTO tasks (
                id, title, description, status, owner_id, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
        UUID.randomUUID(),
        "Invalid status",
        null,
        "BLOCKED",
        USER_ID
    ))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("ck_tasks_status");
  }

  @Test
  void ownerWithTasksCannotBeDeletedByDatabase() {
    assertThatThrownBy(() -> jdbcTemplate.update("DELETE FROM users WHERE id = ?", USER_ID))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("fk_tasks_owner");
  }

  private void insertUser(UUID id, String email, String role) {
    jdbcTemplate.update(
        """
            INSERT INTO users (
                id, first_name, surname, email, password_hash, role, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
        id,
        "Constraint",
        "Test",
        email,
        NON_SECRET_HASH_PLACEHOLDER,
        role
    );
  }
}
