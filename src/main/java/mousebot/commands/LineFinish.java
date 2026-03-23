package mousebot.commands;

import mousebot.components.Line;
import mousebot.components.Point;
import mousebot.main.Controller;

public class LineFinish implements Command {

  private Line line;
  private Point c2;

  public LineFinish(Line line) {
    this.line = line;
    c2 = line.getEnd();
  }

  public void execute() {
    line.unfreeze();
  }

  public void undo() {
    Controller.getProject().tool.get().setComponent(line);
    line.setEnd(Controller.getProject().getMouseLoc());
  }

  public void redo() {
    line.setEnd(c2);
    Controller.getInvoker().redo();
  }

}