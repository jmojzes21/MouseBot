package mousebot.main;

import java.io.File;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Za jednostavnije prikazivanje informacija i upita korisniku.
 *
 * @author josip
 *
 */
public class Dialogs {

  public static final ButtonType SAVE = new ButtonType(LanguageManager.getString("options.save"), ButtonData.YES);
  public static final ButtonType CANCEL = new ButtonType(LanguageManager.getString("options.cancel"),
      ButtonData.CANCEL_CLOSE);
  public static final ButtonType DONT_SAVE = new ButtonType(LanguageManager.getString("dialogs.btn.dontsave"),
      ButtonData.NO);

  private static Alert alert = new Alert(null);
  private static FileChooser mbfc = new FileChooser();
  private static Stage stage;

  static {
    stage = (Stage) Controller.getInstance().canvas.getScene().getWindow();
    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MouseBot.APP_ICON);
    alert.getDialogPane().getStylesheets().add("/Goliath-Envy.css");
    alert.setTitle(MouseBot.APP_TITLE);
    mbfc.getExtensionFilters().add(new ExtensionFilter("MouseBot (*.mousebot)", "*.mousebot"));
  }

  /**
   * Prozor za prikaz informacije ili upita korisniku.
   *
   * @param at      tip
   * @param text    sadržaj
   * @param buttons opcije
   * @return opciju koju je korisnik odabrao, null ako je zatvorio prozor
   */
  public static ButtonType show(AlertType at, String text, ButtonType... buttons) {
    alert.setAlertType(at);
    alert.getButtonTypes().clear();
    alert.getButtonTypes().addAll(buttons);
    alert.setHeaderText(null);
    alert.setContentText(LanguageManager.getString(text));
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() ? result.get() : null;
  }

  /**
   * Upit za odabir datoteke u koju treba spremiti projekt.
   *
   * @return datoteka, null ako datoteka nije odabrana
   */
  public static File saveFile() {
    String dir = RegManager.getDir();
    mbfc.setInitialDirectory(new File(dir));
    File f = mbfc.showSaveDialog(stage);
    if (f != null) {
      RegManager.setDir(f.getParent());
    }
    return f;
  }

  /**
   * Upit za odabir daoteke iz koje treba učitati projekt.
   *
   * @return datoteka, null ako datoteka nije odabrana
   */
  public static File openFile() {
    String dir = RegManager.getDir();
    mbfc.setInitialDirectory(new File(dir));
    File f = mbfc.showOpenDialog(stage);
    if (f != null) {
      RegManager.setDir(f.getParent());
    }
    return f;
  }

}