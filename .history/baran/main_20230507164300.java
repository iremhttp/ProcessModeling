package baran;

import java.util.ArrayList;
import java.util.List;

class ThreadPool {
  private static ThreadPool instance = null;
  private static int MAX_THREADS = 2;
  private Thread[] threads;

  private ThreadPool() {
    threads = new Thread[MAX_THREADS];
    for (int i = 0; i < MAX_THREADS; i++) {
      threads[i] = new Thread();
    }
  }

  public static synchronized ThreadPool getInstance() {
    if (instance == null) {
      instance = new ThreadPool();
    }
    return instance;
  }

  public Thread getThread() {
    for (int i = 0; i < MAX_THREADS; i++) {
      if (!threads[i].isAlive()) {
        return threads[i];
      }
    }
    return null;
  }
}

interface Task {
  void execute();
}

class ReadMemoryTask implements Task {
  private int addr;
  private int size;

  public ReadMemoryTask(int addr, int size) {
    this.addr = addr;
    this.size = size;
  }

  @Override
  public void execute() {
    // Memory read operation
  }
}

class WriteMemoryTask implements Task {
  private byte[] data;
  private int addr;

  public WriteMemoryTask(byte[] data, int addr) {
    this.data = data;
    this.addr = addr;
  }

  @Override
  public void execute() {
    // Memory write operation
  }
}

class ReadCardTask implements Task {
  private int size;

  public ReadCardTask(int size) {
    this.size = size;
  }

  @Override
  public void execute() {
    // Card read operation
  }
}

class WriteCardTask implements Task {
  private byte[] data;

  public WriteCardTask(byte[] data) {
    this.data = data;
  }

  @Override
  public void execute() {
    // Card write operation
  }
}

class TaskFactory {
  public static Task createTask(String taskType, Object... args) {
    switch (taskType) {
      case "ReadMemory":
        return new ReadMemoryTask((int) args[0], (int) args[1]);
      case "WriteMemory":
        return new WriteMemoryTask((byte[]) args[0], (int) args[1]);
      case "ReadCard":
        return new ReadCardTask((int) args[0]);
      case "WriteCard":
        return new WriteCardTask((byte[]) args[0]);
      default:
        throw new IllegalArgumentException("Invalid task type: " + taskType);
    }
  }
}

interface TaskFinishedObserver {
  void onTaskFinished(String taskType);
}

class TaskExecutor {
  private ThreadPool threadPool;
  private List<TaskFinishedObserver> observers;

  public TaskExecutor() {
    threadPool = ThreadPool.getInstance();
    observers = new ArrayList<>();
  }

  public void executeTask(String taskType, Object... args) {
    Task task = TaskFactory.createTask(taskType, args);
    Thread thread = threadPool.getThread();
    if (thread != null) {
      thread.start();
      task.execute();
      notifyObservers(taskType);
    }
  }

  public void addObserver(TaskFinishedObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(TaskFinishedObserver observer) {
    observers.remove(observer);
  }

  private void notifyObservers(String taskType) {
    for (TaskFinishedObserver observer : observers) {
      observer.onTaskFinished(taskType);
    }
  }
}

class SystemEventLog implements TaskFinishedObserver {
  private static SystemEventLog instance = null;

  private SystemEventLog() {
    // Private constructor to prevent instantiation
  }

  public static synchronized SystemEventLog getInstance() {
    if (instance == null) {
      instance = new SystemEventLog();
    }
    return instance;
  }

  @Override
  public void onTaskFinished(String taskType) {
    // Log the task information
  }
}

public class Main {
  public static void main(String[] args) {
    // TaskExecutor sınıfı oluşturulur
    TaskExecutor taskExecutor = new TaskExecutor();

    // SystemEventLog sınıfı oluşturulur ve TaskFinishedObserver olarak kaydedilir
    SystemEventLog systemEventLog = SystemEventLog.getInstance();
    taskExecutor.addObserver(systemEventLog);

    // Bellek okuma görevi yürütülür
    taskExecutor.executeTask("ReadMemory", 0x1000, 0x10);

    // Bellek yazma görevi yürütülür
    byte[] data = { 0x01, 0x02, 0x03 };
    taskExecutor.executeTask("WriteMemory", data, 0x2000);

    // Kart okuma görevi yürütülür
    taskExecutor.executeTask("ReadCard", 0x20);

    // Kart yazma görevi yürütülür
    byte[] cardData = { 0x04, 0x05, 0x06 };
    taskExecutor.executeTask("WriteCard", cardData);
  }
}