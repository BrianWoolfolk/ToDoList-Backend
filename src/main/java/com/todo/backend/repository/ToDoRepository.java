package com.todo.backend.repository;

import com.todo.backend.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Integer> {

    @Query("SELECT t FROM ToDo t WHERE " +
            "(:text IS NULL OR t.text LIKE %:text%) AND " +
            "(:done IS NULL OR t.done = :done) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    List<ToDo> searchToDos(@Param("text") String text,
            @Param("done") Boolean done,
            @Param("priority") ToDo.Priority priority);

}
