package mousebot.commands;

import mousebot.components.Bezier;
import mousebot.main.Controller;

public class BezierFinish implements Command {

  private Bezier b;

  public BezierFinish(Bezier b) {
    this.b = b;
  }

  public void execute() {
    if (!b.initedCP1) {
      b.initControlPoints();
    }
    Controller.getProject().tool.get().setComponent(null);
    Controller.getProject().cpManager.removeControlPoints();
  }

  public void undo() {
    Controller.getProject().tool.get().setComponent(b);
    b.createControlPoints(true);
  }

  public void redo() {
    Controller.getProject().tool.get().setComponent(null);
    Controller.getProject().cpManager.removeControlPoints();
  }

}