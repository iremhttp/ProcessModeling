package baran;

import java.util.ArrayList;

class Task {
  private ArrayList tasks[];

  public String getTask() {
    return "Hello World";
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