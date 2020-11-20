package dev.navo.game.Client;

public class Buffer {
    private String data;
    private boolean empty = true;
    public synchronized String get() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        empty = true;
        notifyAll();
        return data;
    }

    public synchronized void put(String data) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        empty = false;
        this.data = data;
        notifyAll();
    }
}
