package app_health_checker;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class LogWriter {
  public static void log(String log) {
    String message = new Date().toString() + " - " + log + "\n";
    try {
      File file = new File("appchecker.log.txt");
      FileWriter fr = new FileWriter(file, true);
      fr.write(message);
      fr.close();
    } catch (Exception e) {
    }
    System.out.print(message);
  }

  public static void error(String message) {
    log("ERROR - " + message);
  }
}
