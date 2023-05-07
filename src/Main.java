import java.util.*;

// Interfaces
interface Memory {
    byte[] read(int address, int size);
    void write(byte[] data, int address, int size);
}

interface NetworkCard {
    byte[] read(int size);
    int write(byte[] data);
}

class RAM implements Memory {
    private static RAM instance = null;
    private RAM(){}
    public static RAM getInstance(){
        if(instance == null)
            instance = new RAM();
        return instance;
    }
    public byte[] read(int address, int size){
        // Implementation
        return new byte[0];
    }
    public void write(byte[] data, int address, int size){
        // Implementation
    }
}

class Ethernet implements NetworkCard {
    private static Ethernet instance = null;
    private Ethernet(){}
    public static Ethernet getInstance(){
        if(instance == null)
            instance = new Ethernet();
        return instance;
    }
    public byte[] read(int size){
        // Implementation
        return new byte[0];
    }
    public int write(byte[] data){
        // Implementation
        return 0;
    }
}

class TokenRing implements NetworkCard {
    private static TokenRing instance = null;
    private TokenRing(){}
    public static TokenRing getInstance(){
        if(instance == null)
            instance = new TokenRing();
        return instance;
    }
    public byte[] read(int size){
        // Implementation
        return new byte[0];
    }
    public int write(byte[] data){
        // Implementation
        return 0;
    }
}
class Task {
    private TaskType type;
    private byte[] data;
    private int address;
    private int size;

    public Task(TaskType type, int address, int size) {
        this.type = type;
        this.address = address;
        this.size = size;
    }

    public Task(TaskType type, byte[] data, int address, int size) {
        this.type = type;
        this.data = data;
        this.address = address;
        this.size = size;
    }

    public TaskType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public int getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }
}

enum TaskType {
    READ_MEMORY,
    WRITE_MEMORY,
    READ_ETHERNET,
    WRITE_TOKEN_RING
}

class CPU {
    private Memory memory;
    private NetworkCard ethernetCard;
    private NetworkCard tokenRingCard;
    private List<Task> taskQueue;

    public CPU(Memory memory, NetworkCard ethernetCard, NetworkCard tokenRingCard) {
        this.memory = memory;
        this.ethernetCard = ethernetCard;
        this.tokenRingCard = tokenRingCard;
        this.taskQueue = new ArrayList<>();
    }

    public void run() {
        while (true) {
            if (!taskQueue.isEmpty()) {
                Task task = taskQueue.remove(0);
                switch (task.getType()) {
                    case READ_MEMORY:
                        // read from memory
                        byte[] data = memory.read(task.getAddress(), task.getSize());
                        // send notification
                        System.out.println("Task finished: READ_MEMORY");
                        break;
                    case WRITE_MEMORY:
                        // write to memory
                        memory.write(task.getData(), task.getAddress(), task.getSize());
                        // send notification
                        System.out.println("Task finished: WRITE_MEMORY");
                        break;
                    case READ_ETHERNET:
                        // read from ethernet card
                        data = ethernetCard.read(task.getSize());
                        // write to memory
                        memory.write(data, task.getAddress(), data.length);
                        // write to token-ring card
                        tokenRingCard.write(data);
                        // send notification
                        System.out.println("Task finished: READ_ETHERNET");
                        break;
                    case WRITE_TOKEN_RING:
                        // write to token-ring card
                        int bytesWritten = tokenRingCard.write(task.getData());
                        // send notification
                        System.out.println("Task finished: WRITE_TOKEN_RING");
                        break;
                }
            }
        }
    }

    public void addTask(Task task) {
        taskQueue.add(task);
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public void setEthernetCard(NetworkCard ethernetCard) {
        this.ethernetCard = ethernetCard;
    }

    public void setTokenRingCard(NetworkCard tokenRingCard) {
        this.tokenRingCard = tokenRingCard;
    }
}

class CPUFactory {
    public CPU createCPU(Memory memory, NetworkCard ethernetCard, NetworkCard tokenRingCard) {
        CPU cpu = new CPU(memory, ethernetCard, tokenRingCard);
        // start the CPU in a new thread
        new Thread(cpu::run).start();
        return cpu;
    }
}
class Main {
    public static void main(String[] args) {
        // create instances of RAM, Ethernet, and TokenRing
        RAM ram = RAM.getInstance();
        Ethernet ethernet = Ethernet.getInstance();
        TokenRing tokenRing = TokenRing.getInstance();

        // create CPU instance using CPUFactory
        CPUFactory cpuFactory = new CPUFactory();
        CPU cpu = cpuFactory.createCPU(ram, ethernet, tokenRing);

        // add tasks to the CPU's task queue
        Task readMemTask = new Task(TaskType.READ_MEMORY, 0, 1024);
        Task writeMemTask = new Task(TaskType.WRITE_MEMORY, new byte[]{0, 1, 2, 3}, 0, 4);
        Task readEthernetTask = new Task(TaskType.READ_ETHERNET, 0, 1024);
        Task writeTokenRingTask = new Task(TaskType.WRITE_TOKEN_RING, new byte[]{4, 5, 6, 7}, 0, 4);
        cpu.addTask(readMemTask);
        cpu.addTask(writeMemTask);
        cpu.addTask(readEthernetTask);
        cpu.addTask(writeTokenRingTask);

        // run the CPU to execute the tasks
        cpu.run();
    }
}