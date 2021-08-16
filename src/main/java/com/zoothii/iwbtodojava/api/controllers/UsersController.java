package com.zoothii.iwbtodojava.api.controllers;

import com.zoothii.iwbtodojava.business.abstracts.UserService;
import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.entities.User;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.ErrorDataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/")
@CrossOrigin
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users")
    public DataResult<List<User>> getUsers() {
        return this.userService.getUsers();
    }

    @DeleteMapping("users/{userId}")
    public ResponseEntity<Result> deleteUser(@PathVariable Long userId) {
        var result = this.userService.deleteUser(userId);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // todo move to roles
//    @ApiOperation(value = "set new roles to the user's existing roles")
//    @PostMapping("setnewroles")
//    public ResponseEntity<Result> setNewRolesToUser(@RequestParam String username, @RequestBody Set<Role> roles) {
//        return new ResponseEntity<>(userService.setNewRolesToUser(username, roles), HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "set roles by removing the user's existing roles")
//    @PostMapping("setroles")
//    public ResponseEntity<Result> setRolesToUser(@RequestParam String username, @RequestBody Set<Role> roles) {
//        return new ResponseEntity<>(userService.setRolesToUser(username, roles), HttpStatus.OK);
//    }

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
