import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
