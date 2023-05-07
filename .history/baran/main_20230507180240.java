package baran;

interface IRam {
  byte[] get(int address, int size);

  int set(byte[] data, int address);
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

public class Main {
  public static void main(String[] args) {
  }
}