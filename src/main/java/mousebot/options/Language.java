package mousebot.options;

import mousebot.main.LanguageManager;

public enum Language {

  ENGLISH("en"), CROATIAN("hr");

  String id, name;

  Language(String id) {
    this.id = id;
  }

  public String getID() {
    return id;
  }

  public String toString() {
    return name;
  }

  public static Language value(String str) {
    if (str.equals("hr")) {
      return CROATIAN;
    }
    return ENGLISH;
  }

  public static void setNames() {
    ENGLISH.name = LanguageManager.getString("language.english");
    CROATIAN.name = LanguageManager.getString("language.croatian");
  }

}