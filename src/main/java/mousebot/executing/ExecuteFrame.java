package mousebot.executing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mousebot.commands.Invoker;
import mousebot.components.Background;
import mousebot.components.Component;
import mousebot.components.ControlRect;
import mousebot.components.ControlRectController;
import mousebot.components.Point;
import mousebot.layers.Layer;
import mousebot.layers.LayersManager;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;
import mousebot.tools.ChangeCursor;

/**
 * Za prikaz pregleda izvršavanja.
 *
 * @author josip
 */
public class ExecuteFrame {

  private Stage stage;
  public Graphics g;

  private double width, height;

  private ArrayList<Component> components = new ArrayList<>(); // lista svih komponenti
  private ControlRect cr; // kontrolni pravokutnik za upravljanje komponentama
  public Invoker invoker = new Invoker(); // invoker za undo-redo sustav pri pregledu

  private boolean controlDown = false;
  public boolean shiftDown = false;

  public ExecuteFrame() {
    initFrame();
  }

  /**
   * Inicijaliziranje prozora.
   */
  private void initFrame() {

    stage = new Stage();
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.initModality(Modality.APPLICATION_MODAL);

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    width = screen.getWidth();
    height = screen.getHeight();

    stage.setWidth(width);
    stage.setHeight(height);

    stage.setX(0);
    stage.setY(0);

    StackPane sp = new StackPane();

    Canvas canvas = new Canvas(width, height);
    sp.getChildren().add(canvas);
    g = new Graphics(canvas.getGraphicsContext2D());

    Scene scene = new Scene(sp);
    stage.setScene(scene);

    scene.setFill(Color.TRANSPARENT);
    stage.setOpacity(0.5);

    canvas.setOnMousePressed(e -> {

      Controller.getInstance().mbPressed++;

      double x = e.getX();
      double y = e.getY();

      try {
        cr.mousePressed(x, y, e.getButton());
      } catch (ChangeCursor _) {}

      render();

    });

    canvas.setOnMouseReleased(e -> {

      Controller.getInstance().mbPressed--;

      double x = e.getX();
      double y = e.getY();

      cr.mouseReleased(x, y, e.getButton());
      render();

    });

    canvas.setOnMouseMoved(e -> {

      double x = e.getX();
      double y = e.getY();

      try {
        cr.mouseMoved(x, y);
        canvas.setCursor(Cursor.DEFAULT);
      } catch (ChangeCursor c) {
        canvas.setCursor(c.cursor);
      }

    });

    canvas.setOnMouseDragged(e -> {

      double x = e.getX();
      double y = e.getY();

      try {
        cr.mouseDragged(x, y, e.getButton());
      } catch (Exception ex) {}
      render();

    });

    canvas.setOnKeyPressed(e -> {

      if (e.getCode() == KeyCode.DEAD_CEDILLA) {
        initComponents();
        for (int i = 0; i < components.size() * 3; i++) {
          Collections.swap(components, random(components.size()), random(components.size()));
        }
        return;
      }

      if (e.getCode() == KeyCode.CONTROL) {
        controlDown = true;
        return;
      } else if (e.getCode() == KeyCode.SHIFT) {
        shiftDown = true;
        return;
      }

      if (!controlDown) {return;}

      if (e.getCode() == KeyCode.R) {
        initComponents();
      } else if (e.getCode() == KeyCode.Z) {
        invoker.undo();
        render();
      } else if (e.getCode() == KeyCode.Y) {
        invoker.redo();
        render();
      }

    });

    canvas.setOnKeyReleased(e -> {
      if (e.getCode() == KeyCode.CONTROL) {
        controlDown = false;
      } else if (e.getCode() == KeyCode.SHIFT) {
        shiftDown = false;
      }
    });

    canvas.setFocusTraversable(true);

  }

  /**
   * Inicijaliziranje komponenti.
   */
  public void initComponents() {

    components.clear();
    invoker.reset();

    RectFinder.getInstance().reset();
    LayersManager lm = Controller.getLayersManager();
    for (int l = 0; l < lm.layers.size(); l++) {
      Layer layer = lm.layers.get(l);
      if (!layer.isEnabled()) {continue;}
      for (int i = 0; i < layer.components.size(); i++) {
        Component c = layer.components.get(i).clone();
        if (c instanceof Background) {continue;}
        components.add(c);
        c.processRect();

      }
    }

    RectFinder.getInstance().fixZeroSize();
    Point start = RectFinder.getInstance().getStart().clone();
    Point end = RectFinder.getInstance().getEnd().clone();

    double rwidth = end.x - start.x;
    double rheight = end.y - start.y;

    double aspectRation = rheight / rwidth;

    if (rwidth > rheight) {
      if (rwidth > width) {
        rwidth = width - 30;
        rheight = rwidth * aspectRation;
      }
      if (rheight > height) {
        rheight = height - 30;
        rwidth = rheight / aspectRation;
      }
    } else {
      if (rheight > height) {
        rheight = height - 30;
        rwidth = rheight / aspectRation;
      }
      if (rwidth > width) {
        rwidth = width - 30;
        rheight = rwidth * aspectRation;
      }
    }

    start.x = (width - rwidth) / 2d;
    start.y = (height - rheight) / 2d;
    end.x = start.x + rwidth;
    end.y = start.y + rheight;

    cr = new ControlRect(ControlRectController.EXECUTINGFRAME);
    cr.scaled(start, end);
    render();
  }

  private void render() {

    g.gc.clearRect(0, 0, width, height);

    g.gc.setStroke(Color.BLACK);
    for (int i = 0; i < components.size(); i++) {
      components.get(i).render(g);
    }

    cr.render(g);

  }

  public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).transform(oldDim, dim, oldRect, rect, finish);
    }
  }

  public void rotated(Point center, double angle, boolean finish) {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).rotate(center, angle, finish);
      components.get(i).processRect();
    }
  }

  /**
   * Prikaži prozor.
   */
  public void show() {
    stage.setAlwaysOnTop(true);
    stage.show();
  }

  /**
   * Zatvori prozor.
   */
  public void close() {
    stage.close();
  }

  public ArrayList<Component> getComponents() {
    return components;
  }

  /**
   * Vraća nasumični broj.
   *
   * @param bound max
   * @return nasumični broj >= 0 i < max
   */
  private int random(int bound) {
    return ThreadLocalRandom.current().nextInt(bound);
  }

}