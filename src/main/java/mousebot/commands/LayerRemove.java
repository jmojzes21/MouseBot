package mousebot.commands;

import mousebot.layers.Layer;
import mousebot.main.Controller;

public class LayerRemove implements Command {

  private Layer layer;

  public void execute() {
    layer = Controller.getProject().currentLayer;
    Controller.getLayersManager().listener.disable();
    Controller.getLayersManager().remove(Controller.getProject().currentLayer);
    Controller.getLayersManager().listener.enable();
    Controller.getProject().currentLayer = Controller.getLayersManager().listView.getSelectionModel().getSelectedItem();
    Controller.getProject().render();
  }

  public void undo() {
    Controller.getLayersManager().addLayer(layer);
    Controller.getProject().currentLayer = layer;
    Controller.getLayersManager().listener.disable();
    Controller.getLayersManager().listView.getSelectionModel().select(layer);
    Controller.getLayersManager().listener.enable();
    Controller.getProject().render();
  }

  public void redo() {
    execute();
  }

}