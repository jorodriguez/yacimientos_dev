package com.ihsa.sia.commons;

/**
 *
 * @author Aplimovil SA de CV
 */
public interface SaveObservable {
    public void addObserver(SaveObserver o);
    public void notifyObservers(String event);    
}
