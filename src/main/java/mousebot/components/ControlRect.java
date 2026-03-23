package mousebot.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import mousebot.commands.ControlRectPos;
import mousebot.commands.ControlRectRotate;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;
import mousebot.tools.ChangeCursor;

/**
 * Klasa kontrolnog pravokutnika te njegovo upravljanje.
 *
 * @author josip
 */
public class ControlRect {

  private static final double R = Math.pow(Math.toRadians(15), -1);

  public DoubleProperty startx, starty, endx, endy; // trenutne krajne točke pravokutnika
  private ControlRectCP[] controlPoints = new ControlRectCP[8]; // kontrolne točke pravokutnika za skaliranje

  public Point rotate; // točka rotacije
  private ControlPoint rotateCP; // omogućuje rotaciju točke rotacije

  // privremene vrijednosti
  public Point tempStart, tempEnd;
  private Point trTemp, trStart, trEnd, trR;

  private boolean translating = false, rotating = false;
  private double angle, startAngle;

  private ControlRectController crc; // određuje koje su komponente zahvaćene transformacijom (skaliranje,
  // premještanje, rotiranje) kontrolnog pravokutnika

  public ControlRect(ControlRectController crc) {

    this.crc = crc;

    RectFinder.getInstance().fixZeroSize();
    Point start = RectFinder.getInstance().getStart();
    Point end = RectFinder.getInstance().getEnd();

    startx = new SimpleDoubleProperty(start.x);
    starty = new SimpleDoubleProperty(start.y);
    endx = new SimpleDoubleProperty(end.x);
    endy = new SimpleDoubleProperty(end.y);

    controlPoints[0] = new ControlRectCP(startx, starty);
    controlPoints[1] = new ControlRectCP(endx, starty);

    controlPoints[2] = new ControlRectCP(endx, endy) {

      public void set(double x, double y) {

        if (Controller.getInstance().isShiftDown()) {

          double aspectRation = (tempEnd.x - tempStart.x) / (tempEnd.y - tempStart.y);

          double h = (x - tempStart.x) / aspectRation;

          if (x < tempStart.x) {
            h = -h;
          }

          if (y > tempStart.y) {
            y = tempStart.y + h;
          } else {
            y = tempStart.y - h;
          }

        }

        this.x.set(x);
        this.y.set(y);
      }

    };
    controlPoints[3] = new ControlRectCP(startx, endy);

    DoubleProperty mx = new SimpleDoubleProperty();
    mx.bind(endx.subtract(startx).divide(2d).add(startx));
    controlPoints[4] = new ControlRectCP(mx, starty) {
      public void set(double x, double y) {
        this.y.set(y);
      }
    };
    controlPoints[5] = new ControlRectCP(mx, endy) {
      public void set(double x, double y) {
        this.y.set(y);
      }
    };

    DoubleProperty my = new SimpleDoubleProperty();
    my.bind(endy.subtract(starty).divide(2d).add(starty));
    controlPoints[6] = new ControlRectCP(startx, my) {
      public void set(double x, double y) {
        this.x.set(x);
      }
    };
    controlPoints[7] = new ControlRectCP(endx, my) {
      public void set(double x, double y) {
        this.x.set(x);
      }
    };

    rotate = new Point(start.x + (end.x - start.x) / 2d, start.y + (end.y - start.y) / 2d);
    rotateCP = new ControlPoint(rotate) {
      private double r = 8d;

      public void render(Graphics g) {
        g.gc.setStroke(color);
        g.gc.strokeOval(p.x * g.scale() - r + g.translation.x, p.y * g.scale() - r + g.translation.y, 2d * r, 2d * r);
        g.gc.strokeLine(p.x * g.scale() - r + g.translation.x, p.y * g.scale() + g.translation.y,
            p.x * g.scale() + r + g.translation.x, p.y * g.scale() + g.translation.y);
        g.gc.strokeLine(p.x * g.scale() + g.translation.x, p.y * g.scale() - r + g.translation.y,
            p.x * g.scale() + g.translation.x, p.y * g.scale() + r + g.translation.y);
      }
    };

  }

