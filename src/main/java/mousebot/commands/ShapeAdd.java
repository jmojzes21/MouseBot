package mousebot.commands;

import mousebot.components.Point;
import mousebot.components.Shape;
import mousebot.main.Controller;

public class ShapeAdd implements Command {

  private Shape s;

  public ShapeAdd(Shape s) {
    this.s = s;
  }

  public void execute() {
    Controller.getProject().currentLayer.components.add(s);
  }

  public void undo() {
    Controller.getProject().currentLayer.components.remove(s);
    Controller.getProject().tool.get().setComponent(null);
  }

  public void redo() {
    Controller.getProject().currentLayer.components.add(s);
    Controller.getProject().tool.get().setComponent(s);
    Point m = Controller.getProject().getMouseLoc();
    s.setEndPoint(m.x, m.y);
  }

}