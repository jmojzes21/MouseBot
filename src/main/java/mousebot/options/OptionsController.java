package mousebot.options;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mousebot.main.Controller;
import mousebot.main.Dialogs;
import mousebot.main.RegManager;

/**
 * Upravljač za prozor postavki.
 *
 * @author josip
 *
 */
public class OptionsController {

  private Stage stage;

  public Slider delay;
  public ChoiceBox<Language> language;
  public TextField gridSize;

  public OptionsController(Stage stage) {
    this.stage = stage;
  }

  public void initialize() {

    delay.setValue(RegManager.getDelay());

    ObservableList<Language> langs = FXCollections.observableArrayList();
    langs.add(Language.ENGLISH);
    langs.add(Language.CROATIAN);
    language.setItems(langs);
    language.getSelectionModel().select(RegManager.getLanguage());

    gridSize.setText(Integer.toString(Controller.getProject().snap.gridSize.intValue()));
  }

  /**
   * Sprema postavke i zatvara prozor.
   */
  public void save() {

    if (!checkGridSize()) {
      Dialogs.show(AlertType.WARNING, "dialogs.gridsize.invalid", ButtonType.OK);
      gridSize.setText(Integer.toString(Controller.getProject().snap.gridSize.intValue()));
      return;
    }

    RegManager.setDelay((int) delay.getValue());
    Language newLang = language.getSelectionModel().getSelectedItem();
    if (RegManager.getLanguage() != newLang) {
      RegManager.setLanguage(language.getSelectionModel().getSelectedItem());
      Dialogs.show(AlertType.INFORMATION, "dialogs.language.change", ButtonType.OK);
    }
    stage.close();
  }

  /**
   * Zatvara prozor.
   */
  public void cancel() {
    stage.close();
  }

  /**
   * Provjera vrijednost za veličinu rešetke.
   *
   * @return valjanost
   */
  private boolean checkGridSize() {
    try {
      Integer val = Integer.parseInt(gridSize.getText());
      if (val >= 10 && val <= 1000) {
        Controller.getProject().snap.gridSize.set(val);
        Controller.getProject().render();
        return true;
      }
    } catch (Exception exception) {}
    return false;
  }

}