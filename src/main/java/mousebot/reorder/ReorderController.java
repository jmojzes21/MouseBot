package mousebot.reorder;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mousebot.components.Component;
import mousebot.components.Point;
import mousebot.layers.Layer;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.Project;

public class ReorderController {

  public Canvas canvas;
  public Pane canvasPane;
  public Slider slider;

  private Stage stage;
  private Layer layer;

  private Graphics g;

  private Point ttemp = new Point(0, 0), told = new Point(0, 0);
  private boolean dragging = false;
  private boolean reordering = false;

  private Point mouseLoc = new Point(0, 0);
  private double canvasWidth;
  private double canvasHeight;

  private boolean moved[];
  private int pos[];
  private int maxComponents;

  public ReorderController(Stage stage) {
    this.stage = stage;
    this.layer = Controller.getProject().currentLayer;
    moved = new boolean[layer.components.size()];
    pos = new int[moved.length];
  }

  public void initialize() {
    initCanvas();
    slider.setMax(pos.length);
    slider.valueProperty().addListener((o, oldVal, newVal) -> {
      maxComponents = newVal.intValue();
      render();
    });
    slider.setValue(slider.getMax());
  }

  private void render() {

    g.gc.setFill(Color.BLACK);
    g.gc.fillRect(0, 0, canvasWidth, canvasHeight);

    g.gc.setLineWidth(Project.LINE_WIDTH);

    //layer.render(g, moved);
		/*ArrayList<Component> components = layer.components;
		g.gc.setStroke(Color.GREEN);
		for(int i = 0, v = -1; i < maxComponents; i++) {
			if(i < p) {
				components.get(pos[i]).preRender(g);
			}else {
				g.gc.setStroke(Color.RED);
				do {
					v++;
				}while(moved[v]);
				components.get(v).preRender(g);
			}
		}*/
    foreachComponent(layer.components, (c, i) -> {
      g.gc.setStroke(moved[i] ? Color.GREEN : Color.RED);
      c.preRender(g);
      return true;
    });

    if (!dragging && reordering) {
      g.gc.setStroke(Color.BLUE);
      g.gc.strokeOval(mouseLoc.x * g.scale() - 6 + g.translation.x, mouseLoc.y * g.scale() - 6 + g.translation.y, 12,
          12);
    }

  }

  private void reorder(double x, double y) {

    double scale = g.scaleI();
    Rectangle2D rect = new Rectangle2D.Double(x - 6 * scale, y - 6 * scale, 12 * scale, 12 * scale);
		
		/*for(int i = 0; i < layer.components.size(); i++) {	
			Component c = layer.components.get(i);
			
			if(!moved[i] && c.intersects(rect, false)) {
				moved[i] = true;
				prior(i);
				return;
			}

		}*/
    foreachComponent(layer.components, (c, i) -> {
      if (!moved[i] && c.intersects(rect, false)) {
        moved[i] = true;
        prior(i);
        return false;
      }
      return true;
    });

  }

  private void dereorder(double x, double y) {

    double scale = g.scaleI();
    Rectangle2D rect = new Rectangle2D.Double(x - 6 * scale, y - 6 * scale, 12 * scale, 12 * scale);

    foreachComponent(layer.components, (c, i) -> {
      if (moved[i] && c.intersects(rect, false)) {
        moved[i] = false;
        deprior(i);
        return false;
      }
      return true;
    });
  }

  private int p = 0;

  private void prior(int i) {
    pos[p] = i;
    p++;
  }

  private void deprior(int x) {

    int ind = -1;
    for (int i = 0; i < p; i++) {
      if (pos[i] == x) {
        ind = i;
        break;
      }
    }

    for (int i = ind; i < p - 1; i++) {
      pos[i] = pos[i + 1];
    }
    p--;
  }

  private double tx(double x) {
    return (x - g.translation.x) * g.scaleI();
  }

  private double ty(double y) {
    return (y - g.translation.y) * g.scaleI();
  }

