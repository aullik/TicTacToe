package de.htwg.util.observer;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public interface IObservable {
    void addObserver(IObserver s);

    void removeObserver(IObserver s);

    void removeAllObservers();

    void notifyObservers();

}
