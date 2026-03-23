package mousebot.components;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Scanner;
import mousebot.components.Bezier.BezierType;
import mousebot.executing.Executor;
import mousebot.executing.MouseBot;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;
import mousebot.parameters.Param;

/**
 * Komponenta koja se odnosi na tekst.
 *
 * @author josip
 */
public class Text extends Component {

  private static final long serialVersionUID = 2746494016770153227L;

  public ArrayList<Point[]> subcomponents;

  public Text() {
    super(0, Param.ACCURACY_TEXT);
  }


  public Text(double x, double y, String text, String font) {
    this();
    processText(x, y, text, font);
  }

  public void render(Graphics g) {

    for (int i = 0; i < subcomponents.size(); i++) {
      Point[] cp = subcomponents.get(i);

      if (cp.length == 2) {
        g.strokeLine(cp[0].x, cp[0].y, cp[1].x, cp[1].y);
      } else {
        BezierProcessor.render(g, getAccuracy(), getBezierType(cp.length), cp);
      }

    }
  }

  public void processRect() {
    for (int j = 0; j < subcomponents.size(); j++) {
      Point[] cp = subcomponents.get(j);
      RectFinder.getInstance().process(cp);
    }
  }

  public boolean intersects(Rectangle2D rect) {

    for (int i = 0; i < subcomponents.size(); i++) {
      Point[] cp = subcomponents.get(i);

      if (cp.length == 2) {
        if (rect.intersectsLine(cp[0].x, cp[0].y, cp[1].x, cp[1].y)) {
          return true;
        }
      } else {
        if (BezierProcessor.intersect(rect, getAccuracy(), getBezierType(cp.length), cp)) {
          return true;
        }
      }

    }
    return false;
  }

  public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {

    for (int j = 0; j < subcomponents.size(); j++) {
      Point[] cp = subcomponents.get(j);
      for (int i = 0; i < cp.length; i++) {
        cp[i].transform(oldDim, dim, oldRect, rect, finish);
      }
    }

  }

  public void rotate(Point center, double angle, boolean finish) {

    for (int j = 0; j < subcomponents.size(); j++) {
      Point[] cp = subcomponents.get(j);
      for (int i = 0; i < cp.length; i++) {
        cp[i].rotate(center, angle, finish);
      }
    }

  }

  public ArrayList<Point[]> cloneSubComponents() {

    ArrayList<Point[]> cloned = new ArrayList<>();

    subcomponents.forEach(cp -> {
      Point c[] = new Point[cp.length];
      for (int i = 0; i < cp.length; i++) {
        c[i] = cp[i].clone();
      }
      cloned.add(c);
    });

    return cloned;
  }

  private void processText(double x, double y, String text, String fontfamily) {

    subcomponents = new ArrayList<>();

    ArrayList<String> lines = new ArrayList<>();
    Scanner scan = new Scanner(text);
    while (scan.hasNextLine()) {
      lines.add(scan.nextLine());
    }
    scan.close();

    Font font = new Font(fontfamily, Font.PLAIN, 64);

    int height = font.getSize();

    for (int i = 0; i < lines.size(); i++) {

      GlyphVector gv = font.createGlyphVector(new FontRenderContext(null, true, false), lines.get(i));
      java.awt.Shape shape = gv.getOutline((float) x, (float) y + (i * height));
      PathIterator p = shape.getPathIterator(null);

      Point start = null, curStart = null, end = null;

      while (!p.isDone()) {

        float cords[] = new float[6];
        int type = p.currentSegment(cords);

        switch (type) {
          case PathIterator.SEG_MOVETO:
            curStart = new Point(cords[0], cords[1]);
            start = curStart.clone();
            break;
          case PathIterator.SEG_LINETO:
            subcomponents.add(new Point[]{curStart, new Point(cords[0], cords[1])});
            curStart = new Point(cords[0], cords[1]);
            break;
          case PathIterator.SEG_QUADTO:
            end = new Point(cords[2], cords[3]);
            subcomponents.add(new Point[]{curStart, end, new Point(cords[0], cords[1])});
            curStart = end.clone();
            break;
          case PathIterator.SEG_CUBICTO:
            end = new Point(cords[4], cords[5]);
            subcomponents.add(new Point[]{curStart, end, new Point(cords[0], cords[1]), new Point(cords[2], cords[3])});
            curStart = end.clone();
            break;
          case PathIterator.SEG_CLOSE:
            curStart = start.clone();
            break;
        }

        p.next();
      }

    }

  }

  public void execute() throws Exception {

    for (int i = 0; i < subcomponents.size(); i++) {
      Executor.check();
      Point[] cp = subcomponents.get(i);

      if (cp.length == 2) {
        MouseBot.move(cp[0]);
        MouseBot.press();
        MouseBot.move(cp[1]);
        MouseBot.release();
      } else {
        BezierProcessor.execute(getAccuracy(), getBezierType(cp.length), cp);
      }

    }

  }

  private double getAccuracy() {
    return (double) getParamValue(Param.ACCURACY_TEXT);
  }

  private BezierType getBezierType(int len) {
    return len == 3 ? BezierType.QUADRATIC : BezierType.CUBIC;
  }

}