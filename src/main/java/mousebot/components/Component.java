package mousebot.components;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;
import mousebot.layers.Layer;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.Project;
import mousebot.main.RectFinder;
import mousebot.main.Snap;
import mousebot.parameters.GlobalParams;
import mousebot.parameters.Param;

/**
 * Osnovna klasa komponenta koju nasljeđuju sve konkretne komponente.
 *
 * @author josip
 */
public abstract class Component implements Serializable {

  // zahtjeva interface Serializable
  private static final long serialVersionUID = -6205508495134999814L;

  public transient Layer layer; // sloj u kojem se nalazi
  // transient znači da ovaj objekt nije serializable
  public Point[] cp; // niz kontrolnih točaka koje određuju položaj i oblik komponente
  public transient boolean selected = false; // je li komponenta odabrana s alatom odabir

  public HashMap<Param, Object> paramValues = new HashMap<>(); // parametri te vrijednosti za određenu komponentu

  /**
   *
   * @param cp     - broj kontrolnih točaka
   * @param params - parametri koje nasljeđena komponenta koristi
   */
  public Component(int cp, Param... params) {
    for (int i = 0; i < params.length; i++) {
      // vrijednosti se poprimaju na temelju trenutnih vrijednosti određenog parametra
      paramValues.put(params[i], GlobalParams.getInstance().get(params[i]));
    }
    if (cp > 0) {
      this.cp = new Point[cp];
    }
    layer = Controller.getProject().currentLayer;
  }

  /**
   * Ako je komponenta odabrana, potrebno ju je istaknuti podebljajući prikaz.
   *
   * @param g graphics
   */
  public void preRender(Graphics g) {
    if (selected) {
      g.gc.setLineWidth(Project.SELECTED_LINE_WIDTH);
    }
    render(g);
    if (selected) {
      g.gc.setLineWidth(Project.LINE_WIDTH);
    }
  }

  /**
   * Prikaz komponente.
   *
   * @param g graphics
   */
  public abstract void render(Graphics g);

  /**
   * Kreiraju se kontrolne točke da se mogu pomicati.
   *
   * @param remprevs treba li ukloniti prethodne kontrolne točke koje se mogu pomicati
   */
  public void createControlPoints(boolean remprevs) {
    if (remprevs) {
      Controller.getProject().cpManager.removeControlPoints();
    }
    createControlPoints();
  }

  /**
   * Samo određene komponente imaju mogućnost prikaza kontrolnih točaka da se pomiču. Određene nasljeđene komponente
   * override tu metodu.
   */
  public void createControlPoints() {}

  /**
   * Ako je vrijednost mustContain = true, vraća se true ako je cijela komponenta smještena u pravokutnik, inače false.
   * Ako je vrijednost mustContain = false, vraća se true ako se komponenta ukrštava s pravokutnikom, inače false.
   *
   * @param rect        pravokutnik
   * @param mustContain
   * @return true ili false
   */
  public boolean intersects(Rectangle2D rect, boolean mustContain) {
    if (mustContain) {
      return rect.contains(getRect());
    }
    return intersects(rect);
  }

  public abstract boolean intersects(Rectangle2D rect);

  /**
   * Služa da se dobiju krajne točke komponente.
   */
  public abstract void processRect();

  /**
   * Izvršavanje komponente. Pretvorba komponente u niz pomaka i pritisaka miša.
   *
   * @throws Exception ako je potrebno zaustaviti izvršavanje
   */
  public abstract void execute() throws Exception;

  /**
   * Ažuriranje kontrolnih točaka pri premještanju ili skaliranju kontrolnog pravokutnika.
   */
  public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {
    for (int i = 0; i < cp.length; i++) {
      cp[i].transform(oldDim, dim, oldRect, rect, finish);
    }
  }

  /**
   * Ažuriranje kontrolnih točaka pri rotiranju kontrolnog pravokutnika.
   */
  public void rotate(Point center, double angle, boolean finish) {
    if (this instanceof Background) {return;}
    for (int i = 0; i < cp.length; i++) {
      cp[i].rotate(center, angle, finish);
    }
  }

  /**
   * Ako je pokazivač miša blizu određene kontrolne točke, uhvatit će se za vrijednost kontrolne točke.
   *
   * @param pozicija pokazivača miša
   * @return uhvaćena kontrolna točka, ili null ako nije uhvaćena niti jedna točka
   */
  public Point getSnapped(Point p) {

    if (cp == null) {return null;}

    for (int i = 0; i < cp.length; i++) {

      if (cp[i] == null || Controller.getProject().cpManager.containsCP(cp[i])) {continue;}
      if (Snap.process(cp[i], p)) {
        return cp[i];
      }
    }

    return null;
  }

  /**
   * Vraća vrijednost za parametar p
   *
   * @param p
   * @return vrijednost
   */
  public Object getParamValue(Param p) {
    return paramValues.get(p);
  }

  /**
   * Postavlja parametru p vrijedst v.
   *
   * @param p
   * @param v
   */
  public void setParamValue(Param p, Object v) {
    if (paramValues.containsKey(p)) {
      paramValues.put(p, v);
    }
  }

  /**
   * Klonira komponentu.
   *
   * @return klon komponente
   */
  public Component clone() {

    try {

      final Component cloned = getClass().getDeclaredConstructor().newInstance();

      if (cloned instanceof HandLine) {
        cloned.cp = new Point[cp.length];
      }

      if (cp != null) {
        for (int i = 0; i < cp.length; i++) {
          if (cp[i] != null) {
            cloned.cp[i] = cp[i].clone();
          }
        }
      }

      if (cloned instanceof Text) {
        ((Text) cloned).subcomponents = ((Text) this).cloneSubComponents();
      } else if (cloned instanceof Bezier) {
        ((Bezier) cloned).initedCP1 = ((Bezier) this).initedCP1;
        ((Bezier) cloned).initedCP2 = ((Bezier) this).initedCP2;
      }
      if (cloned instanceof Background) {
        ((Background) cloned).img = ((Background) this).img.clone();
      }

      paramValues.forEach((key, value) -> {
        cloned.setParamValue(key, value);
      });

      if (cloned instanceof Background) {
        ((Background) cloned).img = ((Background) this).img.clone();
      }

      return cloned;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Vraća pravokutnik s krajnim točkama komponente.
   *
   * @return pravokutnik
   */
  public Rectangle2D getRect() {
    RectFinder.getInstance().reset();
    processRect();
    RectFinder.getInstance().fixZeroSize();
    Point start = RectFinder.getInstance().getStart();
    Point end = RectFinder.getInstance().getEnd();
    Rectangle2D rect = new Rectangle2D.Double(start.x, start.y, end.x - start.x, end.y - start.y);
    return rect;
  }

}