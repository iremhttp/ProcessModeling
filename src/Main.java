import java.nio.ByteBuffer;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

interface ITask {
  void execute();
}

class TaskFactory {
  public static ITask createTask(String taskType, IAdapter adapter, Memorycard memorycard, Communicationcard card) {
    if (taskType.equals("WriteToMemory")) {
      return new WriteToMemoryTask(adapter, memorycard);
    } else if (taskType.equals("ReadFromMemory")) {
      return new ReadFromMemoryTask(adapter, memorycard);
    } else if (taskType.equals("ReadFromCard")) {
      return new ReadFromCardTask(adapter, card);
    } else if (taskType.equals("WriteToCard")) {
      return new WriteToCardTask(adapter, card);
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
 private final Memorycard memorycard;
 private IAdapter adapter ;
 private int address;
 private int size ;

  public ReadFromMemoryTask(IAdapter adapter, Memorycard memorycard) {
    this.adapter = adapter;
    this.memorycard = memorycard;
  }
  @Override
  public void execute() {
    System.out.println("-Read from memory-");

    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the address of the process:");
    int address = scanner.nextInt();
    System.out.println("Enter the size of the process:");
    int size = scanner.nextInt();

    getMem(address, size);
    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logEvent("Task completed: " + this.getClass().getSimpleName());

  }
  @Override
  public byte[] getMem(int addr, int size) {
    if (memorycard.getAddress() == addr && memorycard.getSize() == size) {
      return adapter.read(addr, size);
    } else {
      return null;
    }
  }
}

class WriteToMemoryTask implements ITask, IWriteToMemoryTask {
  private Memorycard memorycard ;
  private  IAdapter adapter;
  public  WriteToMemoryTask(IAdapter adapter, Memorycard memorycard) {
    this.adapter=adapter;
    this.memorycard = memorycard;
  }
  @Override
  public void execute() {
    if (adapter instanceof RamAdapter) {
      System.out.println("-Write to Memory-");
      Scanner scanner = new Scanner(System.in);
      System.out.print("Enter the size of the byte array: ");
      int size = scanner.nextInt();
      byte[] byteArray = new byte[size];
      System.out.println("Enter " + size + " byte values:");
      for (int i = 0; i < size; i++) {
        byteArray[i] = scanner.nextByte();
      }
      scanner.nextLine();
      System.out.println("The byte array you entered is: ");
      for (byte b : byteArray) {
        System.out.print(b + " ");
      }

      System.out.println("\n Enter address: ");
      int address = scanner.nextInt();
      setMem(byteArray, address);
      SystemEventLog eventLog = SystemEventLog.getInstance();
      eventLog.logEvent("Task completed: " + this.getClass().getSimpleName());

    }
    else {
  /*
   * TODO: Ethernet Adapter part
   */

    }
  }

  @Override
  public void setMem(byte[] data, int addr) {
    adapter.write(data,addr);
  }
}

class ReadFromCardTask implements ITask, IReadFromCardTask {

  private IAdapter addapter ;
  private Communicationcard card;

  public  ReadFromCardTask (IAdapter adapter , Communicationcard card) {
    this.addapter = adapter;
    this.card=card;

  }
  @Override
  public void execute() {
    System.out.println("Read from card");
    getCom(card.getSize());
    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logEvent("Task completed: " + this.getClass().getSimpleName());

  }

  @Override
  public byte[] getCom(int size) {

    return addapter.read(card.getAddress(),size);
  }
}

class WriteToCardTask implements ITask, IWriteToCardTask {
  private IAdapter adapter;
  private Communicationcard card ;

  public WriteToCardTask(IAdapter adapter, Communicationcard card) {
    this.adapter = adapter;
    this.card=card;
  }


  @Override
  public void execute() {
Scanner scanner=new Scanner(System.in);
    System.out.println("-Write to card-");
   System.out.println("Enter the size of the byte array: ");
   int size = scanner.nextInt();
   byte[] byteArray = new byte[size];
   System.out.println("Enter " + size + " byte values:");
   for (int i = 0; i < size; i++) {
     byteArray[i] = scanner.nextByte();
   }

   System.out.println("The byte array you entered is: ");
   for (byte b : byteArray) {
     System.out.print(b + " ");
   }
    setCom(byteArray);
    SystemEventLog eventLog = SystemEventLog.getInstance();
    eventLog.logEvent("Task completed: " + this.getClass().getSimpleName());

  }

  @Override
  public int setCom(byte[] data) {

   return adapter.write(data, card.getAddress());
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
    while (!taskQueue.isEmpty()) {
      this.task = taskQueue.poll();
      task.execute();
    }
  }

  public void discardtask (ITask task) {

    if (taskQueue.contains(task)) {
      taskQueue.remove(task);
    }
    else {
      System.out.println("There is no task that is removed");
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

class Communicationcard {
  private int address;
  private int size;
  private byte[] data;

  public Communicationcard () {

  }


  public int getAddress() {
    return address;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "Communicationcard{" +
            "address=" + getAddress() +
            ", size=" + getSize() +
            ", data=" + Arrays.toString(data) +
            '}';
  }
}
class Memorycard {
  private int address;
  private int size;
  private byte[] data;


  public Memorycard (int address , int size , byte [] data) {
    this.address =address;
    this.size = size ;
    this.data = data;

  }


  public int getAddress() {
    return address;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public int getSize() {
    return size;
  }

  public int setSize(byte[] data, int address) {

      this.data =data;
      this.address = address;
      int value = ByteBuffer.wrap(data).getInt();
      this.size = value ;
      System.out.println("The new data is :  "+ Arrays.toString(this.data)+ this.address+this.size );
      return this.size;
    /*
     * TODO: System event log has finished.
     */
  }

  public byte[] getData(int size,int address) {
    if (this.size ==  size &&  this.address ==address) {
      System.out.println("Data is on read processing : ");
      System.out.println("Data read : "+Arrays.toString(this.data));

      return this.data;
      /*
       * TODO: System event log has finished.
       */
    }else
    {
      System.out.println( "There is no data...");
      return null;

    }
  }

  public void setData(byte[] data) {
    this.data = data;
  }


  @Override
  public String toString() {
    return "Memorycard{" +
            "address=" + address +
            ", size=" + size +
            ", data=" + Arrays.toString(data) +
            '}';
  }
}


interface  IAdapter {

  byte[] read(int addr, int size);
  int write(byte[] data, int addr);

}
class EthernetAdapter implements IAdapter {

private  Ethernet ethernet ;
public EthernetAdapter (Ethernet ethernet) {
  this.ethernet=ethernet;


}

  @Override
  public byte[] read(int addr, int size) {

    return convertByteArrayToPrimitive(ethernet.read(size));
  }

  @Override
  public int write(byte[] data, int addr) {

    return ethernet.write(convertByteArrayToByte(data));
  }
  public Byte convertByteArrayToByte(byte[] byteArray) {
    if (byteArray.length > 0) {
      return Byte.valueOf(byteArray[0]);
    } else {
      return null; // or handle the case when the array is empty
    }
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

  Byte [] read (Integer size);
  Integer write(Byte data);

}
class Ethernet implements  IEthernet{


  @Override
  public Byte[] read(Integer size) {
    return new Byte[0];
  }

  @Override
  public Integer write(Byte data) {

    return null;
  }
}



class RamAdapter implements IAdapter {
 private IRam ram ;
 public RamAdapter (Ram ram) {
   this.ram = ram;

 }
  @Override
  public byte[] read(int addr, int size) {
    return ram.get(addr,size);
  }

  @Override
  public int write(byte[] data, int addr) {
    return ram.set(data,addr);
  }
}


class TokenringAdapeter implements IAdapter {

  private ITokenring tokenring;

   public TokenringAdapeter (Tokenring tokenring) {
    this.tokenring = tokenring;
  }
  @Override
  public byte[] read(int addr, int size) {
    return convertIntArrayToByteArray(tokenring.receive(size));
  }

  @Override
  public int write(byte[] data, int addr) {
    return tokenring.send(convertByteArrayToIntArray(data),addr);
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
interface ITokenring  {
  int [] receive (int size);
  int send (int []data, int size) ;
}
class Tokenring implements ITokenring {


  @Override
  public int[] receive(int size) {
    return new int[0];
  }

  @Override
  public int send(int[] data, int size) {
    return 0;
  }
}

class Ram implements IRam {
  private Memorycard memory;
  public  Ram (Memorycard memorycard) {
  this.memory =memorycard;

 }
 @Override
  public byte[] get(int address, int size) {
    return memory.getData(size,address);
  }

  @Override
  public int set(byte[] data, int address) {
   return memory.setSize(data, address);
  }

}

class Thread {

   private  TaskQueue taskQueue ;
   private ArrayList<ITask>tasklist=new ArrayList<>();

  public Thread (){
    this.taskQueue=new TaskQueue();

  }
  public void createTask (ITask task) {
    tasklist.add(task);
  }
  public void executeTasks(){
    for (int a = 0 ; a<tasklist.size(); a++) {
      taskQueue.addTask(tasklist.get(a));
      System.out.println(tasklist.get(a).toString()+" added to the queue...");
    }
      taskQueue.execute();
  }
  public void disdcardtheTask(ITask task) {
    tasklist.remove(task);
  }

}
class SystemEventLog{
  private static SystemEventLog instance;
  private FileWriter logFile;
  private SystemEventLog() {
    try {
      logFile = new FileWriter("log", true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  } public static SystemEventLog getInstance() {
    if (instance == null) {
      instance = new SystemEventLog();
    }
    return instance;
  }
  public synchronized void logEvent(String event) {
    try {
      logFile.write(event + "\n");
      logFile.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void closeLogFile() {
    try {
      logFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
public class Main {
  private static byte  [] a ={0};

  public static void main(String[] args) {

    Memorycard memorycard =new Memorycard(0,0,a);
    Communicationcard card =new Communicationcard();
    Thread thread = new Thread();
    Ram ram =new Ram(memorycard);
    Ethernet ethernet = new Ethernet();
    Tokenring tokenring = new Tokenring();
    IAdapter RamAdapter =new RamAdapter(ram);
    IAdapter EthernetAdapter = new EthernetAdapter(ethernet);
    IAdapter TokenringAdapter = new TokenringAdapeter(tokenring);

    ITask writeToMemoryTask = TaskFactory.createTask("WriteToMemory", RamAdapter, memorycard, null);
    ITask readFromMemoryTask = TaskFactory.createTask("ReadFromMemory", RamAdapter, memorycard, null);
    ITask readFromCardTask = TaskFactory.createTask("ReadFromCard", EthernetAdapter, null, card);
    ITask writeToCardTask = TaskFactory.createTask("WriteToCard", EthernetAdapter, null, card);

    thread.createTask(writeToMemoryTask);
    thread.createTask(readFromMemoryTask);
    thread.createTask(readFromCardTask);
    thread.createTask(writeToCardTask);

    thread.executeTasks();

  }
}

