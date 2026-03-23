package mousebot.layers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import mousebot.commands.LayerSet;
import mousebot.main.Controller;
import mousebot.main.FixedChangeListener;

/**
 * Upravitelj slojeva.
 *
 * @author josip
 */
public class LayersManager {

  public ListView<Layer> listView; // komponenta za prikaz slojeva
  public ObservableList<Layer> layers = FXCollections.observableArrayList(); // lista svih slojeva
  public FixedChangeListener<Layer> listener; // bilježi promjene trenutnog sloja

  public LayersManager(ListView<Layer> lv) {

    listView = lv;

    listView.setCellFactory(new Callback<ListView<Layer>, ListCell<Layer>>() {
      public ListCell<Layer> call(ListView<Layer> param) {
        return new LayerListCell();
      }
    });

    listView.setItems(layers);
    listener = new FixedChangeListener<>() {
      public Layer validate(Layer current, Layer newValue) {
        return newValue;
      }

      public void changed(Layer oldValue, Layer newValue) {
        Controller.getProject().tool.get().cancel();
        Controller.getInvoker().execute(new LayerSet(oldValue, newValue));
        Controller.getProject().render();
      }

      public void setValue(Layer value) {
        listView.getSelectionModel().select(value);
      }
    };
    listView.getSelectionModel().selectedItemProperty().addListener(listener);

    reset(null, 0);
  }

  public void addLayer(Layer layer) {
    layers.add(layer);
  }

  public void remove(Layer layer) {
    layers.remove(layer);
  }

  /**
   * Obriše sve slojeve. Ako je input = null, dodaje samo početni sloj. Inače, dodaje sve slojeve proslijeđene
   * parametrom te označava trenutni sloj.
   *
   * @param input - slojevi s komponentama, može biti null
   * @param c     - index trenutnog sloja
   */
  public void reset(Layer[] input, int c) {
    listener.disable();

    layers.clear();

    if (input == null) {
      Layer defaultLayer = new Layer(1);
      layers.add(defaultLayer);
      Controller.getProject().currentLayer = defaultLayer;
      listView.getSelectionModel().select(0);
    } else {
      layers.addAll(input);
      Controller.getProject().currentLayer = input[c];
      listView.getSelectionModel().select(c);
    }

    listener.enable();
  }

}