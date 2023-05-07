package baran;

import java.util.LinkedList;
import java.util.Queue;

interface ITask {
  void execute();
}

class Task {
  // private ITask task;
  private TaskQueue taskQueue;

  public Task(ITask task) {
    // this.task = task;
    taskQueue.addTask(task);
  }

  public void execute() {
    taskQueue.execute();
  }

}

interface IReadFromMemoryTask {
  byte[] getMem(int addr, int size);
}

interface IWriteToMemoryTask {
  void setMem(byte[] data, int addr);
}

interface IReadFromCardTask {
  byte[] getCom(int size);
}

interface IWriteToCardTask {
  int setCom(byte[] data);
}

class ReadFromMemoryTask implements ITask, IReadFromMemoryTask {
  @Override
  public void execute() {
    System.out.println("Read from memory");
  }

  @Override
  public byte[] getMem(int addr, int size) {
    return null;
  }

}

class WriteToMemoryTask implements ITask, IWriteToMemoryTask {
  @Override
  public void execute() {
    System.out.println("Write to memory");
  }

  @Override
  public void setMem(byte[] data, int addr) {
  }
}

class ReadFromCardTask implements ITask, IReadFromCardTask {
  @Override
  public void execute() {
    System.out.println("Read from card");
  }

  @Override
  public byte[] getCom(int size) {
    return null;
  }
}

class WriteToCardTask implements ITask, IWriteToCardTask {
  @Override
  public void execute() {
    System.out.println("Write to card");
  }

  @Override
  public int setCom(byte[] data) {
    return 0;
  }
}

class TaskQueue {
  private Queue<ITask> taskQueue;
  private ITask task;

  public TaskQueue() {
    taskQueue = new LinkedList<>();
  }

  public void addTask(ITask task) {
    taskQueue.add(task);
  }

  public void execute() {
    while (!taskQueue.isEmpty()) {
      this.task = taskQueue.poll();
      task.execute();
    }
  }
}

interface IRam {
  byte[] get(int address, int size);

  int set(byte[] data, int address);
}

interface IMemory {
  byte[] getMem(int address, int size);

  void setMem(byte[] data, int address);
}

interface ICard {
  byte[] getCom(int address);

  int setCom(byte[] data);
}

class Ram implements IRam {
  private int size;
  private int address;

  public Ram(int address, int size) {
    this.address = address;
    this.size = size;
  }

  @Override
  public byte[] get(int address, int size) {
    throw new UnsupportedOperationException("Unimplemented method 'get'");
  }

  @Override
  public int set(byte[] data, int address) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'set'");
  }

}

class Memory {
  private IRam ram;
  private int size;
  private int address;

  public Memory(int address, int size) {
    this.address = address;
    this.size = size;

  }

}

public class Main {
  public static void main(String[] args) {
    Task task1 = new Task(new ReadFromMemoryTask());
    Task task2 = new Task(new WriteToMemoryTask());
    Task task3 = new Task(new ReadFromCardTask());
    Task task4 = new Task(new WriteToCardTask());

    // thread 1
    task1.execute();
    task2.execute();
    task3.execute();
    task4.execute();
  }
}