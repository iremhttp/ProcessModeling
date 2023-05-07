import java.util.*;

// Interfaces
interface Memory{
    byte[] read(int address, int size);
    void write(byte[] data, int address, int size);
}

interface NetworkCard{
    byte[] read(int addr, int size);
    int write(byte[] data, int addr);
}

class RAM implements Memory{
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

class Ethernet implements NetworkCard{
    private static Ethernet instance = null;
    private Ethernet(){}
    public static Ethernet getInstance(){
        if(instance == null)
            instance = new Ethernet();
        return instance;
    }
    @Override
    public byte[] read(int addr, int size) {
        return new byte[0];
    }

    @Override
    public int write(byte[] data, int addr) {
        return 0;
    }
}

class TokenRing implements NetworkCard{
    private static TokenRing instance;

    public static TokenRing getInstance(){
        if(instance == null)
            instance = new TokenRing();
        return instance;
    }
    public byte[] read(int addr, int size){

        return new byte[0];
    }

    @Override
    public int write(byte[] data, int addr) {
        return 0;
    }
}

class CPU{
    private Memory memory;
    private NetworkCard networkCard;
    public CPU(Memory memory, NetworkCard networkCard){
        this.memory = memory;
        this.networkCard = networkCard;
    }

    public void run(){
        /////
    }

    public void setMemory(Memory memory){
        this.memory = memory;
    }

    public void setNetworkCard(NetworkCard networkCard){
        this.networkCard = networkCard;
    }
}
class CPUFactory{
    public CPU createCPU(Memory memory, NetworkCard networkCard){
        return new CPU(memory, networkCard);
    }
}
class Main {
    public static void main(String[] args) {
        CPUFactory cpuFactory = new CPUFactory();
        Memory ram = RAM.getInstance();
        NetworkCard ethernet = Ethernet.getInstance();
        NetworkCard tokenRing = TokenRing.getInstance();
        CPU cpu1 = cpuFactory.createCPU(ram, ethernet);
        CPU cpu2 = cpuFactory.createCPU(ram, tokenRing);

        // Simulation code
        cpu1.run();
        cpu2.run();
    }
}