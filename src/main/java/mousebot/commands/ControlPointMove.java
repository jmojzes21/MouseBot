package mousebot.commands;

import mousebot.components.ControlPoint;
import mousebot.components.Point;

public class ControlPointMove implements Command {

  private ControlPoint cp;
  private Point newVal;

  public ControlPointMove(ControlPoint cp) {
    this.cp = cp;
    newVal = cp.p.clone();
  }

  public void execute() {}

  public void undo() {
    cp.p.set(cp.prevValue);
  }

  public void redo() {
    cp.p.set(newVal);
  }

}