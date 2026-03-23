package mousebot.parameters;

import javafx.scene.Node;

/**
 * Abstraktna klasa za prikaz komponente parametra.
 *
 * @author josip
 */
public abstract class ParamValue<T extends Node> {

  public Object value; // vrijednost
  public T node; // komponenta

  public abstract void init(Param p); // potrebna inicijalizacija

  public abstract void setValue(Object object); // postavlja vrijednost

}