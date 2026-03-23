package mousebot.components;

import java.util.ArrayList;
import javafx.scene.input.MouseButton;
import mousebot.main.Graphics;
import mousebot.tools.ChangeCursor;

/**
 * Služi za upravljanje prikaza kontrolnih točaka te kontrolnog pravokutnika.
 *
 * @author josip
 */
public class ControlPointsManager {

  // lista kontolnih točaka koje se prikazuju te se mogu pomicati
  private ArrayList<ControlPoint> controlPoints = new ArrayList<>();

  public ControlRect cr; // instanca kontrolnog pravokutnika

  public void render(Graphics g) {

    for (int i = 0; i < controlPoints.size(); i++) {
      controlPoints.get(i).render(g);
    }

    if (cr != null) {
      cr.render(g);
    }

  }

  /**
   * Prikaži kontrolne točke da se mogu pomicati.
   *
   * @param points nik točaka
   */
  public void createControlPoints(Point... points) {
    for (Point p : points) {
      controlPoints.add(new ControlPoint(p));
    }
  }

  /**
   * Uklanja sve prikazane kontrolne točke.
   */
  public void removeControlPoints() {
    controlPoints.clear();
  }

  /**
   *
   * @param x
   * @param y
   * @param button
   * @throws ChangeCursor ako je potrebno prekinuti provjere utjecaja eventa
   */
  public void mousePressed(double x, double y, MouseButton button) throws ChangeCursor {

    if (cr != null) {
      cr.mousePressed(x, y, button);
    }

    for (int i = 0; i < controlPoints.size(); i++) {
      controlPoints.get(i).mousePressed(x, y, button);
    }

  }

  public void mouseReleased(double x, double y, MouseButton button) {

    for (int i = 0; i < controlPoints.size(); i++) {
      controlPoints.get(i).mouseReleased(x, y, button);
    }

    if (cr != null) {

      cr.mouseReleased(x, y, button);
    }

  }

  /**
   *
   * @param x
   * @param y
   * @throws ChangeCursor ako je potrebno prekinuti provjere utjecaja eventa
   */
  public void mouseMoved(double x, double y) throws ChangeCursor {

    for (int i = 0; i < controlPoints.size(); i++) {
      controlPoints.get(i).mouseMoved(x, y);
    }

    if (cr != null) {
      cr.mouseMoved(x, y);
    }

  }

  /**
   *
   * @param x
   * @param y
   * @param button
   * @throws ChangeCursor ako je potrebno prekinuti provjere utjecaja eventa
   */
  public void mouseDragged(double x, double y, MouseButton button) throws ChangeCursor {

    for (int i = 0; i < controlPoints.size(); i++) {
      controlPoints.get(i).mouseDragged(x, y, button);
    }

    if (cr != null) {
      cr.mouseDragged(x, y, button);
    }

  }

  /**
   *
   * @param p
   * @return true ako se točka p prikazuje, inače false
   */
  public boolean containsCP(Point p) {
    for (int i = 0; i < controlPoints.size(); i++) {
      if (controlPoints.get(i).p == p) {
        return true;
      }
    }
    return false;
  }

  /**
   * Ponovno postavljanje.
   */
  public void reset() {
    controlPoints.clear();
    cr = null;
  }

}