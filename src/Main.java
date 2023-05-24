import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

interface ITask {
  void execute();
}

class TaskFactory {
  public static ITask createTask(String taskType, IAdapter Adapter, IEthernet ethernet) {
    Scanner scanner = new Scanner(System.in);
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
  private IAdapter adapter;

  public ReadFromMemoryTask(IAdapter adapter) {
    this.adapter = adapter;
  }

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

  @Override
  public byte[] getMem(int addr, int size) {
    return adapter.read(addr, size);
  }
}

class WriteToMemoryTask implements ITask, IWriteToMemoryTask {
  private IAdapter adapter;
  private IEthernet ethernet;

  public WriteToMemoryTask(IAdapter adapter, IEthernet ethernet) {
    this.adapter = adapter;
    this.ethernet = ethernet;
  }

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

  public static byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i].byteValue();
    }
    return primitiveArray;
  }

  @Override
  public void setMem(byte[] data, int addr) {
    adapter.write(data, addr);
  }
}

class ReadFromCardTask implements ITask, IReadFromCardTask {
  private IAdapter addapter;
  private int size;

  public ReadFromCardTask(IAdapter adapter) {
    this.addapter = adapter;
  }

  @Override
  public void execute() {
    Scanner scanner = new Scanner(System.in);

    System.out.println("");
    System.out.println("Read from card is in progressing with " + this.addapter.getClass().getSimpleName());
    System.out.print("Enter size you want to view: ");

    int a = scanner.nextInt();
    getCom(a);

    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logTaskCompletion(this.getClass().getSimpleName());
  }

  @Override
  public byte[] getCom(int size) {
    return addapter.read(0, size);
  }
}

class WriteToCardTask implements ITask, IWriteToCardTask {
  private IAdapter adapter;

  public WriteToCardTask(IAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public void execute() {
    Packet packet = new Packet();
    System.out.println("-Write To Card Task in progress with " + adapter.getClass().getSimpleName());

    setCom(convertByteArrayToPrimitive(packet.getData()));

    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logTaskCompletion(this.getClass().getSimpleName());
  }

  public byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i];
    }
    return primitiveArray;
  }

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

  public void addTask(ITask task) {
    taskQueue.add(task);
  }

  public void execute() {
    System.out.println("Execute tasks");
    System.out.println("");
    while (!taskQueue.isEmpty()) {
      this.task = taskQueue.poll();
      task.execute();
    }
  }

  public void deQue(ITask task) {
    if (taskQueue.contains(task)) {
      taskQueue.remove(task);
    } else {
      System.out.println("There is no task that is removed");
    }
  }
}

interface IRAM {
  byte[] get(int address, int size);

  int set(byte[] data, int address);
}

interface IAdapter {
  byte[] read(int addr, int size);

  int write(byte[] data, int addr);
}

class EthernetAdapter implements IAdapter {
  private Ethernet ethernet;

  public EthernetAdapter(Ethernet ethernet) {
    this.ethernet = ethernet;
  }

  @Override
  public byte[] read(int addr, int size) {
    return convertByteArrayToPrimitive(ethernet.read(size));
  }

  @Override
  public int write(byte[] data, int addr) {
    return ethernet.write(convertByteArrayToByte(data));
  }

  public Byte[] convertByteArrayToByte(byte[] byteArray) {
    Byte[] result = new Byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      result[i] = byteArray[i];
    }
    return result;
  }

  public byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i];
    }
    return primitiveArray;
  }
}

interface IEthernet {

  Byte[] read(Integer size);

  Integer write(Byte[] data);

}

class Ethernet implements IEthernet {
  private int size;
  private Byte[] data;

  @Override
  public Byte[] read(Integer size) {
    if (size <= 0 || size > data.length) {
      return new Byte[0];
    }
    Byte[] subset = Arrays.copyOfRange(data, 0, size);
    System.out.println("Read from communucation card data is : " + Arrays.toString(subset));
    System.out.println("");
    return subset;
  }

  @Override
  public Integer write(Byte[] data) {
    System.out.println("data size is : " + data.length);
    this.size = data.length;
    this.data = data;
    return setsize(data);
  }

  public Integer setsize(Byte[] data) {
    this.data = data;
    this.size = data.length;
    return data.length;
  }
}

class RamAdapter implements IAdapter {
  private IRAM ram;

  public RamAdapter(RAM ram) {
    this.ram = ram;
  }

  @Override
  public byte[] read(int addr, int size) {
    return ram.get(addr, size);
  }

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

  @Override
  public byte[] read(int addr, int size) {
    return convertIntArrayToByteArray(tokenring.receive(size));
  }

  @Override
  public int write(byte[] data, int addr) {
    return tokenring.send(convertByteArrayToIntArray(data), addr);
  }

  public byte[] convertIntArrayToByteArray(int[] intArray) {
    ByteBuffer buffer = ByteBuffer.allocate(intArray.length * Integer.BYTES);
    for (int value : intArray) {
      buffer.putInt(value);
    }
    return buffer.array();
  }

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

interface ITokenring {
  int[] receive(int size);

  int send(int[] data, int size);
}

class Tokenring implements ITokenring {
  private IEthernet ethernet;
  private int size;
  private int[] data;

