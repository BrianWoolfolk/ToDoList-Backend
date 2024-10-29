package com.todo.backend.repository;

import com.todo.backend.model.ToDo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Integer> {

    final String SEARCH_QUERY = "SELECT t FROM ToDo t WHERE " +
            "(:text IS NULL OR t.text LIKE %:text%) AND " +
            "(:done IS NULL OR t.done = :done) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) AND " +
            "(:dueDateTo IS NULL OR t.dueDate <= :dueDateTo) AND " +
            "(:creationDateFrom IS NULL OR t.creationDate >= :creationDateFrom) AND " +
            "(:creationDateTo IS NULL OR t.creationDate <= :creationDateTo) AND " +
            "(:doneDateFrom IS NULL OR t.doneDate >= :doneDateFrom) AND " +
            "(:doneDateTo IS NULL OR t.doneDate <= :doneDateTo) AND " +
            "(:tags IS NULL OR t.tags IN :tags) AND " +
            "(:assignedUser IS NULL OR t.assignedUser = :assignedUser)";

    @Query(SEARCH_QUERY)
    Page<ToDo> searchToDos(
            @Param("text") String text,
            @Param("done") Boolean done,
            @Param("priority") ToDo.Priority priority,
            @Param("dueDateFrom") Instant dueDateFrom,
            @Param("dueDateTo") Instant dueDateTo,
            @Param("creationDateFrom") Instant creationDateFrom,
            @Param("creationDateTo") Instant creationDateTo,
            @Param("doneDateFrom") Instant doneDateFrom,
            @Param("doneDateTo") Instant doneDateTo,
            @Param("tags") List<String> tags,
            @Param("user") String assignedUser,
            Pageable pageable);

    @Query(SEARCH_QUERY)
    List<ToDo> searchAllToDos(
            @Param("text") String text,
            @Param("done") Boolean done,
            @Param("priority") ToDo.Priority priority,
            @Param("dueDateFrom") Instant dueDateFrom,
            @Param("dueDateTo") Instant dueDateTo,
            @Param("creationDateFrom") Instant creationDateFrom,
            @Param("creationDateTo") Instant creationDateTo,
            @Param("doneDateFrom") Instant doneDateFrom,
            @Param("doneDateTo") Instant doneDateTo,
            @Param("tags") List<String> tags,
            @Param("user") String assignedUser);
}
