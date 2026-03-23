package mousebot.components;

import java.awt.geom.Rectangle2D;
import mousebot.executing.Executor;
import mousebot.executing.MouseBot;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;
import mousebot.main.Snap;
import mousebot.parameters.Param;
import mousebot.tools.THandLine;

/**
 * Komponenta koja se odnosi na ručni krivulju.
 *
 * @author josip
 */
public class HandLine extends Component {

  private static final long serialVersionUID = -8200009763904539295L;

  public HandLine() {
    super(0, Param.DENSITY);
  }

  public void render(Graphics g) {

    int density = (int) getParamValue(Param.DENSITY);

    int i = 0;

    if (cp == null) {

      THandLine tl = (THandLine) Controller.getProject().tool.get();
      while (i < tl.points.size() - 1) {
        int i2 = density + i + 1;
        if (i2 >= tl.points.size()) {
          i2 = tl.points.size() - 1;
        }
        g.strokeLine(tl.points.get(i).x, tl.points.get(i).y, tl.points.get(i2).x, tl.points.get(i2).y);
        i += density + 1;
      }

      return;
    }

    while (i < cp.length - 1) {
      int i2 = density + i + 1;
      if (i2 >= cp.length) {
        i2 = cp.length - 1;
      }
      g.strokeLine(cp[i].x, cp[i].y, cp[i2].x, cp[i2].y);
      i += density + 1;
    }
  }

  public boolean intersects(Rectangle2D rect) {

    int density = (int) getParamValue(Param.DENSITY);

    int i = 0;
    while (i < cp.length - 1) {
      int i2 = density + i + 1;
      if (i2 >= cp.length) {
        i2 = cp.length - 1;
      }
      if (rect.intersectsLine(cp[i].x, cp[i].y, cp[i2].x, cp[i2].y)) {
        return true;
      }
      i += density + 1;
    }
    return false;
  }

  public void processRect() {

    int density = (int) getParamValue(Param.DENSITY);

    int i = 0;
    while (i < cp.length - 1) {
      RectFinder.getInstance().process(cp[i]);
      i += density + 1;
    }
    RectFinder.getInstance().process(cp[cp.length - 1]);

  }

  public void execute() throws Exception {

    int density = (int) getParamValue(Param.DENSITY);

    int i = 0;
    while (i < cp.length - 1) {
      Executor.check();
      MouseBot.move(cp[i]);
      if (i == 0) {
        MouseBot.press();
      }
      i += density + 1;
    }
    MouseBot.release();

  }

  public Point getSnapped(Point p) {

    Point cp[] = {this.cp[0], this.cp[this.cp.length - 1]};

    for (int i = 0; i < cp.length; i++) {

      if (cp[i] == null || Controller.getProject().cpManager.containsCP(cp[i])) {continue;}
      if (Snap.process(cp[i], p)) {
        return cp[i];
      }
    }

    return null;
  }

  public boolean intersects(Rectangle2D rect, boolean containing) {
    if (containing) {
      Rectangle2D r = getRect();
      if (r.getWidth() * r.getHeight() == 0) {
        return rect.contains(cp[0].x, cp[0].y);
      }
      return rect.contains(getRect());
    }
    return intersects(rect);
  }

}