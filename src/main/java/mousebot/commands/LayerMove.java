package mousebot.commands;

import java.util.Collections;
import mousebot.main.Controller;

public class LayerMove implements Command {

  private int i, j;

  public LayerMove(int i, int j) {
    this.i = i;
    this.j = j;
  }

  public void execute() {
    Controller.getLayersManager().listener.disable();
    Collections.swap(Controller.getLayersManager().layers, i, j);
    Controller.getLayersManager().listView.getSelectionModel().select(j);
    Controller.getProject().render();
    Controller.getLayersManager().listener.enable();
  }

  public void undo() {
    Controller.getLayersManager().listener.disable();
    Collections.swap(Controller.getLayersManager().layers, i, j);
    Controller.getLayersManager().listView.getSelectionModel().select(i);
    Controller.getProject().render();
    Controller.getLayersManager().listener.enable();
  }

  public void redo() {
    execute();
  }

}