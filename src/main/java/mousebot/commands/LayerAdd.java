package mousebot.commands;

import mousebot.layers.Layer;
import mousebot.main.Controller;

public class LayerAdd implements Command {

  private Layer layer = new Layer();

  public void execute() {
    Controller.getLayersManager().addLayer(layer);
  }

  public void undo() {
    Controller.getLayersManager().remove(layer);
  }

  public void redo() {
    Controller.getLayersManager().addLayer(layer);
  }

}