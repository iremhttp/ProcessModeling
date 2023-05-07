package baran;

interface IRam {
  byte[] get(int address, int size);

  int set(byte[] data, int address);
}

interface IMemory {
  byte[] getMem(int address, int size);

  void set(byte[] data, int address);
}

interface ICard {
  byte[] getCom(int address);

  int getCom(byte[] data);
}

class Ram implements IRam {

  @Override
  public byte[] get(int address, int size) {
    // TODO Auto-generated method stub
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