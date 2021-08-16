package com.zoothii.iwbtodojava.api.controllers;

import com.zoothii.iwbtodojava.business.abstracts.TodoService;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.ErrorDataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;
import com.zoothii.iwbtodojava.entities.concretes.Todo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
@CrossOrigin
public class TodosController {

    private final TodoService todoService;

    public TodosController(TodoService todoService) {
        this.todoService = todoService;
    }

    @ApiOperation(value = "get todos of authenticated user")
    @GetMapping("todos")
    public ResponseEntity<DataResult<List<Todo>>> getTodos() throws InterruptedException {
        var result = todoService.getTodos();
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "get todos of all users")
    @GetMapping("all/todos")
    public ResponseEntity<DataResult<List<Todo>>> getAllTodos() {
        var result = todoService.getAllTodos();
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "create todo for authenticated user")
    @PostMapping("todo")
    public ResponseEntity<Result> createTodo(@Valid @RequestBody Todo todo) {
        var result = todoService.createTodo(todo);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "delete todo for authenticated user")
    @DeleteMapping("todos/{todoId}")
    public ResponseEntity<Result> deleteTodo(@PathVariable Long todoId) {
        var result = todoService.deleteTodo(todoId);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "update todo for authenticated user")
    @PutMapping("todo")
    public ResponseEntity<Result> updateTodo(@Valid @RequestBody Todo todo) {
        var result = todoService.updateTodo(todo);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /*** Validation ***/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDataResult<Object> validationExceptionHandler(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ErrorDataResult<>(validationErrors, "validation errors");
    }

}
