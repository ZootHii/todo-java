package com.zoothii.iwbtodojava.business.abstracts;

import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;
import com.zoothii.iwbtodojava.entities.concretes.Todo;

import java.util.List;

public interface TodoService {
    DataResult<List<Todo>> getTodos() throws InterruptedException;

    DataResult<List<Todo>> getAllTodos();

    Result createTodo(Todo todo);

    Result deleteTodo(Long todoId);

    Result updateTodo(Todo todo);

    Result deleteTodosByUserId(Long userId);

    Result checkIfTodoExistsById(Long todoId);

    Result checkIfUserOwnTodo(Long todoUserId, Long userId);
}
