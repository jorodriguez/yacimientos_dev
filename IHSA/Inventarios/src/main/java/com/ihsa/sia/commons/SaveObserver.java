package com.ihsa.sia.commons;

/**
 *
 * @author Aplimovil SA de CV
 */
public interface SaveObserver {
    public void update(SaveObservable observable, String event);
}
