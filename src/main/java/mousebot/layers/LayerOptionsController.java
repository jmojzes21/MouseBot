package mousebot.layers;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mousebot.main.Controller;
import mousebot.main.Dialogs;

/**
 * Upravljač koji koristi prozor za postavke sloja.
 *
 * @author josip
 *
 */
public class LayerOptionsController {

  public TextField name;
  public ColorPicker color;

  private Stage stage;
  private Layer layer;

  public LayerOptionsController(Stage stage) {
    this.stage = stage;
    this.layer = Controller.getProject().currentLayer;
  }

  public void initialize() {
    name.setText(layer.name.get());
    color.setValue(layer.color.getFXColor());
  }

  /**
   * Zatvara prozor, ali i primjenjuje postavke.
   */
  public void save() {

    String name = this.name.getText().trim();
    if (name.length() == 0) {
      Dialogs.show(AlertType.WARNING, "dialogs.layer.invalidname", ButtonType.OK);
      return;
    }

    layer.name.set(name);
    layer.color.setColor(color.getValue());
    stage.close();

    Controller.getProject().render();
  }

  /**
   * Zatvara prozor.
   */
  public void cancel() {
    stage.close();
  }

}