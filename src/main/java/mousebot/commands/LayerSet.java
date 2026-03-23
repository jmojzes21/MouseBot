package mousebot.commands;

import mousebot.layers.Layer;
import mousebot.main.Controller;

public class LayerSet implements Command {

  private Layer prevLayer, newLayer;

  public LayerSet(Layer prevLayer, Layer newLayer) {
    this.prevLayer = prevLayer;
    this.newLayer = newLayer;
  }

  public void execute() {
    Controller.getProject().currentLayer = newLayer;
  }

  public void undo() {
    Controller.getProject().currentLayer = prevLayer;
    Controller.getLayersManager().listener.disable();
    Controller.getLayersManager().listView.getSelectionModel().select(prevLayer);
    Controller.getLayersManager().listener.enable();
  }

  public void redo() {
    Controller.getProject().currentLayer = newLayer;
    Controller.getLayersManager().listener.disable();
    Controller.getLayersManager().listView.getSelectionModel().select(newLayer);
    Controller.getLayersManager().listener.enable();
  }

}