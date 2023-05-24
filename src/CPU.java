class CPU implements ICPU {
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
