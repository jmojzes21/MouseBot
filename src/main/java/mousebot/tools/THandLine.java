package mousebot.tools;

import java.util.ArrayList;
import javafx.scene.input.MouseButton;
import mousebot.commands.HandLineAdd;
import mousebot.components.HandLine;
import mousebot.components.Point;
import mousebot.main.Controller;
import mousebot.parameters.Param;

/*
 * Za crtanje ručne krivulje.
 */
public class THandLine extends DrawingTool<HandLine> {

  private static final Param[] gparams = {Param.DENSITY};

  public ArrayList<Point> points = new ArrayList<>();

  public THandLine() {
    super("tl.basic.handline", "handline");
  }

  public void mousePressed(double x, double y, MouseButton b) {

    if (b == MouseButton.PRIMARY && c == null) {
      c = new HandLine();
      points.add(new Point(x, y));
      Controller.getInvoker().execute(new HandLineAdd(c));
    }
  }

  public void mouseReleased(double x, double y, MouseButton b) {

    if (b == MouseButton.PRIMARY && c != null) {
      if (points.size() < 2) {
        points.add(new Point(x, y));
      }
      finishHandLine();
    }

  }

  public void mouseMoved(double x, double y) {}

  public void mouseDragged(double x, double y, MouseButton b) {

    if (b == MouseButton.PRIMARY & c != null) {
      points.add(new Point(x, y));
      Controller.getProject().render();
    }

  }

  public void finishHandLine() {
    if (c != null) {
      HandLine hl = (HandLine) c;
      hl.cp = new Point[points.size()];
      for (int i = 0; i < points.size(); i++) {
        hl.cp[i] = points.get(i);
      }
      c = null;
    }
    points.clear();
  }

  public void cancel() {}

  public Param[] createParams() {
    return gparams;
  }

  public void paramChanged(Param p, Object v) {}

}