package app_health_checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigItem {
  private boolean running = false;
  private String id;
  private String pattern;
  private String command;
  private String args;
  private String workdir;
  
  public String getPattern() {
    return pattern;
  }

  public List<String> buildProcessLine() {

    List<String> line = new ArrayList<>();
    line.add(command);
    line.addAll(getArgsAs());

    return line;
  }

  public List<String> getArgsAs() {
    return Arrays.asList(args.split(" "));
  }

  public void setArgs(String args) {
    this.args = args;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  @Override
  public String toString() {
    return "{id: " + id + ", pattern:" + pattern + ", command:" + command + ", args:" + args + "}";
  }

  public String getWorkDir() {
	 return workdir;
  }

  public void setWorkDir(String workdir) {
	this.workdir = workdir;
  }

}