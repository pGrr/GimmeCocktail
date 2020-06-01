package com.gimmecocktail;

public interface Observer<T> {

    void onResult(T result);

    void onError(Exception exception);

}