  public Tokenring(IEthernet ethernet) {
    this.ethernet = ethernet;
  }

  @Override
  public int[] receive(int size) {
    this.size = size;
    this.data = convertByteArrayToIntArray(ethernet.read(size));
    return data;
  }

  public static int[] convertByteArrayToIntArray(Byte[] byteArray) {
    int[] intArray = new int[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      intArray[i] = byteArray[i].intValue();
    }
    return intArray;
  }

  @Override
  public int send(int[] data, int size) {
    Byte[] byteData = convertIntArrayToByteArray(data); // Convert int[] to Byte[]
    ethernet.write(byteData); // Send the Byte[] data through Ethernet
    return size;
  }

  public static Byte[] convertIntArrayToByteArray(int[] intArray) {
    Byte[] byteArray = new Byte[intArray.length];
    for (int i = 0; i < intArray.length; i++) {
      byteArray[i] = (byte) intArray[i];
    }
    return byteArray;
  }

  @Override
  public String toString() {
    return "\n*\tTokenring {                             *" +
        "\n*\t    ethernet = " + ethernet + "," +
        "\n*\t    size = " + size + "," +
        "\n*\t    data = " + Arrays.toString(data) +
        "\n*\t}                                      ";
  }
}

class RAM implements IRAM {
  private int size;
  private int address;
  private byte[] data;
  private IEthernet ethernet;
  private ArrayList<byte[]> dataList;

  public RAM(IEthernet ethernet) {
    this.ethernet = ethernet;
    this.dataList = new ArrayList<byte[]>();
  }

  @Override
  public byte[] get(int address, int size) {
    if (size <= data.length && address == this.address) {
      byte[] subset = Arrays.copyOfRange(data, 0, size);
      return subset;
    } else {
      System.out.println("the address is not correct for this request");
      return null;
    }
  }

  public byte[] convertByteArrayToPrimitive(Byte[] byteArray) {
    byte[] primitiveArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      primitiveArray[i] = byteArray[i].byteValue();
    }
    return primitiveArray;
  }

  @Override
  public int set(byte[] data, int address) {
    try {
      this.data = data;
      this.address = address;
      this.size = ByteBuffer.wrap(data).getInt();
      System.out.println("The new written data is :  " + Arrays.toString(this.data) + this.address + " " + this.size);
    } catch (Exception e) {
      this.size = 4;
      System.out.println("size cannot be smaller than 4 bytes. size is set to 4. ");
      System.out.println("");
      System.out.println("");
      System.out.println("The new written data is :  " + Arrays.toString(this.data) + this.address + " " + this.size);
    }
    return this.size;
  }

  @Override
  public String toString() {
    return "\n*\tRam {                                   *" +
        "\n*\t    size = " + size + "," +
        "\n*\t    address = " + address + "," +
        "\n*\t    data = " + Arrays.toString(data) +
        "\n*\t}                                      ";
  }
}

class Packet {
  Byte[] data = { 14, 15, 16, 17, 18, 23, 45, 56, 45, 78 };

  public Byte[] getData() {
    return data;
  }
}

class Thread {
  private TaskQueue taskQueue;

  public Thread() {
    this.taskQueue = new TaskQueue();
  }

  public void createTask(ITask task) {
    taskQueue.addTask(task);
    System.out.println(task.toString() + " Added to the Queue...");
  }

  public void executeTasks() {
    taskQueue.execute();
  }

  public void disdcardtheTask(ITask task) {
    taskQueue.deQue(task);
    System.out.println("");
    System.out.println(task.toString() + " Removed from Queue");
  }
}

class SystemEventLog {
  private static SystemEventLog instance;
  private FileWriter logFile;

  private SystemEventLog() {
    try {
      logFile = new FileWriter("log.log", true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static SystemEventLog getInstance() {
    if (instance == null) {
      instance = new SystemEventLog();
    }
    return instance;
  }

  public void logEvent(String event) {
    try {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
      LocalDateTime now = LocalDateTime.now();
      logFile.write(event + " - " + dateTimeFormatter.format(now) + "\n");
      logFile.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void logTaskCompletion(String taskName) {
    String event = "Task completed: " + taskName;
    logEvent(event);
  }

  public void closeLogFile() {
    try {
      logFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class CPU {
  private Thread thread;

  public CPU(Thread thread) {
    this.thread = thread;
  }

  public void addTasks(ITask task) {
    thread.createTask(task);
  }

  public void runCPU() {
    thread.executeTasks();
  }

  public void removeTask(ITask task) {
    thread.disdcardtheTask(task);
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

    CPU1.addTasks(writeToCardTask);
    CPU1.addTasks(readFromCardTask);
    CPU1.addTasks(writeToMemoryTask);
    CPU1.addTasks(readFromMemoryTask);

    // CPU 2 TASK----------------------------------------------------------
    ITask writeToCardTask2 = TaskFactory.createTask("WriteToCard", EthernetAdapter, null);
    ITask writeToMemoryTask2 = TaskFactory.createTask("WriteToMemory", RamAdapter, ethernet);

    CPU2.addTasks(writeToCardTask2);
    CPU2.addTasks(writeToMemoryTask2);
    CPU2.removeTask(writeToCardTask2);

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

    SystemEventLog.getInstance().closeLogFile();
  }
}
