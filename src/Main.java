// Münevver İrem Hatipoğlu
// Mert Günay
// Hüseyin Baran Özdemir
// Erinhan Ulutaş
// Process Modeling

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class TaskFactory {
  public static ITask createTask(String taskType, IAdapter Adapter, IEthernet ethernet) {
    if (taskType.equals("WriteToMemory")) {
      return new WriteToMemoryTask(Adapter, ethernet);
    } else if (taskType.equals("ReadFromMemory")) {
      return new ReadFromMemoryTask(Adapter);
    } else if (taskType.equals("ReadFromCard")) {
      return new ReadFromCardTask(Adapter);
    } else if (taskType.equals("WriteToCard")) {
      return new WriteToCardTask(Adapter);
    }
    return null;
  }
}

class ReadFromMemoryTask implements IReadFromMemoryTask {
  private IAdapter adapter;

  public ReadFromMemoryTask(IAdapter adapter) {
    this.adapter = adapter;
  }

  // execute task
  @Override
  public void execute() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("");
    System.out.println("-Read from memory task is in progressing with " + adapter.getClass().getSimpleName());

    System.out.print("Enter the address of the process: ");
    int address = scanner.nextInt();

    System.out.print("Enter the size of the process: ");
    int size = scanner.nextInt();

    byte[] data = getMem(address, size);

    System.out.println("data read from memory :" + Arrays.toString(data));
    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logTaskCompletion(this.getClass().getSimpleName());
  }

  // get data from adapter and read from memory
  @Override
  public byte[] getMem(int addr, int size) {
    return adapter.read(addr, size);
  }
}

class WriteToMemoryTask implements IWriteToMemoryTask {
  private IAdapter adapter;
  private IEthernet ethernet;

  public WriteToMemoryTask(IAdapter adapter, IEthernet ethernet) {
    this.adapter = adapter;
    this.ethernet = ethernet;
  }

  // execute task
  @Override
  public void execute() {
    if (adapter instanceof RamAdapter) {
      Scanner scanner = new Scanner(System.in);
      System.out.println("-Write to Memory task is  progressing with " + adapter.getClass().getSimpleName());

      System.out.print("Enter size of data to write into memory: ");
      int size = scanner.nextInt();

      System.out.print("Enter address of data to write into memory: ");
      int address = scanner.nextInt();

      setMem(convertByteArrayToPrimitive(ethernet.read(size)), address);

      SystemEventLog eventLog = SystemEventLog.getInstance();
      eventLog.logTaskCompletion(this.getClass().getSimpleName());
    } else {
      System.out.println("There is not access to memory without Ram");
    }
  }

  // convert Byte[] to byte[]
  public static byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i].byteValue();
    }
    return primitiveArray;
  }

  // set data to adapter and write to memory
  @Override
  public void setMem(byte[] data, int addr) {
    adapter.write(data, addr);
  }
}

class ReadFromCardTask implements IReadFromCardTask {
  private IAdapter addapter;

  public ReadFromCardTask(IAdapter adapter) {
    this.addapter = adapter;
  }

  // execute task
  @Override
  public void execute() {
    Scanner scanner = new Scanner(System.in);

    System.out.println("");
    System.out.println("Read from card is in progressing with " + this.addapter.getClass().getSimpleName());
    System.out.print("Enter size you want to view: ");

    int size = scanner.nextInt();

    // send size to adapter
    getCom(size);

    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logTaskCompletion(this.getClass().getSimpleName());
  }

  // get data from adapter and read from card
  @Override
  public byte[] getCom(int size) {
    return addapter.read(0, size);
  }
}

class WriteToCardTask implements IWriteToCardTask {
  private IAdapter adapter;

  public WriteToCardTask(IAdapter adapter) {
    this.adapter = adapter;
  }

  // execute task
  @Override
  public void execute() {
    Packet packet = new Packet();
    System.out.println("-Write To Card Task in progress with " + adapter.getClass().getSimpleName());

    // set data to packet
    setCom(convertByteArrayToPrimitive(packet.getData()));

    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logTaskCompletion(this.getClass().getSimpleName());
  }

  // convert Byte[] to byte[]
  public byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i];
    }
    return primitiveArray;
  }

  // send data to adapter and write to card
  @Override
  public int setCom(byte[] data) {
    return adapter.write(data, 0);
  }
}

