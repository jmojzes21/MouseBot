package mousebot.tools;

import javafx.scene.input.MouseButton;
import mousebot.commands.ShapeAdd;
import mousebot.commands.ShapeCR;
import mousebot.commands.ShapeCancel;
import mousebot.components.Shape;
import mousebot.main.Controller;
import mousebot.parameters.Param;

public abstract class TShape<S extends Shape> extends DrawingTool<S> {

  private Param[] gparams;

  public TShape(String shape, Param... gparams) {
    super("tl.shapes." + shape, shape);
    this.gparams = gparams;
  }

  public abstract S createInstance(double x, double y); // kreiranje nove instance komponente

  public void mousePressed(double x, double y, MouseButton button) {

    if (button == MouseButton.PRIMARY) {

      if (c == null) {
        c = createInstance(x, y);
        Controller.getInvoker().execute(new ShapeAdd(c));
      } else if (Controller.getProject().cpManager.cr == null) {
        Controller.getInvoker().execute(new ShapeCR(c));
      }

    } else if (button == MouseButton.SECONDARY) {

      if (c != null) {
        Controller.getInvoker().execute(new ShapeCancel(c));
      }

    }

  }

  public void mouseReleased(double x, double y, MouseButton button) {}

  public void mouseMoved(double x, double y) {
    if (c != null && Controller.getProject().cpManager.cr == null) {
      c.toolSetEndPoint(x, y);
      Controller.getProject().render();
    }
  }

  public void mouseDragged(double x, double y, MouseButton button) {}

  public void cancel() {

    if (c != null) {
      Controller.getInvoker().execute(new ShapeCancel(c));
    }

  }

  public Param[] createParams() {
    return gparams;
  }

  public void paramChanged(Param p, Object v) {
    if (c != null) {
      c.setParamValue(p, v);
    }
  }

}