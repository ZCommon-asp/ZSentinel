import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: JacX.
 * @description:
 * @Date: 2021/7/7 21:03
 * @Modified By:JacX.
 * @see
 * @since
 */
public class Test {

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    public static void main(String[] args) {



        new Thread() {
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println("sig lock...");
                    Thread.sleep(1000);
                    while (true) {
                        condition.signal();
                        System.out.println("sig notify...");
                        condition.await();
                        System.out.println("sig await...");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();

                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println("lock...");
                    condition.signal();
                    condition.await();
                    System.out.println("notify...");
                } catch (InterruptedException e) {
                }finally {
                    lock.unlock();

                }
            }
        }.start();
    }
}