class TaskQueue {
  private Queue<ITask> taskQueue;
  private ITask task;

  public TaskQueue() {
    taskQueue = new LinkedList<>();
  }

  // add task to queue
  public void addTask(ITask task) {
    taskQueue.add(task);
  }

  // execute tasks
  public void execute() {
    System.out.println("Execute tasks");
    System.out.println("");
    while (!taskQueue.isEmpty()) {
      this.task = taskQueue.poll();
      task.execute();
    }
  }

  // remove task from queue
  public void deQue(ITask task) {
    if (taskQueue.contains(task)) {
      taskQueue.remove(task);
    } else {
      System.out.println("There is no task that is removed");
    }
  }
}

class EthernetAdapter implements IAdapter {
  private Ethernet ethernet;

  public EthernetAdapter(Ethernet ethernet) {
    this.ethernet = ethernet;
  }

  // read from ethernet
  @Override
  public byte[] read(int addr, int size) {
    return convertByteArrayToPrimitive(ethernet.read(size));
  }

  // write to ethernet
  @Override
  public int write(byte[] data, int addr) {
    return ethernet.write(convertByteArrayToByte(data));
  }

  // convert byte[] to Byte[]
  public Byte[] convertByteArrayToByte(byte[] byteArray) {
    Byte[] result = new Byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      result[i] = byteArray[i];
    }
    return result;
  }

  // convert Byte[] to byte[]
  public byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i];
    }
    return primitiveArray;
  }
}

class Ethernet implements IEthernet {
  private Byte[] data;

  // read from ethernet
  @Override
  public Byte[] read(Integer size) {
    if (size <= 0 || size > data.length) {
      return new Byte[0];
    } // if size is not valid return empty array
    Byte[] subset = Arrays.copyOfRange(data, 0, size); // subset of data
    System.out.println("Read from communucation card data is : " + Arrays.toString(subset));
    System.out.println("");
    return subset;
  }

  // write to communication card
  @Override
  public Integer write(Byte[] data) {
    System.out.println("data size is : " + data.length);
    this.data = data; // set data
    return setSize(data); // return size of data
  }

  // set size of data
  public Integer setSize(Byte[] data) {
    this.data = data;
    return data.length; // return size of data
  }
}

class RamAdapter implements IAdapter {
  private IRAM ram;

  public RamAdapter(RAM ram) {
    this.ram = ram;
  }

  // read from ram
  @Override
  public byte[] read(int addr, int size) {
    return ram.get(addr, size);
  }

  // write to ram
  @Override
  public int write(byte[] data, int addr) {
    return ram.set(data, addr);
  }
}

class TokenringAdapter implements IAdapter {
  private ITokenring tokenring;

  public TokenringAdapter(Tokenring tokenring) {
    this.tokenring = tokenring;
  }

  // read from tokenring
  @Override
  public byte[] read(int addr, int size) {
    return convertIntArrayToByteArray(tokenring.receive(size));
  }

  // write to tokenring
  @Override
  public int write(byte[] data, int addr) {
    return tokenring.send(convertByteArrayToIntArray(data), addr);
  }

  // convert int array to byte array
  public byte[] convertIntArrayToByteArray(int[] intArray) {
    ByteBuffer buffer = ByteBuffer.allocate(intArray.length * Integer.BYTES);
    for (int value : intArray) {
      buffer.putInt(value);
    }
    return buffer.array();
  }

  // convert byte array to int array
  public int[] convertByteArrayToIntArray(byte[] byteArray) {
    int length = byteArray.length / 4;
    int[] intArray = new int[length];
    ByteBuffer buffer = ByteBuffer.wrap(byteArray);
    for (int i = 0; i < length; i++) {
      intArray[i] = buffer.getInt();
    }
    return intArray;
  }
}

class Tokenring implements ITokenring {
  private IEthernet ethernet;
  private int size;
  private int[] data;

  public Tokenring(IEthernet ethernet) {
    this.ethernet = ethernet;
  }

  // receive data from ethernet
  @Override
  public int[] receive(int size) {
    this.size = size;
    this.data = convertByteArrayToIntArray(ethernet.read(size));
    return data;
  }

  // convert byte array to int array
  public static int[] convertByteArrayToIntArray(Byte[] byteArray) {
    int[] intArray = new int[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      intArray[i] = byteArray[i].intValue();
    }
    return intArray;
  }

