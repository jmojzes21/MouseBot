package mousebot.components;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import mousebot.commands.ControlPointMove;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.tools.ChangeCursor;

/**
 * Služi kao upravljač određene točke.
 *
 * @author josip
 */
public class ControlPoint {

  // na prikaz kontrolne točke
  protected static final double r = 6;
  protected static final Color color = Color.MAGENTA;

  public Point p; // točka koju se pomiče
  public Point prevValue; // prethodna vrijednost

  private boolean dragging = false; // premica li se

  /**
   *
   * @param p točka
   */
  public ControlPoint(Point p) {
    this.p = p;
  }

  public void render(Graphics g) {
    g.gc.setStroke(color);
    g.gc.strokeOval(p.x * g.scale() - r + g.translation.x, p.y * g.scale() - r + g.translation.y, 2d * r, 2d * r);
  }

  public void mousePressed(double x, double y, MouseButton button) throws ChangeCursor {
    if (button == MouseButton.PRIMARY && inside(x, y)) {
      prevValue = p.clone();
      dragging = true;
      throw new ChangeCursor();
    }
  }

  public void mouseReleased(double x, double y, MouseButton button) {
    if (dragging && button == MouseButton.PRIMARY) {
      p.set(x, y);
      Controller.getInvoker().execute(new ControlPointMove(this));
      dragging = false;
    }
  }

  public void mouseMoved(double x, double y) throws ChangeCursor {
    if (inside(x, y)) {
      throw new ChangeCursor();
    }
  }

  public void mouseDragged(double x, double y, MouseButton button) throws ChangeCursor {
    if (button == MouseButton.PRIMARY && dragging) {
      p.set(x, y);
      Controller.getProject().render();
      throw new ChangeCursor();
    }
  }

  /**
   *
   * @param x
   * @param y
   * @return true ako se pokazivač miša unutar dometa, inače false
   */
  private boolean inside(double x, double y) {
    Graphics g = Controller.getGraphics();
    return Point.length(p, new Point(x, y)) <= r * g.scaleI();
  }

}