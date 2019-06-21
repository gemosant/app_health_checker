package app_health_checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Checker implements Runnable {
  private Config config;
  private MailService mailservice;
private Scanner sc;

  public Checker(Config config, MailService mailservice) {
    this.config = config;
    this.mailservice = mailservice;
  }

  @Override
  public void run() {
    try {
      String line;
      Process proc = Runtime.getRuntime().exec("ps aux");
      InputStream stream = proc.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      Set<ConfigItem> result = new HashSet<>();
      while ((line = reader.readLine()) != null) {
        for (ConfigItem item : config.getItems()) {
          if (line.contains(item.getPattern())) {
            item.setRunning(true);
          } else {
            result.add(item);
          }
        }

      }
      Iterator<ConfigItem> iterator = result.iterator();
      while (iterator.hasNext()) {
        ConfigItem item = iterator.next();
        if (item.isRunning()) {
          iterator.remove();
        }
      }

      result.stream().forEach(item -> {
        try {
          LogWriter.log("'" + item.getPattern() + "' is not running");
          ProcessBuilder builder = new ProcessBuilder(item.buildProcessLine());
          
          
          builder.directory(new File(item.getWorkDir()));
          
          Process process = builder.start();
          new Thread(() -> {
            sc = new Scanner(process.getErrorStream());
            StringBuilder sb = new StringBuilder(item.toString());
            sb.append("\n");
            while (sc.hasNextLine()) {
              sb.append(sc.nextLine()).append("\n");
            }
            LogWriter.error(sb.toString().trim());
            sendMail("Alert - " + item.getPattern() + " has startup errors", sb.toString().trim());

          }).start();
          LogWriter.log("'" + item.getPattern() + "' is started with '" + item.getCommand() + "'");
          sendMail("Info - " + item.getPattern() + " was started", "'" + item.getPattern() + "' is started with '" + item.getCommand() + "'");
        } catch (Exception e) {
          LogWriter.error(e.getMessage());
        }
      });

      config.getItems().forEach(i -> i.setRunning(false));
    } catch (Exception e) {
      LogWriter.error(e.getMessage());
    }

  }

  private void sendMail(String subject, String body) {
    if (mailservice != null) {
      mailservice.send(body, subject);
    }
  }

}
