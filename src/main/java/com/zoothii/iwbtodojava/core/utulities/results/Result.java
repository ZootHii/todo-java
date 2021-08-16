package com.zoothii.iwbtodojava.core.utulities.results;

import org.springframework.hateoas.RepresentationModel;

public class Result extends RepresentationModel<Result> {
    private final boolean success;
    private String message;

    public Result(boolean success){
        this.success = success;
    }

    public Result(boolean success, String message){
        this(success);
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
