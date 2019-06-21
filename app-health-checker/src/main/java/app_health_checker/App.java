package app_health_checker;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
  public static void main(String[] args) throws Exception {

    if (args != null && args.length != 1) {
      System.out.println("please give the config file like : ");
      System.out.println("pattern.0=app.js");
      System.out.println("command.0=/usr/local/bin/node");
      System.out.println("args.0=app.js");
      System.out.println("workdir.0=/<path_to_app>/");
      return;
    }

    Config config = new ConfigParser(load(args[0])).parse();
    MailService mailservice = createMailService("app.properties");
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleWithFixedDelay(new Checker(config, mailservice), 0, 10, TimeUnit.MINUTES);

  }

  private static MailService createMailService(String property) {
    try {
      Properties properties = load(property);

      MailService mailservice = new MailService(properties.getProperty("account"), properties.getProperty("password"), properties.getProperty("host"), properties.getProperty("port"), properties.getProperty("receivers"));
      return mailservice;
    } catch (Exception e) {
      LogWriter.log(property + " file not found. Email alerts disabled.");
    }
    return null;
  }

  private static Properties load(String confPath) throws Exception {
    File confFile = new File(confPath);
    if (!confFile.exists()) {
      throw new Exception(confPath + " is not found");
    }
    try (FileInputStream stream = new FileInputStream(confFile)) {
      Properties properties = new Properties();
      properties.load(stream);
      return properties;
    } catch (Exception e) {
      LogWriter.error(e.getMessage());
      throw e;
    }
  }

}
