package yayuki.heartrate.server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ClientTest {
    @org.junit.jupiter.api.Test
    void connect() {
        //Client.connect(1);

        Client.connect(10);
        block();
    }

    private void block() {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        try {
            lock.lock();
            condition.await();
            lock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}