package mousebot.commands;

import mousebot.components.ControlRect;
import mousebot.components.Point;

public class ControlRectPos implements Command {

  private ControlRect cr;
  private Point oldstart, oldend, newstart, newend;

  public ControlRectPos(ControlRect cr) {
    this.cr = cr;
    this.oldstart = cr.tempStart.clone();
    this.oldend = cr.tempEnd.clone();
    this.newstart = new Point(cr.startx.get(), cr.starty.get());
    this.newend = new Point(cr.endx.get(), cr.endy.get());
  }

  public void execute() {}

  public void undo() {

    cr.startx.set(oldstart.x);
    cr.starty.set(oldstart.y);
    cr.endx.set(oldend.x);
    cr.endy.set(oldend.y);

    cr.tempStart = newstart.clone();
    cr.tempEnd = newend.clone();

    cr.scaled(true);

  }

  public void redo() {

    cr.startx.set(newstart.x);
    cr.starty.set(newstart.y);
    cr.endx.set(newend.x);
    cr.endy.set(newend.y);

    cr.tempStart = oldstart.clone();
    cr.tempEnd = oldend.clone();

    cr.scaled(true);

  }

}