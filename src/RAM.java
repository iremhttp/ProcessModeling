import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

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
