package mousebot.commands;

import mousebot.components.ControlRect;
import mousebot.components.ControlRectController;
import mousebot.components.Point;
import mousebot.components.Shape;
import mousebot.main.Controller;
import mousebot.main.RectFinder;

public class ShapeCR implements Command {

  private Shape s;
  private ControlRect cr;
  private Point tempEndPoint;

  public ShapeCR(Shape s) {
    this.s = s;
  }

  public void execute() {

    if (tempEndPoint != null) {
      s.setEndPoint(tempEndPoint.x, tempEndPoint.y);
    }
    if (cr == null) {
      RectFinder.getInstance().reset();
      s.processRect();
      cr = new ControlRect(ControlRectController.TCOMPONENT);
    }

    Controller.getProject().cpManager.cr = cr;

  }


  public void undo() {
    cr = Controller.getProject().cpManager.cr;
    Controller.getProject().cpManager.cr = null;
    if (tempEndPoint == null) {
      tempEndPoint = s.lastEndPoint.clone();
    }
  }

  public void redo() {
    execute();
  }

}