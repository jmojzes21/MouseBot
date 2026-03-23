package mousebot.commands;

import mousebot.components.Bezier;
import mousebot.main.Controller;

public class BezierAdd implements Command {

  private Bezier b;

  public BezierAdd(Bezier b) {
    this.b = b;
  }

  public void execute() {
    Controller.getProject().currentLayer.components.add(b);
  }

  public void undo() {
    Controller.getProject().currentLayer.components.remove(b);
    Controller.getProject().tool.get().setComponent(null);
    Controller.getProject().cpManager.removeControlPoints();
  }

  public void redo() {
    Controller.getProject().currentLayer.components.add(b);
    Controller.getProject().tool.get().setComponent(b);
    b.createControlPoints(true);
  }

}