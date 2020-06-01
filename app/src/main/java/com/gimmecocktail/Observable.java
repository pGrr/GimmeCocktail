package com.gimmecocktail;

public interface Observable<T> {

    void observe(Observer<T> observer);

    void detach(Observer<T> observer);

    void notifyResultToObservers();

    void notifyErrorToObservers();

}
