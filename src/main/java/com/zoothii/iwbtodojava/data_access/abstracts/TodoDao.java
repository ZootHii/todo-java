package com.zoothii.iwbtodojava.data_access.abstracts;

import com.zoothii.iwbtodojava.entities.concretes.Todo;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoDao extends JpaRepository<Todo, Long> {

    List<Todo> getAllByUserId(Long userId);

    boolean existsById(@NotNull Long todoId);

    void deleteAllByUserId(@NotNull Long userId);
}
