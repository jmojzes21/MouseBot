package mousebot.tools;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import mousebot.commands.SelAddComponents;
import mousebot.commands.SelSetGP;
import mousebot.components.Component;
import mousebot.components.Point;
import mousebot.components.SelectionModel;
import mousebot.layers.Layer;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.parameters.Param;

/*
 * Za odabir komponenti.
 */
public class TSelect extends Tool {

  public TSelect() {
    super("tl.basic.select", "select");
  }

  private ObservableList<Layer> layers = Controller.getLayersManager().layers;
  private SelectionModel selectionModel = Controller.getProject().selectionModel;

  // pomoćne varijable za definiranje pravokutnika za višestruki odabir
  private Point start = new Point(0, 0), end = new Point(0, 0);
  private boolean multipleSelecting = false;

  public void mousePressed(double x, double y, MouseButton button) {

    if (button == MouseButton.PRIMARY) {

      // odabir komponente klikom miša

      if (Controller.getProject().cpManager.cr != null) {return;}

      start.set(x, y);

      double scale = Controller.getProject().g.scaleI();
      Rectangle2D rect = new Rectangle2D.Double(x - 6 * scale, y - 6 * scale, 12 * scale, 12 * scale);

      for (int i = 0; i < layers.size(); i++) {
        if (!layers.get(i).isEnabled()) {continue;}
        for (int j = 0; j < layers.get(i).components.size(); j++) {
          Component c = layers.get(i).components.get(j);
          if (!c.selected && c.intersects(rect, false)) {
            selectionModel.addComponent(c);
            return;
          }
        }
      }

    } else if (button == MouseButton.SECONDARY) {
      selectionModel.clear(); // poništenje odabira
    }

  }

  public void mouseReleased(double mx, double my, MouseButton button) {

    // odabir komponenti pravokutnikom
    if (multipleSelecting) {
      multipleSelecting = false;

      double x = start.x;
      double y = start.y;

      double w = mx - start.x;
      double h = my - start.y;

      if (w < 0) {
        w = -w;
        x -= w;
      }

      if (h < 0) {
        h = -h;
        y -= h;
      }

      Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
      ArrayList<Component> toSelect = new ArrayList<>();

      for (int i = 0; i < layers.size(); i++) {
        if (!layers.get(i).isEnabled()) {continue;}
        for (int j = 0; j < layers.get(i).components.size(); j++) {
          Component c = layers.get(i).components.get(j);
          if (!c.selected && c.intersects(rect, true)) {
            toSelect.add(c);
          }
        }
      }

      if (!toSelect.isEmpty()) {
        Controller.getInvoker().execute(new SelAddComponents(toSelect));
      }

    }
  }

  public void mouseMoved(double x, double y) {}

  public void mouseDragged(double x, double y, MouseButton button) {
    if (button == MouseButton.PRIMARY && Controller.getProject().cpManager.cr == null) {
      multipleSelecting = true;
      end.set(x, y);
      Controller.getProject().render();
    }
  }

  public void cancel() {
    selectionModel.clear();
  }

  public void paramChanged(Param p, Object v) {

    ObservableList<Component> components = selectionModel.selected;
    Object vals[] = (Object[]) v;
    SelSetGP command = new SelSetGP(p, vals[0], vals[1]);

    for (int i = 0; i < components.size(); i++) {
      Component c = components.get(i);
      if (c.getParamValue(p) != null) {
        command.components.add(c);
      }
    }

    Controller.getInvoker().execute(command);

  }

  public void initParamValues() {}

  public void render(Graphics g) {
    if (multipleSelecting) {
      g.gc.setStroke(Color.BLUE);
      g.gc.beginPath();
      g.lineTo(start.x, start.y, true);
      g.lineTo(end.x, start.y, false);
      g.lineTo(end.x, end.y, false);
      g.lineTo(start.x, end.y, false);
      g.lineTo(start.x, start.y, false);
      g.gc.stroke();
    }
  }

  public Param[] createParams() {
    return null;
  }

}