package mousebot.tools;

import javafx.scene.input.MouseButton;
import mousebot.commands.BezierAdd;
import mousebot.commands.BezierFinish;
import mousebot.components.Bezier;
import mousebot.main.Controller;
import mousebot.parameters.Param;

/*
 * Za crtanje Bezierove krivulje.
 */
public class TBezier extends DrawingTool<Bezier> {

  private static final Param[] gparams = {Param.BEZIER_TYPE, Param.ACCURACY_BEZIER};

  public TBezier() {
    super("tl.basic.bezier", "bezier");
  }

  public void mousePressed(double x, double y, MouseButton button) {

    if (button == MouseButton.PRIMARY) {
      if (c == null) {
        c = new Bezier(x, y);
        Controller.getInvoker().execute(new BezierAdd(c));
      } else if (!((Bezier) c).initedCP1) {
        c.cp[1].set(x, y);
        c.initControlPoints();
        c.createControlPoints(true);
      }
    } else if (button == MouseButton.SECONDARY) {
      if (c != null) {
        Controller.getInvoker().execute(new BezierFinish(c));
      }
    }

  }

  public void mouseReleased(double x, double y, MouseButton button) {}

  public void mouseMoved(double x, double y) {

    if (c != null && !((Bezier) c).initedCP1) {
      c.cp[1].set(x, y);
      Controller.getProject().render();
    }

  }

  public void mouseDragged(double x, double y, MouseButton button) {
    mouseMoved(x, y);
  }

  public void cancel() {
    if (c != null) {
      Controller.getInvoker().execute(new BezierFinish(c));
    }
  }

  public Param[] createParams() {
    return gparams;
  }

  public void paramChanged(Param p, Object v) {
    if (c != null) {
      c.setParamValue(p, v);
      if (p == Param.BEZIER_TYPE) {
        c.createControlPoints(true);
      }
    }
  }

}