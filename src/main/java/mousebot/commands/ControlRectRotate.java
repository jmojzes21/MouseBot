package mousebot.commands;

import mousebot.components.ControlRect;

public class ControlRectRotate implements Command {

  private ControlRect cr;
  private double angle;

  public ControlRectRotate(ControlRect cr, double angle) {
    this.cr = cr;
    this.angle = angle;
  }

  public void execute() {}

  public void undo() {
    cr.rotated(-angle, true);
  }

  public void redo() {
    cr.rotated(angle, true);
  }

}