# Java并发编程-常用API

## 1. Thread 类

### 1.1. 简介

```java
public class Thread implements Runnable
```

`Thread` 类是用于创建一个新的线程并执行。`Thread` 类现实了 `Runnable` 接口

创建新执行线程有两种方法。一种方法是将类声明为 `Thread` 的子类。该子类应重写 `Thread` 类的 `run` 方法。

### 1.2. 构造方法

```java
public Thread()
```

直接创建一个线程对象，此构造方法与 `Thread(null, null, gname)` 具有相同的作用，自动生成的线程名称的形式为 `"Thread-"+n`，其中的 n 为整数。

```java
public Thread(String name)
```

创建一个线程并设置线程的名称，此构造方法与 `Thread(null, null, name)` 具有相同的作用。

```java
public Thread(Runnable target)
```

使用 `Runnable` 接口实现类对象来创建一个线程对象。此构造方法与 `Thread(null, target, gname)` 具有相同的作用，自动生成的线程名称的形式为 `"Thread-"+n`，其中的 n 为整数。

```java
public Thread(Runnable target, String name)
```

使用 `Runnable` 接口实现类对象来创建一个线程对象，并设置线程名称。此构造方法与 `Thread(null, target, name)` 具有相同的作用。

```java
public Thread(ThreadGroup group, Runnable target, String name)
```

创建一个线程，将 `target` 作为其运行对象，将指定的 `name` 作为其名称，并作为 `group` 所引用的线程组的一员。

> 建议创建线程时，都给线程设置名称，方便日志的追踪

### 1.3. Thread类常用方法

```java
public void run()
```

- `run` 方法放的是在子线程上面执行的代码。直接调用 `run` 方法，不会开启新的线程。如果在构造 `Thread` 对象时传递了 `Runnable` 参数，则线程启动后会调用 `Runnable` 中的 `run` 方法，否则默认不执行任何操作。但可以创建 `Thread` 的子类对象，重写该方法

```java
public void start()
```

- 调用系统资源创建一个新的线程，新的线程会执行对象的`run`方法。此方法只是让线程进入就绪状态，`run` 方法中代码不一定立刻运行（CPU 的时间片还没分给它）。每个线程对象的 `start` 方法只能调用一次，如果调用了多次会出现 `IllegalThreadStateException`

```java
public long getId()
```

- 获取该线程的标识符ID（正长整型）。在创建该线程时生成。线程 ID 是唯一的且不变。线程终止时，该线程 ID 可以被重新使用。

```java
public final String getName()
```

- 获取线程的名称，默认命名规则：`Thread-序号`。

```java
public final void setName(String name)
```

- 设置线程名称。使用场景，当程序出现异常时，可以通过名字判断异常出现哪个线程中。
    - `name`参数：该线程的新名称。

```java
public final void join() throws InterruptedException
```

- 等待线程运行结束

```java
public final synchronized void join(long millis) throws InterruptedException
```

- 等待线程运行结束，最多等待时长为 `millis` 毫秒。如果设置参数 `millis` 为 0，则代表一直等下去。
    - `millis`参数：最大等待时间（单位：毫秒）。

```java
public final int getPriority()
```

- 获取线程优先级

```java
public final void setPriority(int newPriority)
```

- 修改线程优先级。较大的优先级能提高该线程被 CPU 调度的机率。*但具体还是由CPU调度决定，此优先级数影响不大*
    - `newPriority`参数：要为线程设定的优先级，规定为1~10的整数

```java
public State getState()
```

- 获取当前线程的状态。Java 中线程状态是用 `Thread.State` 枚举表示。**返回值** `Thread.State` 枚举：
    - `NEW` 至今尚未启动的线程处于这种状态。
    - `RUNNABLE` 正在 Java 虚拟机中执行的线程处于这种状态。
    - `BLOCKED` 受阻塞并等待某个监视器锁的线程处于这种状态。
    - `WAITING` 无限期地等待另一个线程来执行某一特定操作的线程处于这种状态。
    - `TIMED_WAITING` 等待另一个线程来执行取决于指定等待时间的操作的线程处于这种状态。
    - `TERMINATED` 已退出的线程处于这种状态。

```java
public boolean isInterrupted()
```

- 判断是否被打断，**不会清除打断标记**。线程的中断状态不受该方法的影响。

```java
public final native boolean isAlive();
```

- 判断线程是否存活（还没有运行完毕）。如果线程已经启动且尚未终止，则为活动状态。

**返回**：

- 如果该线程处于活动状态，则返回 true；否则返回 false。

```java
public void interrupt()
```

- 打断线程。如果被打断线程正在 `sleep`，`wait`，`join` 会导致被打断的线程抛出 `InterruptedException`，并**清除打断标记**；如果打断的正在运行的线程，则会**设置打断标记** ；`park` 的线程被打断，也会**设置打断标记**

```java
public static boolean interrupted()
```

- 判断当前线程是否被打断，**会清除打断标记**。即如果连续两次调用该方法，则第二次调用将返回 false（在第一次调用已清除了其中断标记之后，且第二次调用检验完中断状态前，当前线程再次中断的情况除外）

```java
public static native Thread currentThread();
```

- 获得当前方法执行所在的线程对象。哪个线程执行这行代码就返回哪个线程

```java
ublic static native void sleep(long millis) throws InterruptedException;
```

- 让当前方法所在的线程睡眠 `millis` 毫秒，在指定的毫秒数内让当前正在执行的线程休眠（暂停执行）。休眠时会让出 cpu 的时间片给其它线程

```java
public static native void yield();
```

- 提示线程调度器让出当前线程对CPU的使用，并执行其他线程。*主要是为了测试和调试*

```java
public final void setDaemon(boolean on)
```

- 将该线程标记为守护线程或用户线程。<font color=red>**注意：该方法必须在启动线程前调用。**</font>
    - `on`参数：设置为 `true`，则将该线程标记为守护线程。

```java
public static void dumpStack()
```

- 将当前线程的堆栈跟踪打印至标准错误流。

## 2. FutureTask 类

### 2.1. 简介

```java
public class FutureTask<V> implements RunnableFuture<V>
```

创建一个可取消的异步任务，可使用 `FutureTask` 包装 `Callable` 或 `Runnable` 对象。该异步任务具有返回值，在任务完成后再能获取返回结果，如果在任务未完成，调用 `get` 方法则会阻塞当前线程

其中泛型是 `V` 是此 `FutureTask` 的 `get` 方法所返回的结果类型。

### 2.2. 构造方法

```java
public FutureTask(Callable<V> callable)
```

创建一个 `FutureTask` 对象，一旦运行就执行给定的 `Callable` 的实现逻辑

**参数**：

- `callable`：可调用的任务

```java
public FutureTask(Runnable runnable, V result)
```

创建一个 `FutureTask` 对象，一旦运行就执行给定的 `Runnable` 的实现逻辑，并在成功完成时通过 `get` 方法返回给定的结果

**参数**：

- `runnable`：待执行的线务任务
- `result`：成功完成时要返回的结果。如果不需要特定的结果可以`new FutureTask(runnable, null)`

### 2.3. 常用方法

```java
public V get() throws InterruptedException, ExecutionException
```

调用此方法，`FutureTask`对象将当前线程阻塞，并等待执行完成后，返回结果。







