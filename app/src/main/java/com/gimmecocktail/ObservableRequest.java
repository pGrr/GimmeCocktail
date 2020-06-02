package com.gimmecocktail;

public interface ObservableRequest<T> {

    ObservableRequest<T> observe(Observer<T> observer);

    ObservableRequest<T> detach(Observer<T> observer);

    /**
     * Sends the request (adds it to the request queue)
     * Observers must be attached before sending.
     */
    void send();

    void notifyResultToObservers(T result);

    void notifyErrorToObservers(Exception e);

}
