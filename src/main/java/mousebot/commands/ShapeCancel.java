package mousebot.commands;

import mousebot.components.ControlRect;
import mousebot.components.Point;
import mousebot.components.Shape;
import mousebot.main.Controller;

public class ShapeCancel implements Command {

  private Shape s;
  private ControlRect cr;

  public ShapeCancel(Shape s) {
    this.s = s;
  }

  public void execute() {
    cr = Controller.getProject().cpManager.cr;
    Controller.getProject().cpManager.cr = null;
    Controller.getProject().tool.get().setComponent(null);
  }

  public void undo() {
    if (cr == null) {
      Point m = Controller.getProject().getMouseLoc();
      s.setEndPoint(m.x, m.y);
    }
    Controller.getProject().cpManager.cr = cr;
    Controller.getProject().tool.get().setComponent(s);
  }

  public void redo() {
    execute();
  }

}