  // send data to ethernet
  @Override
  public int send(int[] data, int size) {
    Byte[] byteData = convertIntArrayToByteArray(data); // Convert int[] to Byte[]
    ethernet.write(byteData); // Send the Byte[] data through Ethernet
    return size;
  }

  // convert int array to byte array
  public static Byte[] convertIntArrayToByteArray(int[] intArray) {
    Byte[] byteArray = new Byte[intArray.length];
    for (int i = 0; i < intArray.length; i++) {
      byteArray[i] = (byte) intArray[i];
    }
    return byteArray;
  }

  // collect data and return as string
  @Override
  public String toString() {
    return "\n*\tTokenring {                             *" +
        "\n*\t    ethernet = " + ethernet + "," +
        "\n*\t    size = " + size + "," +
        "\n*\t    data = " + Arrays.toString(data) +
        "\n*\t}                                      ";
  }
}

class Packet {
  Byte[] data = { 14, 15, 16, 17, 18, 23, 45, 56, 45, 78 };

  // get ethernet packet data
  public Byte[] getData() {
    return data;
  }
}

class Thread {
  private TaskQueue taskQueue;

  public Thread() {
    this.taskQueue = new TaskQueue();
  }

  // add task to queue
  public void createTask(ITask task) {
    taskQueue.addTask(task);
    System.out.println(task.toString() + " Added to the Queue...");
  }

  // execute tasks
  public void executeTasks() {
    taskQueue.execute();
  }

  // discard task from queue
  public void disdcardtheTask(ITask task) {
    taskQueue.deQue(task);
    System.out.println("");
    System.out.println(task.toString() + " Removed from Queue");
  }
}

public class Main {
  public static void main(String[] args) {
    Thread thread = new Thread();
    Thread thread1 = new Thread();
    CPU CPU1 = new CPU(thread);
    CPU CPU2 = new CPU(thread1);

    Ethernet ethernet = new Ethernet();
    RAM ram = new RAM(ethernet);
    Tokenring tokenring = new Tokenring(ethernet);

    IAdapter RamAdapter = new RamAdapter(ram);
    IAdapter EthernetAdapter = new EthernetAdapter(ethernet);
    IAdapter TokenringAdapter = new TokenringAdapter(tokenring);

    // CPU 1 TASK----------------------------------------------------------
    ITask writeToCardTask = TaskFactory.createTask("WriteToCard", EthernetAdapter, null);
    ITask readFromCardTask = TaskFactory.createTask("ReadFromCard", TokenringAdapter, null);
    ITask writeToMemoryTask = TaskFactory.createTask("WriteToMemory", RamAdapter, ethernet);
    ITask readFromMemoryTask = TaskFactory.createTask("ReadFromMemory", RamAdapter, null);

    // add tasks to CPU1
    CPU1.addTasks(writeToCardTask);
    CPU1.addTasks(readFromCardTask);
    CPU1.addTasks(writeToMemoryTask);
    CPU1.addTasks(readFromMemoryTask);

    // CPU 2 TASK----------------------------------------------------------
    ITask writeToCardTask2 = TaskFactory.createTask("WriteToCard", EthernetAdapter, null);
    ITask writeToMemoryTask2 = TaskFactory.createTask("WriteToMemory", RamAdapter, ethernet);

    // add tasks to CPU2
    CPU2.addTasks(writeToCardTask2);
    CPU2.addTasks(writeToMemoryTask2);
    CPU2.removeTask(writeToCardTask2);

    // run CPU1 and CPU2
    CPU1.runCPU();
    CPU2.runCPU();

    System.out.println("");
    System.out.println("+Observe the initial Memory and Tokenring data :*");
    System.out.println("*                                               *");
    System.out.println("*                  +Ram Data+                   *");
    System.out.print("*                                               *");
    System.out.println(ram.toString() + " *");
    System.out.println("*                                               *");
    System.out.println("*************************************************");
    System.out.println("*             +Tokenring Card Data+             *");
    System.out.print("*                                               *");
    System.out.println(tokenring.toString() + " *");
    System.out.println("*                                               *");
    System.out.println("*************************************************");

    // close log file's scanner
    SystemEventLog.getInstance().closeLogFile();
  }
}
