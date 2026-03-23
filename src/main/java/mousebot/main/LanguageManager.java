package mousebot.main;

import java.util.Locale;
import java.util.ResourceBundle;
import mousebot.options.Language;

/**
 * Upravitelj stringova za lokalizaciju.
 *
 * @author josip
 */
public class LanguageManager {

  public static final ResourceBundle resourceBundle;

  static {
    String lang = RegManager.getLanguage().getID();
    resourceBundle = ResourceBundle.getBundle("language.app", new Locale(lang, lang));
    Language.setNames();
  }

  /**
   * Vraća lokaliziran string na određen jezik.
   *
   * @param key ključ (id) stringa
   * @return lokaliziran string, ključ ako nije moguće pronaći string u jezičnoj datoteci
   */
  public static String getString(String key) {
    try {
      return resourceBundle.getString(key);
    } catch (Exception e) {
      return key;
    }
  }

}