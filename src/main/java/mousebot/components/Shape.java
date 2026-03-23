package mousebot.components;

import mousebot.main.Controller;
import mousebot.parameters.Param;

/**
 * Komponente pravokutnik i elipsa su tipa oblik te imaju dodatne metode.
 *
 * @author josip
 */
public abstract class Shape extends Component {

  private static final long serialVersionUID = -1601414390439744384L;

  public Shape(int cp, Param... params) {
    super(cp, params);
  }

  public Point lastEndPoint;

  // za postavljanje završne točke
  public void toolSetEndPoint(double x, double y) {

    if (Controller.getProject().shiftDown) {
      double w = Math.abs(x - cp[0].x);

      if (y > cp[0].y) {
        y = cp[0].y + w;
      } else {
        y = cp[0].y - w;
      }

    }

    lastEndPoint = new Point(x, y);
    setEndPoint(x, y);
  }

  public abstract void setEndPoint(double x, double y);

}