package com.zoothii.iwbtodojava.business.concretes;

import com.zoothii.iwbtodojava.business.abstracts.TodoService;
import com.zoothii.iwbtodojava.core.utulities.results.*;
import com.zoothii.iwbtodojava.data_access.abstracts.TodoDao;
import com.zoothii.iwbtodojava.entities.concretes.Todo;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"todos"})
public class TodoManager implements TodoService {

    private final TodoDao todoDao;
    private final AuthManager authManager;

    public TodoManager(TodoDao todoDao, AuthManager authManager) {
        this.todoDao = todoDao;
        this.authManager = authManager;
    }

    @Override
    @Cacheable(value = "ten-seconds-cache", key = "'todos-cache'")
    @PreAuthorize("hasAnyRole('USER')")
    public DataResult<List<Todo>> getTodos() throws InterruptedException {
        Thread.sleep(1000);
        var resultAuthenticatedUserDetails = authManager.getAuthenticatedUserDetails();
        if (!resultAuthenticatedUserDetails.isSuccess()) {
            return new ErrorDataResult<>(resultAuthenticatedUserDetails.getMessage());
        }
        var userId = resultAuthenticatedUserDetails.getData().getId();
        return new SuccessDataResult<>(todoDao.getAllByUserId(userId), "get todos success");
    }

    @Override
    public DataResult<List<Todo>> getAllTodos() {
        return new SuccessDataResult<>(todoDao.findAll());
    }

    @Override
    @CacheEvict(value = "ten-seconds-cache", key = "'todos-cache'", condition = "#result.success")
    @PreAuthorize("hasAnyRole('USER')")
    public Result createTodo(Todo todo) {
        var resultAuthenticatedUserDetails = authManager.getAuthenticatedUserDetails();
        if (!resultAuthenticatedUserDetails.isSuccess()) {
            return new ErrorDataResult<>(resultAuthenticatedUserDetails.getMessage());
        }
        var userId = resultAuthenticatedUserDetails.getData().getId();
        var createdTodo = new Todo(null, todo.getWhatTodo(), null, null, false, userId);

        todoDao.save(createdTodo);
        return new SuccessDataResult<>("create todo success");
    }

    @Override
    @CacheEvict(value = "ten-seconds-cache", key = "'todos-cache'", condition = "#result.success")
    @PreAuthorize("hasAnyRole('USER')")
    public Result deleteTodo(Long todoId) {
        var resultAuthenticatedUserDetails = authManager.getAuthenticatedUserDetails();
        if (!resultAuthenticatedUserDetails.isSuccess()) {
            return new ErrorDataResult<>(resultAuthenticatedUserDetails.getMessage());
        }

        var resultCheckIfTodoExistsById = checkIfTodoExistsById(todoId);
        if (!resultCheckIfTodoExistsById.isSuccess()) {
            return new ErrorResult(resultCheckIfTodoExistsById.getMessage());
        }
        var todoToDelete = todoDao.findById(todoId);
        var todoUserId = todoToDelete.get().getUserId();
        var userId = resultAuthenticatedUserDetails.getData().getId();

        var resultCheckIfUserOwnTodo = checkIfUserOwnTodo(todoUserId, userId);
        if (!resultCheckIfUserOwnTodo.isSuccess()) {
            return new ErrorResult(resultCheckIfUserOwnTodo.getMessage());
        }

        todoDao.delete(todoToDelete.get());
        return new SuccessResult("delete todo success");
    }

    @Override
    @CacheEvict(value = "ten-seconds-cache", key = "'todos-cache'", condition = "#result.success")
    @PreAuthorize("hasAnyRole('USER')")
    public Result updateTodo(Todo todo) {
        Result resultAuthenticatedUserDetails = getResult(todo);
        if (resultAuthenticatedUserDetails != null) return resultAuthenticatedUserDetails;
        var todoToUpdate = todoDao.findById(todo.getId());
        var todoToUpdateNew = new Todo(
                todoToUpdate.get().getId(),
                todoToUpdate.get().getWhatTodo(),
                todoToUpdate.get().getCreatedAt(),
                null, todo.getIsDone(),
                todoToUpdate.get().getUserId());
        todoDao.save(todoToUpdateNew);
        return new SuccessResult("update todo success");
    }

    @Override
    @Transactional
    public Result deleteTodosByUserId(Long userId) {
        todoDao.deleteAllByUserId(userId);
        return new SuccessResult("todos all delete success");
    }

    /*** Business Rules ***/
    @Override
    public Result checkIfTodoExistsById(Long todoId) {
        var result = todoDao.existsById(todoId);
        if (!result) {
            return new ErrorResult("not found todo error");
        }
        return new SuccessResult("found todo success");
    }

    @Override
    public Result checkIfUserOwnTodo(Long todoUserId, Long userId) {
        if (!todoUserId.equals(userId)) {
            return new ErrorResult("not owner of this todo");
        }
        return new SuccessResult("owner of this todo");
    }

    /*** Class Methods ***/
    @Nullable
    private Result getResult(Todo todo) {
        var resultAuthenticatedUserDetails = authManager.getAuthenticatedUserDetails();
        if (!resultAuthenticatedUserDetails.isSuccess()) {
            return new ErrorDataResult<>(resultAuthenticatedUserDetails.getMessage());
        }

        var resultCheckIfTodoExistsById = checkIfTodoExistsById(todo.getId());
        if (!resultCheckIfTodoExistsById.isSuccess()) {
            return new ErrorResult(resultCheckIfTodoExistsById.getMessage());
        }
        var todoToUpdate = todoDao.findById(todo.getId());
        var todoUserId = todoToUpdate.get().getUserId();
        var userId = resultAuthenticatedUserDetails.getData().getId();

        var resultCheckIfUserOwnTodo = checkIfUserOwnTodo(todoUserId, userId);
        if (!resultCheckIfUserOwnTodo.isSuccess()) {
            return new ErrorResult(resultCheckIfUserOwnTodo.getMessage());
        }
        return null;
    }

}
