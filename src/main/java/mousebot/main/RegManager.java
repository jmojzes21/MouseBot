package mousebot.main;

import java.io.File;
import java.util.prefs.Preferences;
import mousebot.options.Language;

/**
 * Koristi se za spremanje konfiguracije aplikacije (vrijeme čekanja, jezik, direktorij iz koje je spremljen/otvoren
 * posljednji projekt).
 *
 * @author josip
 */
public class RegManager {

  private static final String KEY_DELAY = "delay";
  private static final String KEY_LANG = "language";
  private static final String KEY_DIR = "dir";

  private static final Preferences userPref = Preferences.userRoot().node("mousebot");

  public static void setDelay(int d) {
    mousebot.executing.MouseBot.delay = d;
    userPref.putInt(KEY_DELAY, d);
  }

  public static void setLanguage(Language lang) {
    userPref.put(KEY_LANG, lang.getID());
  }

  public static int getDelay() {
    int val = userPref.getInt(KEY_DELAY, 0);
    if (val < 1 || val > 20) {
      val = 1;
    }
    return val;
  }

  public static Language getLanguage() {
    String val = userPref.get(KEY_LANG, "null");
    return Language.value(val);
  }

  public static void setDir(String dir) {
    userPref.put(KEY_DIR, dir);
  }

  public static String getDir() {
    String dir = userPref.get(KEY_DIR, "null");
    if (!new File(dir).exists()) {
      dir = System.getProperty("user.home") + "/documents";
    }
    return dir;
  }

}