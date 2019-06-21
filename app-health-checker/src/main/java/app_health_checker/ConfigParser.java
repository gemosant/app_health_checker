package app_health_checker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigParser {
  private Properties properties;

  public ConfigParser(Properties properties) {
    this.properties = properties;
  }

  public Config parse() {
    Map<String, ConfigItem> confMap = new HashMap<>();
    properties.entrySet().stream().forEach(entry -> convert(confMap, entry));
    List<ConfigItem> items = confMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    return new Config(items);
  }

  private void convert(Map<String, ConfigItem> confMap, Entry<Object, Object> entry) {
    String k = entry.getKey().toString();
    String v = entry.getValue().toString();
    String[] keys = k.toString().split("\\.");

    if (keys.length != 2) {
      throw new Error(k.toString() + " unknown parameter");
    }
    String type = keys[0];
    String id = keys[1];

    ConfigItem item;
    if (confMap.containsKey(id)) {
      item = confMap.get(id);
    } else {
      item = new ConfigItem();
      item.setId(id);
      confMap.put(id, item);
    }

    if ("pattern".equalsIgnoreCase(type)) {
      item.setPattern(v.toString());
    } else if ("command".equalsIgnoreCase(type)) {
      item.setCommand(v.toString());
    } else if ("args".equalsIgnoreCase(type)) {
      item.setArgs(v.toString());
    } else if ("workdir".equalsIgnoreCase(type)) {
      item.setWorkDir(v.toString());
    } else {
      throw new Error(k.toString() + " unknown parameter");
    }

  }

}
