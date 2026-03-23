package mousebot.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import mousebot.commands.Invoker;
import mousebot.components.Component;
import mousebot.components.ControlPointsManager;
import mousebot.components.Point;
import mousebot.components.SelectionModel;
import mousebot.layers.Layer;
import mousebot.layers.LayersManager;
import mousebot.layers.SerializableColor;
import mousebot.tools.TSelect;
import mousebot.tools.Tool;

/**
 * Objekt projekt.
 *
 * @author josip
 */
public class Project {

  public static final float LINE_WIDTH = 2, SELECTED_LINE_WIDTH = 4;

  public Invoker invoker; // upravitelj naredbama

  public ObjectProperty<Tool> tool = new SimpleObjectProperty<>(); // Property na trenutni alat
  public BooleanProperty isToolSelect = new SimpleBooleanProperty(false); // je li trenutni alat TSelect
  public Layer currentLayer; // trenutni sloj
  public Graphics g;
  public double width, height; // širina i visina canvas-a
  public Point mouseLoc = new Point(0, 0); // zadnje detektirana lokacija miša

  public boolean shiftDown = false; // drži li se tipka shift

  // pomoćni objekti za translatiranje, tj. projmenu pogleda
  public Point ttemp = new Point(0, 0), told = new Point(0, 0);

  public SelectionModel selectionModel; // upravitelj odabranih komponenti
  public ControlPointsManager cpManager; // upravitlej za prikaz kontorlnih točki

  public LayersManager lm; // upravitelj slojeva

  public File file = null; // datoteka u kojoj je pohranjen projekt

  public Snap snap; // za hvatanje za kontrolnu točku komponente ili rešetku

  public boolean unsaved = false; // treba li pri zatvaranju projekta pitati korisnika za spremanje

  public Project() {

    invoker = new Invoker();
    selectionModel = new SelectionModel();
    cpManager = new ControlPointsManager();
    tool.addListener((o, oldVal, newVal) -> {
      isToolSelect.set(newVal instanceof TSelect);
    });
    snap = new Snap();
  }

  public void render() {

    g.gc.setFill(Color.BLACK);
    g.gc.fillRect(0, 0, width, height);

    snap.renderGrid(g);

    g.gc.setLineWidth(LINE_WIDTH);

    for (int i = 0; i < lm.layers.size(); i++) {
      lm.layers.get(i).render(g);
    }

    cpManager.render(g);

    if (isToolSelect.get()) {
      TSelect select = (TSelect) tool.get();
      select.render(g);
    }

    snap.renderSnapped(g);

  }

  public Point getMouseLoc() {
    return mouseLoc.clone();
  }

  public void mousePressed(double x, double y, MouseButton b) {
    if (b == MouseButton.MIDDLE) {
      ttemp.set(x, y);
      told = g.translation.clone();
      Controller.getInstance().canvas.setCursor(Cursor.HAND);
    }
  }

  public void mouseReleased(double x, double y, MouseButton b) {
    if (b == MouseButton.MIDDLE) {
      Controller.getInstance().canvas.setCursor(Cursor.DEFAULT);
    }
  }

  public void mouseDragged(double x, double y, MouseButton b) {
    if (b == MouseButton.MIDDLE) {
      g.translation.set(x - ttemp.x + told.x, y - ttemp.y + told.y);
      render();
    }
  }

  /**
   * Sprema projekt u datoteku.
   *
   * @return true ako je uspješno, inače false
   */
  public boolean saveProject() {

    try {

      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

      ObservableList<Layer> layers = lm.layers;
      out.writeInt(layers.size());
      int ci = -1;
      for (int i = 0; i < layers.size(); i++) {
        Layer l = layers.get(i);
        out.writeUTF(l.name.get());
        out.writeObject(l.color);
        out.writeBoolean(l.enabled.get());
        out.writeObject(l.components);
        if (currentLayer == l) {
          ci = i;
        }
      }
      out.writeInt(ci);
      out.close();

      return true;

    } catch (Exception e) {
      Dialogs.show(AlertType.ERROR, "dialogs.error.save", ButtonType.OK);
      return false;
    }

  }

  /**
   * Očisti trenutni projekt. Ako inputn nije null, dodaje input slojeve, tj. učitava projekt.
   *
   * @param input
   * @param c
   * @see LayersManager.reset
   */
  public void clearOrOpen(Layer[] input, int c) {
    invoker.reset();
    selectionModel.reset();
    cpManager.reset();
    lm.reset(input, c);
    g.reset();
    file = null;
    Controller.getParamsManager().clearParams();
  }

  /**
   * Otvara projekt.
   *
   * @param file datoteka projekta
   * @return true ako je uspješno, inače false
   */
  @SuppressWarnings("unchecked")
  public boolean openProject(File file) {

    try {

      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
      Layer layers[] = new Layer[in.readInt()];
      for (int i = 0; i < layers.length; i++) {
        layers[i] = new Layer(in.readUTF(), (SerializableColor) in.readObject(), in.readBoolean(),
            (ArrayList<Component>) in.readObject());
      }
      int c = in.readInt();
      in.close();

      clearOrOpen(layers, c);

      return true;

    } catch (Exception e) {
      Dialogs.show(AlertType.ERROR, "dialogs.error.open", ButtonType.OK);
      return false;
    }

  }

}