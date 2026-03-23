package mousebot.components;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.imageio.ImageIO;
import mousebot.commands.Paste;
import mousebot.commands.SelAddComponent;
import mousebot.commands.SelAddComponents;
import mousebot.commands.SelChangeMode;
import mousebot.commands.SelClear;
import mousebot.commands.SelRemove;
import mousebot.main.Controller;

/**
 * Za upravljanje odabranim komponentama.
 *
 * @author josip
 */
public class SelectionModel {

  public ObservableList<Component> selected = FXCollections.observableArrayList(); // trenutne odabrane komponente
  public BooleanProperty emptySelection = new SimpleBooleanProperty(true); // je li lista odabranih prazna

  public boolean crMode = false; // koristi li se kontorlni pravokutnik za upravljanje odabranih komponenti

  public SelectionModel() {
    selected.addListener(new ListChangeListener<Component>() {
      public void onChanged(Change<? extends Component> var1) {
        emptySelection.set(selected.isEmpty());
      }
    });
  }

  /**
   * Dodaje komponentu u odabir.
   *
   * @param c komponenta
   */
  public void addComponent(Component c) {
    Controller.getInvoker().execute(new SelAddComponent(c));
  }

  /**
   * Poništava odabir.
   */
  public void clear() {
    if (!selected.isEmpty()) {
      Controller.getInvoker().execute(new SelClear());
    }
  }

  /**
   * Mijenja način rada s kontrolnim točkama ili kontrolnim pravokutnikom.
   */
  public void changeMode() {
    Controller.getInvoker().execute(new SelChangeMode());
  }

  /**
   * Uklanja odabrane komponente.
   */
  public void removeSelectedComponents() {
    Controller.getInvoker().execute(new SelRemove());
  }

  /**
   * Pohranjuje odabrane komponente u međuspremnik.
   */
  public void copy() {

    try {
      Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
      ArrayList<Component> data = new ArrayList<>();
      selected.forEach(c -> data.add(c.clone()));
      cp.setContents(new ComponentsTransfer(data), null);
    } catch (Exception e) {}

  }

  /**
   * Ubacuje komponente iz međuspremnika.
   */
  @SuppressWarnings("unchecked")
  public void paste() {

    try {

      Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();

      ArrayList<Component> data = null;

      if (cp.isDataFlavorAvailable(ComponentsTransfer.df)) {
        data = (ArrayList<Component>) cp.getData(ComponentsTransfer.df);
      } else if (cp.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
        BufferedImage img = (BufferedImage) cp.getData(DataFlavor.imageFlavor);
        data = new ArrayList<>();
        Background bg = new Background(img);
        data.add(bg);
      }

      if (data != null) {
        Controller.getInvoker().execute(new Paste(data));
        Controller.getInvoker().execute(new SelAddComponents(data));
      }

    } catch (Exception e) {}

  }

  /**
   * Ako datoteka ima nastavak .mousebot u pitanje je MouseBot projekt iz sve komponente, osim slike podloge, se ubacuju
   * u trenutni projekt. Ako daoteka ima nastavak .png,.jpg ubacuje se slika podloge.
   *
   * @param f datoteka
   * @param x
   * @param y
   */
  @SuppressWarnings("unchecked")
  public void paste(File f, double x, double y) {

    ArrayList<Component> components = new ArrayList<>();

    try {
      if (f.getName().endsWith(".mousebot")) {

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
        int l = in.readInt();
        for (int i = 0; i < l; i++) {
          in.readUTF();
          in.readObject();
          in.readBoolean();
          ArrayList<Component> c = (ArrayList<Component>) in.readObject();
          c.forEach(comp -> {
            if (!(comp instanceof Background)) {
              components.add(comp);
            }
          });
        }
        in.close();

      } else {
        components.add(new Background(ImageIO.read(f)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!components.isEmpty()) {
      Controller.getInvoker().execute(new Paste(components, new Point(x, y)));
      Controller.getInvoker().execute(new SelAddComponents(components));
    }

  }

  /**
   * Ponovno postavljanje.
   */
  public void reset() {
    crMode = false;
    selected.clear();
  }

}