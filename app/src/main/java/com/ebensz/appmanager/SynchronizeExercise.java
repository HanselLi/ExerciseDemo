package com.ebensz.appmanager;

/**
 * Created by liyangos3323 on 2018/12/13.
 * synchronized(this)以及非static的synchronized方法（至于static synchronized方法请往下看），
 * 只能防止多个线程同时执行同一个对象的同步代码段
 * <p>
 * <p>k
 * static synchronized方法，static方法可以直接类名加方法名调用，方法中无法使用this，所以它锁的不是this，
 * 而是类的Class对象，所以，static synchronized方法也相当于全局锁，相当于锁住了代码段
 * <p>
 * <p>
 * volatile
 * 由于java的内存模型的原因，线程在修改了共享变量后并不会立即把修改同步到内存中，而是会保存到线程的本地缓存中。
 * volatile关键字修饰的变量在线程修改后会立刻同步到主内存中，使该修改对其他线程可见
 * <p>
 * 调用wait的线程有可能会存在interrupt，虚假唤醒的情况，导致wait方法返回，
 * 但实际并没有调用对象的notify方法。在使用时通常会搭配一个lock flag 和 loop使用
 */

public class SynchronizeExercise {
    /**
     * 锁的是（）对象
     */
    public void testSynchronize() {
        synchronized (this) {// 锁的适用范围为{}内部
            System.out.println(" Start Begin ==== ");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" End Now =======");
        }
    }

    public void testGlobeLock() {
        synchronized (SynchronizeExercise.class) {
            System.out.println(" Start globe Begin ==== ");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" End globe Now =======");
        }
    }

    /**
     * static方法可以直接类名加方法名调用，方法中无法使用this，
     * 所以它锁的不是this，而是类的Class对象，所以，static synchronized方法也相当于全局锁，
     * 相当于锁住了代码段
     */
    public synchronized static void testGlobeUsingStaticMethod() {
        System.out.println(" Start static globe Begin ==== ");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" End static globe Now =======");
    }


    //锁一个唯一的object保证同步
    public void testSyncUsingLockSameObject(Object mLock) {
        synchronized (mLock) {
            System.out.println(" Start Using Lock Object globe Begin ==== " + mLock);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" End Using Lock Object globe Now =======");
        }
    }

}

class MainEnter {

    //测试锁同一个对象来实现同步,适用final保证唯一性,此对象必须保证唯一
    public static final Object mLock = new Object();

    public static void main(String[] args) {
//        new MainEnter().mainFucTestGlobeLock(4);
        new MainEnter().testTwoThreadCompete();
    }

    // 局部锁的错误用法 ,testSynchronize方法锁的是调用方法的对象，这里创建了三个不同的对象，所以失效
    private void mainFucTestLocalLockErrorSample() {
        for (int i = 0; i < 3; i++) {
            SynchronizeExercise exercise = new SynchronizeExercise();
            TestThread testThread = new TestThread(exercise, 0);
            testThread.start();
        }
    }

    // 测试局部锁正确用法，testSynchronize方法锁的是调用方法的对象，这里创建了唯一的对象，所以生效了
    private void mainFucTestLocalLockCorrectSample() {
        SynchronizeExercise exercise = new SynchronizeExercise();
        for (int i = 0; i < 3; i++) {
            TestThread testThread = new TestThread(exercise, 1);
            testThread.start();
        }
    }

    /**
     * 开三个线程去执行SynchronizeExercise的方法
     * 测试全局锁 , 创建三个SynchronizeExercise对象，锁 Class对象保证同步
     *
     * @param type 2 means synchronize Class , 3 means use static synchronize Class
     *             4 means 适用Object锁同一个对象实现同步
     */
    private void mainFucTestGlobeLock(int type) {
        for (int i = 0; i < 3; i++) {
            SynchronizeExercise exercise = new SynchronizeExercise();
            TestThread testThread = new TestThread(exercise, type);
            testThread.start();
        }
    }

    /**================================================线程等待Test===============================================================*/
    //volatile关键字修饰的变量在线程修改后会立刻同步到主内存中，使该修改对其他线程可见
    private volatile boolean isFinished = false;

    // ThreadA  wait until ThreadB notify it
    class ThreadA extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (mLock) {
                while (!isFinished) {
                    try {
                        System.out.println(" current ThreadA : " + isFinished);
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isFinished = true;
                System.out.println("ThreadA isFinished : " + isFinished);
            }
        }
    }

    class ThreadB extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (mLock) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isFinished = true;
                System.out.println(" current ThreadB isFinish : " + isFinished);
                mLock.notifyAll();
            }
        }
    }

    /**
     * 可以调换 ThreadA 和 ThreadB 的执行顺序
     * 谁先获取了锁，表现的形式也是不一样的
     */
    private void testTwoThreadCompete() {
        new ThreadA().start();
        new ThreadB().start();
    }
}


class TestThread extends Thread {
    SynchronizeExercise exercise;
    int type;

    public TestThread(SynchronizeExercise exercise, int type) {
        this.exercise = exercise;
        this.type = type;
    }

    @Override
    public void run() {
        super.run();
        switch (type) {
            case 0:
            case 1:
                exercise.testSynchronize();
                break;
            case 2:
                exercise.testGlobeLock();
                break;
            case 3:
                //此处为验证多个对象调用，适用static sync的场景，实际上可以直接
                //可以 SynchronizeExercise.testGlobeUsingStaticMethod();
                exercise.testGlobeUsingStaticMethod();
                break;
            case 4:
                exercise.testSyncUsingLockSameObject(MainEnter.mLock);
                break;
            default:
        }
    }
}
