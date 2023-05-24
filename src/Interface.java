interface ITask {
  void execute();
}

interface ICPU {
  void addTasks(ITask task);

  void runCPU();

  void removeTask(ITask task);
}

interface IRAM {
  byte[] get(int address, int size);

  int set(byte[] data, int address);
}

interface IReadFromMemoryTask extends ITask {
  byte[] getMem(int addr, int size);
}

interface IWriteToMemoryTask extends ITask {
  void setMem(byte[] data, int addr);
}

interface IReadFromCardTask extends ITask {
  byte[] getCom(int size);
}

interface IWriteToCardTask extends ITask {
  int setCom(byte[] data);
}

interface IAdapter {
  byte[] read(int addr, int size);

  int write(byte[] data, int addr);
}

interface IEthernet {
  Byte[] read(Integer size);

  Integer write(Byte[] data);
}

interface ITokenring {
  int[] receive(int size);

  int send(int[] data, int size);
}