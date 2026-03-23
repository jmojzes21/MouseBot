package mousebot.components;

import java.io.Serializable;

/**
 * Točka.
 *
 * @author josip
 */
public class Point implements Serializable {

  private static final long serialVersionUID = 6187295158839591499L;

  public double x, y;

  public Point(double x, double y) {
    set(x, y);
  }

  public void set(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void set(Point p) {
    this.x = p.x;
    this.y = p.y;
  }

  public Point clone() {
    return new Point(x, y);
  }

  protected Point temp;

  /**
   * Transformira vrijednost točke iz prethodnog pravokutnika u novi.
   *
   * @param oldDim  dimenzije prethodnog pravokutnika
   * @param dim     dimenzije novog pravokutnika
   * @param oldRect početna točka prethodnog pravokutnika
   * @param rect    početna točka novog pravokutnika
   * @param finish  pomoćna varijabla
   */
  public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {
    if (temp == null) {
      temp = new Point(x, y);
    }

    x = (temp.x - oldRect.x) / oldDim.x * dim.x + rect.x;
    y = (temp.y - oldRect.y) / oldDim.y * dim.y + rect.y;

    if (finish) {
      temp = null;
    }
  }

  /**
   * Rotira vrijednost točke.
   *
   * @param center centar rotacije
   * @param angle  kut
   * @param finish pomoćna varijabla
   */
  public void rotate(Point center, double angle, boolean finish) {
    if (temp == null) {
      temp = new Point(x, y);
    }

    Point rotated = rotate(center, temp, angle);
    x = rotated.x;
    y = rotated.y;

    if (finish) {
      temp = null;
    }
  }

  /**
   * Pregled vrijednosti.
   */
  public String toString() {
    return String.format("(%.2f, %.2f)", x, y);
  }

  /**
   * @param center
   * @param angle
   * @return nova rotirana točka
   */
  public Point rotated(Point center, double angle) {
    double x = (this.x - center.x) * Math.cos(angle) - (this.y - center.y) * Math.sin(angle) + center.x;
    double y = (this.x - center.x) * Math.sin(angle) + (this.y - center.y) * Math.cos(angle) + center.y;
    return new Point(x, y);
  }

  /**
   * Rotira točku.
   *
   * @param center
   * @param angle
   */
  public void rotate(Point center, double angle) {
    double x = (this.x - center.x) * Math.cos(angle) - (this.y - center.y) * Math.sin(angle) + center.x;
    double y = (this.x - center.x) * Math.sin(angle) + (this.y - center.y) * Math.cos(angle) + center.y;
    this.x = x;
    this.y = y;
  }


  public boolean equals(Point p) {
    return x == p.x && y == p.y;
  }

  /**
   * @param a
   * @param b
   * @return udaljenost točaka a i b
   */
  public static double length(Point a, Point b) {
    return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
  }

  /**
   *
   * @param a
   * @param b
   * @return točka između točaka a i b
   */
  public static Point getMiddlePoint(Point a, Point b) {
    double x = (b.x + a.x) / 2d;
    double y = (b.y + a.y) / 2d;
    return new Point(x, y);
  }

  /**
   *
   * @param cp
   * @param p
   * @return kut između cp i p
   */
  public static double getAngle(Point cp, Point p) {
    return Math.atan2(p.y - cp.y, p.x - cp.x);
  }

  /**
   *
   * @param center točka rotacije
   * @param p
   * @param angle
   * @return rotirana točka p u odnosu na točku rotacije
   */
  public static Point rotate(Point center, Point p, double angle) {
    double x = (p.x - center.x) * Math.cos(angle) - (p.y - center.y) * Math.sin(angle) + center.x;
    double y = (p.x - center.x) * Math.sin(angle) + (p.y - center.y) * Math.cos(angle) + center.y;
    return new Point(x, y);
  }
}