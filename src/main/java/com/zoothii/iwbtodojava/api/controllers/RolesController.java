package com.zoothii.iwbtodojava.api.controllers;

import com.zoothii.iwbtodojava.business.abstracts.RoleService;
import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.ErrorDataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/")
@RestController
@CrossOrigin
public class RolesController {
    private final RoleService roleService;

    @Autowired
    public RolesController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("roles")
    public ResponseEntity<DataResult<List<Role>>> getRoles() throws InterruptedException {
        var result = roleService.getRoles();
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("role")
    public ResponseEntity<Result> createRole(@Valid @RequestBody Role role) {
        var result = roleService.createRole(role);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("roles/{roleId}")
    public ResponseEntity<Result> deleteRole(@PathVariable Long roleId) {
        var result = this.roleService.deleteRole(roleId);
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
