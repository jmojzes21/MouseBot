package mousebot.tools;

import javafx.scene.input.MouseButton;
import mousebot.components.Component;
import mousebot.parameters.Param;

/**
 * Abstraktna klasa za alat.
 *
 * @author josip
 */
public abstract class Tool {

  public ToolHolder toolHolder; // odnosi se na grafičku komponentu kojom je alat prikazan

  public Tool(String name, String icon) {
    toolHolder = new ToolHolder(name, icon);
    toolHolder.setUserData(this);
  }

  public String toString() {
    return toolHolder.getText();
  }

  // eventi za upravljanje alatom
  public abstract void mousePressed(double x, double y, MouseButton button);

  public abstract void mouseReleased(double x, double y, MouseButton button);

  public abstract void mouseMoved(double x, double y);

  public abstract void mouseDragged(double x, double y, MouseButton button);

  /**
   * Poziva se nakon promjene alata kako bi se ispravno završilo crtanje komponente.
   */
  public abstract void cancel();

  /**
   * Kada je alat označen, potrebno je prikazati parametre za upravljanje komponente.
   *
   * @return niz parametara
   */
  public abstract Param[] createParams();

  /**
   * Poziv pri promjeni parametra da se komponenti c isto promijeni vrijednost parametra.
   *
   * @param p koji parametar
   * @param v vrijednost parametra
   */
  public abstract void paramChanged(Param p, Object v);

  /**
   * Koristi alat TLine i njegova komponenta Line za postaviti vrijednosti parametara na početne vrijednosti.
   */
  public void initParamValues() {}

  // metode za komponentu alata
  // pozivaju se samo ako je u pitanju DrawingTool
  public void setComponent(Component c) {}

  public Component getComponent() {
    return null;
  }

}