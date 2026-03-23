package mousebot.layers;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * Ćelija u komponenti ListView se treba prilagoditi tako da uz ime sloja prikazuje i komponentu za o(ne)mogućavanje
 * sloja.
 *
 * @author josip
 *
 */
public class LayerListCell extends ListCell<Layer> {

  public void updateItem(Layer item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null) {
      setGraphic(null);
    } else {

      HBox hbox;
      Label text;
      Pane space;
      CheckBox enabled;

      hbox = new HBox();
      text = new Label();
      space = new Pane();
      enabled = new CheckBox();
      enabled.setSelected(true);

      hbox.getChildren().addAll(text, space, enabled);
      HBox.setHgrow(space, Priority.ALWAYS);

      enabled.setFocusTraversable(false);

      setGraphic(hbox);
      text.textProperty().bind(item.name);
      enabled.selectedProperty().bindBidirectional(item.enabled);
    }

  }

}