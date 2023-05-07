package baran;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

interface ITask {
  void execute();
}

class Task {
  private ArrayList tasks[];

  public String getTask() {
    return "Hello World";
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

class TaskQueue {
  private Queue<ITask> taskQueue;

  public TaskQueue() {
    taskQueue = new LinkedList<>();
  }

  public void addTask(ITask task) {
    taskQueue.add(task);
  }

  public void execute() {
    while (!taskQueue.isEmpty()) {
      ITask task = taskQueue.poll();
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
  }
}