  public void render(Graphics g) {

    if (!rotating) {

      g.gc.setStroke(Color.BLUE);
      g.strokeLine(startx.get(), starty.get(), endx.get(), starty.get());
      g.strokeLine(endx.get(), starty.get(), endx.get(), endy.get());
      g.strokeLine(endx.get(), endy.get(), startx.get(), endy.get());
      g.strokeLine(startx.get(), endy.get(), startx.get(), starty.get());

      for (int i = 0; i < controlPoints.length; i++) {
        controlPoints[i].render(g);
      }

    }

    rotateCP.render(g);
  }

  public void mousePressed(double x, double y, MouseButton button) throws ChangeCursor {

    if (button == MouseButton.PRIMARY) {

      try {
        for (int i = 0; i < controlPoints.length; i++) {
          controlPoints[i].mousePressed(x, y, button);
        }
      } catch (Exception exception) {
        tempStart = new Point(startx.get(), starty.get());
        tempEnd = new Point(endx.get(), endy.get());
        throw new ChangeCursor();
      }

      rotateCP.mousePressed(x, y, button);

      if (inside(x, y)) {
        tempStart = new Point(startx.get(), starty.get());
        tempEnd = new Point(endx.get(), endy.get());
        translating = true;
        trTemp = new Point(x, y);
        trStart = new Point(startx.get(), starty.get());
        trEnd = new Point(endx.get(), endy.get());
        trR = rotate.clone();
        throw new ChangeCursor(Cursor.MOVE);
      }

      if (insideRA(x, y)) {
        rotating = true;
        startAngle = Point.getAngle(rotate, new Point(x, y));
        throw new ChangeCursor(Controller.getInstance().getRotateCursor());
      }

    }

  }

  public void mouseReleased(double x, double y, MouseButton button) {

    if (button == MouseButton.PRIMARY) {

      if (translating) {
        translating = false;
        scaled(true);
        processScale();
        return;
      }

      if (rotating) {
        rotating = false;
        rotated(true);
        processRotate();
        return;
      }

      rotateCP.mouseReleased(x, y, button);

      try {
        for (int i = 0; i < controlPoints.length; i++) {
          controlPoints[i].mouseReleased(x, y, button);
        }
      } catch (Exception exception) {
        Controller.getInstance().canvas.setCursor(Cursor.HAND);
        scaled(true);
        processScale();
      }

    }

  }

  public void mouseMoved(double x, double y) throws ChangeCursor {

    for (int i = 0; i < controlPoints.length; i++) {
      controlPoints[i].mouseMoved(x, y);
    }

    rotateCP.mouseMoved(x, y);

    if (inside(x, y)) {
      throw new ChangeCursor(Cursor.MOVE);
    }

    if (insideRA(x, y)) {
      throw new ChangeCursor(Controller.getInstance().getRotateCursor());
    }

  }

  public void mouseDragged(double x, double y, MouseButton button) throws ChangeCursor {

    if (button == MouseButton.PRIMARY) {

      if (translating) {
        startx.set(x - trTemp.x + trStart.x);
        starty.set(y - trTemp.y + trStart.y);
        endx.set(x - trTemp.x + trEnd.x);
        endy.set(y - trTemp.y + trEnd.y);
        rotate.set(x - trTemp.x + trR.x, y - trTemp.y + trR.y);
        scaled(false);
        return;
      }

      if (rotating) {
        angle = Point.getAngle(rotate, new Point(x, y));
        if (Controller.getProject().shiftDown) {
          angle = Math.floor(R * angle) / R;
        }
        rotated(false);
        return;
      }

      rotateCP.mouseDragged(x, y, button);

      try {
        for (int i = 0; i < controlPoints.length; i++) {
          controlPoints[i].mouseDragged(x, y, button);
        }
      } catch (Exception exception) {
        scaled(false);
      }

    }

  }

  /**
   *
   * @param x
   * @param y
   * @return true ako je x,y unutar pravokutnika, inače false
   */
  private boolean inside(double x, double y) {

    double sx = startx.get();
    double sy = starty.get();

    double ex = endx.get();
    double ey = endy.get();

    double t;
    if (ex < sx) {
      t = ex;
      ex = sx;
      sx = t;
    }

    if (ey < sy) {
      t = ey;
      ey = sy;
      sy = t;
    }

    return x >= sx && x <= ex && y >= sy && y <= ey;

  }

