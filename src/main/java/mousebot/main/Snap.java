package mousebot.main;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import mousebot.components.Component;
import mousebot.components.Point;
import mousebot.layers.Layer;
import mousebot.tools.DrawingTool;

/**
 * Za hvatanje kontrolne točke ili za rešetku.
 *
 * @author josip
 */
public class Snap {

  private static final double r = 12d;

  public BooleanProperty enabled = new SimpleBooleanProperty(false);
  public BooleanProperty gridEnabled = new SimpleBooleanProperty(false);
  public IntegerProperty gridSize = new SimpleIntegerProperty(100);

  private double sx, sy;
  private boolean snapped = false;

  /**
   * Pokušaj zahvatiti.
   *
   * @param p ako je zahvatiti, bit će promjenjen
   */
  public void snap(Point p) {
    if (!processComponents(p) && !processGrid(p)) {
      if (snapped) {
        snapped = false;
        Controller.getProject().render();
      }
    } else {
      Controller.getProject().render();
    }
  }

  /**
   * Pokuša uhvatit neku kontrolnu točku komponenti.
   *
   * @param p
   * @return true ako je uhvaćeno, inač ništa
   */
  private boolean processComponents(Point p) {

    ObservableList<Layer> layers = Controller.getLayersManager().layers;
    for (int i = 0; i < layers.size(); i++) {

      if (!layers.get(i).isEnabled()) {continue;}

      ArrayList<Component> components = layers.get(i).components;
      for (int j = 0; j < components.size(); j++) {

        Component c = components.get(j);

        if (Controller.getProject().tool.get() instanceof DrawingTool) {
          if (((DrawingTool<?>) Controller.getProject().tool.get()).c == c) {
            continue;
          }
        }

        Point temp = c.getSnapped(p);
        if (temp != null) {
          snapped = true;
          sx = temp.x;
          sy = temp.y;
          p.set(sx, sy);
          return true;
        }

      }

    }

    return false;
  }

  /**
   * Pokuša uhvatiti točku rešetke.
   *
   * @param p
   * @return true ako je uhvaćeno, inač ništa
   */
  private boolean processGrid(Point p) {

    if (!gridEnabled.get()) {return false;}

    double size = gridSize.doubleValue();

    double x = Math.round(p.x / size) * size;
    double y = Math.round(p.y / size) * size;

    if (process(new Point(x, y), p)) {
      snapped = true;
      sx = x;
      sy = y;
      p.set(sx, sy);
      return true;
    }

    return false;
  }

  /**
   * Prikaz rešetke.
   *
   * @param g
   */
  public void renderGrid(Graphics g) {

    if (!gridEnabled.get()) {return;}

    double width = Controller.getProject().width;
    double height = Controller.getProject().height;

    double scale = g.scale();

    double tx = g.translation.x;
    double ty = g.translation.y;

    g.gc.setLineWidth(1);
    g.gc.setStroke(Color.grayRgb(30));

    double ssize = gridSize.doubleValue() * scale;

    int vl = (int) Math.ceil(width / ssize);
    double kx = tx - Math.floor(tx / ssize) * ssize;
    for (double v = 0; v < vl; v++) {
      double lx = v * ssize + kx;
      g.gc.strokeLine(lx, 0, lx, height);
    }

    int hl = (int) Math.ceil(height / ssize);
    double ky = ty - Math.floor(ty / ssize) * ssize;
    for (double h = 0; h < hl; h++) {
      double ly = h * ssize + ky;
      g.gc.strokeLine(0, ly, width, ly);
    }

  }

  /**
   * Prikazuje točku koja je uhvačena.
   *
   * @param g
   */
  public void renderSnapped(Graphics g) {
    if (snapped && !enabled.get()) {
      snapped = false;
    }
    if (snapped) {
      g.gc.setStroke(Color.GREEN);
      g.gc.strokeRect(sx * g.scale() - r + g.translation.x, sy * g.scale() - r + g.translation.y, 2d * r, 2d * r);
    }
  }

  /**
   * Ako je pokazivač miša dovoljo blizu točki, uhvatit će se, tj. promjenit će koordinate tako da odgovaraju uhvaćenoj
   * točki.
   *
   * @param cp
   * @param p
   * @return true ako treba uhvatiti, inače false
   */
  public static boolean process(Point cp, Point p) {
    return Point.length(cp, p) <= r * Controller.getProject().g.scaleI();
  }

}