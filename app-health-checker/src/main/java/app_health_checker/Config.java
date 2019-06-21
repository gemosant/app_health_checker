package app_health_checker;

import java.util.List;

public class Config {
  private List<ConfigItem> items;

  public Config(List<ConfigItem> items) {
    this.items = items;
  }

  public List<ConfigItem> getItems() {
    return items;
  }
}
