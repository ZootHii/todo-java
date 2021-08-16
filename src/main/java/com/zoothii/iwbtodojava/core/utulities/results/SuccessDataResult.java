package com.zoothii.iwbtodojava.core.utulities.results;

public class SuccessDataResult<T> extends DataResult<T> {
    public SuccessDataResult(T data, String message) {
        super(data, true, message);
    }

    public SuccessDataResult(T data) {
        super(data, true);
    }

    public SuccessDataResult(String message) {
        super(null, true, message);
    }

    public SuccessDataResult() {
        super(null, true);
    }
}
