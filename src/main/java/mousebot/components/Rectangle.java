package mousebot.components;

import java.awt.geom.Rectangle2D;
import mousebot.executing.Executor;
import mousebot.executing.MouseBot;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;

/**
 * Komponenta koja se odnosi na pravokutnik.
 *
 * @author josip
 */
public class Rectangle extends Shape {

  private static final long serialVersionUID = 7546058799911429692L;

  public Rectangle() {
    super(4);
  }

  public Rectangle(double x, double y) {
    this();
    cp[0] = new Point(x, y);
    for (int i = 1; i < cp.length; i++) {
      cp[i] = cp[0].clone();
    }
  }

  public void render(Graphics g) {

    g.strokeLine(cp[0], cp[1]);
    g.strokeLine(cp[1], cp[2]);
    g.strokeLine(cp[2], cp[3]);
    g.strokeLine(cp[3], cp[0]);

  }

  public void processRect() {
    for (int i = 0; i < cp.length; i++) {
      RectFinder.getInstance().process(cp);
    }
  }

  public void setEndPoint(double x, double y) {
    cp[1].x = x;
    cp[2].x = x;
    cp[2].y = y;
    cp[3].y = y;
  }

  public boolean intersects(Rectangle2D rect) {

    for (int i = 0; i < cp.length; i++) {
      Point cp2 = cp[i == 3 ? 0 : i + 1];
      Point cp1 = cp[i];
      if (rect.intersectsLine(cp1.x, cp1.y, cp2.x, cp2.y)) {
        return true;
      }
    }

    return false;
  }

  public void execute() throws Exception {
    MouseBot.move(cp[0]);
    for (int i = 1; i < cp.length; i++) {
      Executor.check();
      MouseBot.press();
      MouseBot.move(cp[i]);
      MouseBot.release();
    }
    MouseBot.press();
    MouseBot.move(cp[0]);
    MouseBot.release();
  }

}