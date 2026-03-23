package mousebot.tools;

import javafx.scene.input.MouseButton;
import mousebot.commands.LineAdd;
import mousebot.commands.LineCancel;
import mousebot.commands.LineFinish;
import mousebot.components.Line;
import mousebot.main.Controller;
import mousebot.parameters.Param;

/*
 * Za crtanje linije.
 */
public class TLine extends DrawingTool<Line> {

  public TLine() {
    super("tl.basic.line", "line");
  }

  public void mousePressed(double x, double y, MouseButton b) {

    if (b == MouseButton.PRIMARY) {

      if (c != null) {
        c.setEndPoint(x, y);
        Controller.getInvoker().execute(new LineFinish(c));
        c = new Line(c.getEnd().x, c.getEnd().y);
      } else {
        c = new Line(x, y);
      }

      Controller.getInvoker().execute(new LineAdd(c));

    } else if (b == MouseButton.SECONDARY) {
      if (c != null) {
        Controller.getInvoker().execute(new LineCancel(c));
        c = null;
      }
    }
  }

  public void mouseReleased(double x, double y, MouseButton b) {}

  public void mouseMoved(double x, double y) {
    if (c != null) {
      c.setEndPoint(x, y);
      Controller.getProject().render();
    }
  }

  public void mouseDragged(double x, double y, MouseButton b) {
    if (c != null) {
      c.setEndPoint(x, y);
      Controller.getProject().render();
    }
  }

  public void cancel() {
    if (c != null) {
      Controller.getInvoker().execute(new LineCancel(c));
      c = null;
    }
  }

  public Param[] createParams() {
    return new Param[]{Param.LENGTH, Param.ANGLE};
  }

  public void paramChanged(Param p, Object v) {

    if (c == null) {
      Controller.getParamsManager().setValue(p, "");
      return;
    }

    if (p == Param.ANGLE) {
      c.freezeAngle(-(double) v);
    } else if (p == Param.LENGTH) {
      c.freezeLength((double) v);
    }

  }

  public void initParamValues() {
    Controller.getParamsManager().setValue(Param.LENGTH, "");
    Controller.getParamsManager().setValue(Param.ANGLE, "");
  }

}