  private static final double RA_OFFSET = 28d, RA_WIDTH = 40d;

  /**
   *
   * @param x
   * @param y
   * @return true ako je x,y u rotacijskom polju oko pravokutnika, inače false
   */
  private boolean insideRA(double x, double y) {

    double sx = startx.get();
    double sy = starty.get();

    double ex = endx.get();
    double ey = endy.get();

    double t;
    if (ex < sx) {
      t = ex;
      ex = sx;
      sx = t;
    }

    if (ey < sy) {
      t = ey;
      ey = sy;
      sy = t;
    }

    double scale = Controller.getGraphics().scaleI();
    double RA_offset = RA_OFFSET * scale, RA_width = RA_WIDTH * scale;

    boolean in1 = x >= sx - RA_width - RA_offset && x <= ex + RA_width + RA_offset && y >= sy - RA_width - RA_offset
        && y <= ey + RA_width + RA_offset;
    boolean in2 = x >= sx - RA_offset && x <= ex + RA_offset && y >= sy - RA_offset && y <= ey + RA_offset;
    return in1 && !in2;
  }

  /**
   * Zabilježi skaliranje ili premještanje.
   */
  private void processScale() {
    Controller.getInvoker().execute(new ControlRectPos(this));
  }

  /**
   * Skaliraj ili premjesti.
   *
   * @param finish pomoćna vrijednost
   */
  public void scaled(boolean finish) {

    double width = endx.get() - startx.get();
    double height = endy.get() - starty.get();

    double oldWidth = tempEnd.x - tempStart.x;
    double oldHeight = tempEnd.y - tempStart.y;

    if (oldWidth == 0) {
      oldWidth = width + 0.1;
      width -= 0.1;
    }

    if (oldHeight == 0) {
      oldHeight = height + 0.1;
      height -= 0.1;
    }

    Point oldDim = new Point(oldWidth, oldHeight);
    Point dim = new Point(width, height);
    Point rectStart = new Point(startx.get(), starty.get());

    rotate.transform(oldDim, dim, tempStart, rectStart, finish);
    crc.transform(oldDim, dim, tempStart, rectStart, finish);

    Controller.getProject().render();

  }

  public void scaled(Point start, Point end) {
    tempStart = new Point(startx.get(), starty.get());
    tempEnd = new Point(endx.get(), endy.get());
    startx.set(start.x);
    starty.set(start.y);
    endx.set(end.x);
    endy.set(end.y);
    scaled(true);
  }

  /**
   * Zabilježi rotaciju.
   */
  private void processRotate() {
    Controller.getInvoker().execute(new ControlRectRotate(this, angle - startAngle));
  }

  /**
   * Rotiranje.
   *
   * @param angle  kut
   * @param finish pomoćna vrijednost
   */
  public void rotated(double angle, boolean finish) {

    rotate.rotate(rotate, angle, finish);

    RectFinder.getInstance().reset();
    crc.rotated(rotate, angle, finish);

    Point start = RectFinder.getInstance().getStart();
    Point end = RectFinder.getInstance().getEnd();

    startx.set(start.x);
    starty.set(start.y);

    endx.set(end.x);
    endy.set(end.y);

    Controller.getProject().render();

  }

  private void rotated(boolean finish) {
    rotated(angle - startAngle, finish);
  }

  /**
   * Postavlja krajne točke pravokutnika.
   *
   * @param start
   * @param end
   */
  public void setPoints(Point start, Point end) {
    startx.set(start.x);
    starty.set(start.y);
    endx.set(end.x);
    endy.set(end.y);
    rotate.set(start.x + (end.x - start.x) / 2d, start.y + (end.y - start.y) / 2d);
  }

  /**
   * Ponovno postavi krajne točke.
   */
  public void resetPoints() {
    setPoints(RectFinder.getInstance().getStart(), RectFinder.getInstance().getEnd());
  }

}