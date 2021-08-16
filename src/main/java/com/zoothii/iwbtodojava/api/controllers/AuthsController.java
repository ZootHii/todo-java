package com.zoothii.iwbtodojava.api.controllers;

import javax.validation.Valid;

import com.zoothii.iwbtodojava.business.abstracts.AuthService;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.ErrorDataResult;
import com.zoothii.iwbtodojava.entities.concretes.payload.request.LoginRequest;
import com.zoothii.iwbtodojava.entities.concretes.payload.request.RegisterRequest;
import com.zoothii.iwbtodojava.entities.concretes.payload.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/auths")
@CrossOrigin
public class AuthsController {

    private final AuthService authService;

    public AuthsController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<DataResult<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        var result = authService.login(loginRequest);
        result.add(linkTo(methodOn(AuthsController.class).login(loginRequest)).withSelfRel());
        result.add(linkTo(methodOn(AuthsController.class).getClass()).withRel("auths"));
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<DataResult<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var result = authService.register(registerRequest);
        result.add(linkTo(methodOn(AuthsController.class).register(registerRequest)).withSelfRel());
        result.add(linkTo(methodOn(AuthsController.class).getClass()).withRel("auths"));
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
