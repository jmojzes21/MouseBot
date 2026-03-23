package mousebot.components;

import javafx.beans.property.DoubleProperty;
import javafx.scene.input.MouseButton;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.tools.ChangeCursor;

/**
 * Isto kao i ControlPoint, ali za kontrolni pravokutnik.
 *
 * @author josip
 */
public class ControlRectCP {

  private static final double r = ControlPoint.r;

  public DoubleProperty x, y;
  public double prevX, prevY;
  private boolean dragging = false;

  public ControlRectCP(DoubleProperty x, DoubleProperty y) {
    this.x = x;
    this.y = y;
  }

  public void render(Graphics g) {
    g.gc.setStroke(ControlPoint.color);
    g.gc.strokeOval(x.get() * g.scale() - r + g.translation.x, y.get() * g.scale() - r + g.translation.y, 2d * r,
        2d * r);
  }

  public void mousePressed(double x, double y, MouseButton button) throws Exception {
    if (inside(x, y)) {
      prevX = this.x.get();
      prevY = this.y.get();
      dragging = true;
      throw new Exception();
    }
  }

  public void mouseReleased(double x, double y, MouseButton button) throws Exception {
    if (dragging) {
      set(x, y);
      dragging = false;
      throw new Exception();
    }
  }

  public void mouseMoved(double x, double y) throws ChangeCursor {
    if (inside(x, y)) {
      throw new ChangeCursor();
    }
  }

  public void mouseDragged(double x, double y, MouseButton button) throws Exception {
    if (dragging) {
      set(x, y);
      throw new Exception();
    }
  }

  private boolean inside(double x, double y) {
    Graphics g = Controller.getGraphics();
    return Point.length(new Point(this.x.get(), this.y.get()), new Point(x, y)) <= r * g.scaleI();
  }

  public void set(double x, double y) {
    this.x.set(x);
    this.y.set(y);
  }

}