# 并发编程 - Abstract Queued Synchronizer(AQS) 队列同步器

## 1. AQS 概述

队列同步器 AbstractQueuedSynchronizer（简称同步器或 AQS），是一个抽象的队列同步器，是用来构建**阻塞式锁或者相关同步组件的基础框架**。

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable
```

AQS 它使用了一个 int 类型的共享成员变量表示同步状态，通过内置的 FIFO (先进先出)的线程等待队列来完成资源获取线程的排队工作，类似于 Monitor 的 EntryList。而条件变量用于实现等待、唤醒机制，支持多个条件变量，类似于 Monitor 的 WaitSet

> *并发包的大师（Doug Lea）期望它能够成为实现大部分同步需求的基础*

## 2. AQS 实现原理

AQS 核心思想是，如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态；如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及被唤醒时锁分配的机制，这个机制 AQS 是用 CLH 队列锁实现的，即将暂时获取不到锁的线程加入到等待队列中。许多同步类的实现都依赖于 AQS，例如常用的 ReentrantLock、Semaphore 和 CountDownLatch。

> CLH(Craig,Landin,and Hagersten)队列是一个虚拟的双向队列（虚拟的双向队列即不存在队列实例，仅存在结点之间的关联关系）。AQS是将每条请求共享资源的线程封装成一个CLH锁队列的一个结点（Node）来实现锁的分配。

![](images/218484510221142.png)

### 2.1. state：状态

AQS 维护了一个 `volatile int` 类型的成员变量 `state`，用于表示当前资源的同步状态（分独占模式和共享模式）。通过内置的 FIFO 队列来完成获取资源线程的排队工作，AQS 使用 CAS 对该同步状态进行原子操作实现对其值的修改

```java
private volatile int state; // 共享变量，使用 volatile 修饰保证线程可见性
```

子类需要定义如何维护这个状态，控制如何获取锁和释放锁。state 属性访问方式有三种：

- `getState()`：获取 state 状态
- `setState()`：设置 state 状态
- `compareAndSetState()`：通过 cas 机制设置 state 状态

以上方法均是原子操作，其中 `compareAndSetState` 的实现依赖于 `Unsafe` 类的 `compareAndSwapInt()` 方法。具体的 JDK 代码实现如下：

```java
/** 返回同步状态的当前值，此操作的内存语义为 volatile 修饰的原子读操作 */
protected final int getState() {
    return state;
}

/** 设置同步状态的值，此操作的内存语义为 volatile 修饰的原子写操作 */
protected final void setState(int newState) {
    state = newState;
}

/**
 * 如果当前同步状态的值等于 expect（期望值），则自动将同步状态值设置为给定值 update。原子操作（CAS）
 * 此操作的内存语义为 volatile 修饰的原子读写操作
 */
protected final boolean compareAndSetState(int expect, int update) {
    // See below for intrinsics setup to support this
    return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
}
```

### 2.2. AQS 核心方法

AQS 是一个框架，只定义了一个接口，具体资源的获取、释放都交由自定义同步器去实现。不同的自定义同步器争用共享资源的方式也不同，自定义同步器在实现时只需实现共享资源 state 的获取与释放方式即可，至于具体线程等待队列的维护，如获取资源失败入队、唤醒出队等，AQS已经在顶层实现好，不需要具体的同步器再做处理。

自定义同步器的主要方法如下：

![](images/175110511239568.png)

> Tips: 同步器的实现是 AQS 的核心。以上方法均默认抛出 `UnsupportedOperationException`

#### 2.2.1. 基础使用实践

获取锁：

```java
// 如果获取锁失败
if (!tryAcquire(arg)) {
    // 入队, 可以选择阻塞当前线程 park unpark
}
```

释放锁

```java
// 如果释放锁成功
if (tryRelease(arg)) {
    // 让阻塞线程恢复运行
}
```

### 2.3. AQS 共享资源的方式

AQS 定义了两种资源共享方式：独占式（Exclusive）和共享式（Share）

- 独占式：只有一个线程能执行与访问资源，具体的 Java 实现有 `ReentrantLock`
- 共享式：多个线程可同时执行与访问资源，具体的 Java 实现有 `Semaphore` 和 `CountDownLatch`

ReentrantLock 对 **AQS 的独占方式实现**为：ReentrantLock 中的 state 初始值为 0 时表示无锁状态。在线程执行 tryAcquire() 获取该锁后 ReentrantLock 中的 state+1，这时该线程独占 ReentrantLock 锁，其他线程在通过 tryAcquire() 获取锁时均会失败，直到该线程释放锁后 state 再次为 0，其他线程才有机会获取该锁。该线程在释放锁之前可以重复获取此锁，每获取一次便会执行一次state+1，因此 ReentrantLock 也属于可重入锁。但获取多少次锁就要释放多少次锁，这样才能保证 state 最终为 0。如果获取锁的次数多于释放锁的次数，则会出现该线程一直持有该锁的情况；如果获取锁的次数少于释放锁的次数，则运行中的程序会报锁异常。

CountDownLatch 对 **AQS 的共享方式实现**为：CountDownLatch 将任务分为 N 个子线程去执行，将 state 也初始化为 N，N 与线程的个数一致 ， N 个子线程是并行执行的，每个子线程都在执行完成后 countDown() 一次，state 会执行 CAS 操作并减 1。在所有子线程都执行完成（即 `state=0`）时会 `unpark()` 主线程，然后主线程会从 `await()` 返回，继续执行后续的动作。

一般来说，自定义同步器要么采用独占方式，要么采用共享方式，实现类只需实现 tryAcquire、tryRelease 或 tryAcquireShared、tryReleaseShared 中的一组即可。但 AQS 也支持自定义同步器同时实现独占和共享两种方式，例如 `ReentrantReadWriteLock` 在读取时采用了共享方式，在写入时采用了独占方式。

## 3. 实现不可重入锁

### 3.1. （!待整理）自定义同步器

> TODO: 待整理

### 3.2. （!待整理）自定义锁

> TODO: 待整理