  public void onFinish() {

    for (int i = p, v = -1; i < pos.length; i++) {
      do {
        v++;
      } while (moved[v]);
      pos[i] = v;
    }

    if (p > 0) {Controller.getInvoker().execute(new mousebot.commands.Reorder(layer, pos));}

    stage.close();

  }

  public void onReset() {
    for (int i = 0; i < moved.length; i++) {
      moved[i] = false;
    }
    p = 0;
    render();
  }

  public void onCancel() {
    stage.close();
  }

  public void onShow() {
    g.reset(layer, canvasWidth, canvasHeight);
    render();
  }

  private void initCanvas() {

    canvasPane.widthProperty().addListener((o, oldVal, newVal) -> {
      canvas.setWidth(newVal.doubleValue());
      canvasWidth = newVal.doubleValue();
      render();
    });
    canvasPane.heightProperty().addListener((o, oldVal, newVal) -> {
      canvas.setHeight(newVal.doubleValue());
      canvasHeight = newVal.doubleValue();
      render();
    });

    canvas.setOnScroll(e -> {
      g.zoom(tx(e.getX()), ty(e.getY()), e.getDeltaY());
      render();
    });

    canvas.setOnMousePressed(e -> {

      if (e.getButton() == MouseButton.MIDDLE) {
        dragging = true;
        ttemp.set(e.getX(), e.getY());
        told = g.translation.clone();
        canvas.setCursor(Cursor.HAND);
      } else if (e.getButton() == MouseButton.PRIMARY) {
        reordering = true;
        canvas.setCursor(Cursor.NONE);
        double x = tx(e.getX());
        double y = ty(e.getY());
        reorder(x, y);
      } else if (e.getButton() == MouseButton.SECONDARY) {
        reordering = true;
        canvas.setCursor(Cursor.NONE);
        double x = tx(e.getX());
        double y = ty(e.getY());
        dereorder(x, y);
      }

      render();

    });

    canvas.setOnMouseReleased(e -> {

      if (e.getButton() == MouseButton.PRIMARY) {
        reordering = false;
        canvas.setCursor(Cursor.DEFAULT);
      } else if (e.getButton() == MouseButton.MIDDLE) {
        dragging = false;
        canvas.setCursor(Cursor.DEFAULT);
      } else if (e.getButton() == MouseButton.SECONDARY) {
        reordering = false;
        canvas.setCursor(Cursor.DEFAULT);
      }

    });

    canvas.setOnMouseMoved(e -> {

      double x = tx(e.getX());
      double y = ty(e.getY());
      mouseLoc.set(x, y);

      render();

    });

    canvas.setOnMouseDragged(e -> {

      if (e.getButton() == MouseButton.MIDDLE) {
        g.translation.set(e.getX() - ttemp.x + told.x, e.getY() - ttemp.y + told.y);
      } else if (e.getButton() == MouseButton.PRIMARY) {
        double x = tx(e.getX());
        double y = ty(e.getY());
        reorder(x, y);
        mouseLoc.set(x, y);
      } else if (e.getButton() == MouseButton.SECONDARY) {
        double x = tx(e.getX());
        double y = ty(e.getY());
        dereorder(x, y);
        mouseLoc.set(x, y);
      }

      render();

    });

    canvas.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.H) {
        g.reset(layer, canvasWidth, canvasHeight);
        render();
      }
    });

    canvas.setFocusTraversable(true);

    g = new Graphics(canvas.getGraphicsContext2D());

  }

  private void foreachComponent(ArrayList<Component> components, Callback cb) {
    for (int i = 0, v = -1; i < maxComponents; i++) {
      if (i < p) {
        if (!cb.process(components.get(pos[i]), pos[i])) {return;}
      } else {
        do {
          v++;
        } while (moved[v]);
        if (!cb.process(components.get(v), v)) {return;}
      }
    }
  }

  private interface Callback {

    public boolean process(Component c, int i);
  }

}