package com.jurajvetrak.taskmanagement.persistence;

import com.jurajvetrak.taskmanagement.task.persistence.QTaskEntity;
import com.jurajvetrak.taskmanagement.task.persistence.TaskEntity;
import com.jurajvetrak.taskmanagement.task.persistence.TaskStatus;
import com.jurajvetrak.taskmanagement.user.persistence.QUserEntity;
import com.jurajvetrak.taskmanagement.user.persistence.Role;
import com.jurajvetrak.taskmanagement.user.persistence.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceModelTests {

  @Test
  void newEntitiesReceiveApplicationGeneratedIdsAndNewTaskStatus() {
    var user = new UserEntity("Demo", "User", "demo@example.com", "not-persisted", Role.USER);
    var task = new TaskEntity("Review assignment", null, user);

    assertThat(user.getId()).isNotNull();
    assertThat(task.getId()).isNotNull();
    assertThat(task.getStatus()).isEqualTo(TaskStatus.NEW);
    assertThat(task.getOwner()).isSameAs(user);
  }

  @Test
  void queryTypesAreGeneratedForBothEntities() {
    assertThat(QUserEntity.userEntity.getType()).isEqualTo(UserEntity.class);
    assertThat(QTaskEntity.taskEntity.getType()).isEqualTo(TaskEntity.class);
  }
}
