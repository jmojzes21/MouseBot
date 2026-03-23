package mousebot.main;

import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import mousebot.components.Component;
import mousebot.components.Point;
import mousebot.layers.Layer;

/**
 * Pomoćna klasa za crtanje grafike.
 *
 * @author josip
 */
public class Graphics {

  private static final double deltaScale = 1.1d; // za koliko se promjeni zoom

  public final GraphicsContext gc; // JavaFx klasa za crtanje grafike
  public Point translation = new Point(0, 0); // trenutna translacija
  private double scale = 1d; // trenutni zoom

  public Graphics(GraphicsContext gc) {
    this.gc = gc;
  }

  /**
   * Crta liniju.
   *
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public void strokeLine(double x1, double y1, double x2, double y2) {
    gc.strokeLine(x1 * scale + translation.x, y1 * scale + translation.y, x2 * scale + translation.x,
        y2 * scale + translation.y);
  }

  /**
   * Crta liniju.
   *
   * @param p1
   * @param p2
   */
  public void strokeLine(Point p1, Point p2) {
    strokeLine(p1.x, p1.y, p2.x, p2.y);
  }

  /**
   * Crta liniju od prethodne točke to nove(x, y).
   *
   * @param x
   * @param y
   * @param start true ako treba postaviti početnu točku, inače false
   */
  public void lineTo(double x, double y, boolean start) {
    x = x * scale + translation.x;
    y = y * scale + translation.y;
    if (start) {
      gc.moveTo(x, y);
    } else {
      gc.lineTo(x, y);
    }
  }

  /**
   * @return trenutni zoom
   */
  public double scale() {
    return scale;
  }

  /**
   * @return 1 / trenutni zoom
   */
  public double scaleI() {
    return Math.pow(scale, -1);
  }

  /**
   * Ponovno postavi pogled na komponente te tako poništi zoom i centrira pogled.
   *
   * @see center
   */
  public void reset() {
    scale = 1;
    center();
  }

  public void reset(Layer l, double w, double h) {
    scale = 1;
    center(l, w, h);
  }

  /**
   * Centrira pogled.
   */
  private void center() {

    RectFinder.getInstance().reset();
    ;
    ObservableList<Layer> layers = Controller.getLayersManager().layers;
    for (int i = 0; i < layers.size(); i++) {
      if (!layers.get(i).isEnabled()) {continue;}
      for (int j = 0; j < layers.get(i).components.size(); j++) {
        Component c = layers.get(i).components.get(j);
        c.processRect();
      }
    }

    Point start = RectFinder.getInstance().getStart();
    Point end = RectFinder.getInstance().getEnd();

    if (start == null) {
      translation.set(0, 0);
      return;
    }

    double x = (Controller.getProject().width - (end.x - start.x)) / 2d - start.x;
    double y = (Controller.getProject().height - (end.y - start.y)) / 2d - start.y;

    translation.set(x, y);
  }

  private void center(Layer layer, double w, double h) {

    RectFinder.getInstance().reset();
    ;
    for (int j = 0; j < layer.components.size(); j++) {
      Component c = layer.components.get(j);
      c.processRect();
    }

    Point start = RectFinder.getInstance().getStart();
    Point end = RectFinder.getInstance().getEnd();

    if (start == null) {
      translation.set(0, 0);
      return;
    }

    double x = (w - (end.x - start.x)) / 2d - start.x;
    double y = (h - (end.y - start.y)) / 2d - start.y;

    translation.set(x, y);
  }

  /**
   * Zoomira.
   *
   * @param x x koordinata miša
   * @param y y koordinata miša
   * @param d ako je veće od 0, potrebno je povećati pogled, inčae smanjiti
   */
  public void zoom(double x, double y, double d) {

    translation.x += x * scale;
    translation.y += y * scale;

    if (d > 0) {
      scale = Math.min(scale * deltaScale, 50d);
    } else if (d < 0) {
      scale = Math.max(scale / deltaScale, 0.1d);
    }

    translation.x += -x * scale;
    translation.y += -y * scale;

  }

}