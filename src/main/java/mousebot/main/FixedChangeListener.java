package mousebot.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * JavaFx Property koristi se u povezivanju, tj. izražavanje relacija između objekata. Promjena napravljen jednom
 * objektu bit će napravljena i drugom.
 * <p>
 * Objektu Property može se postaviti event koji se poziva nakon izvršene promjene vrijednosti, ali nema mogućnost za
 * poziv prije promjene vrijednosti koju program zahtjeva. Također, zbog undo-redo sustava, ponekad je potrebno i
 * privremeno onemogućiti izvšenja evenata.
 * <p>
 * Ova klasa transformira klasičan način rada Property objekta na način da se prvo pozove metoda validate(provjera
 * vrijednosti) te ako se vrijednost može promijeniti, promjeni se i pozove metoda changed.
 *
 * @author josip
 */
public abstract class FixedChangeListener<T> implements ChangeListener<T> {

  private boolean enabled = true;

  /**
   * Metodu koju pozove sam objekat Property.
   */
  public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

    if (!enabled) {return;}

    enabled = false;
    T value = validate(oldValue, newValue);
    if (value == oldValue) {
      setValue(value);
    }
    enabled = true;

    if (value == newValue) {
      changed(oldValue, newValue);
    }

  }

  /**
   * Provjera nove vrijednosti.
   *
   * @param current  trenutna vrijednost
   * @param newValue nova vrijednost
   * @return koja vrijednost će se postaviti
   */
  public abstract T validate(T current, T newValue);

  /**
   * Nakon što je vrijednost objekta promijenjena.
   *
   * @param oldValue stara vrijednost
   * @param newValue nova vrijednost
   */
  public abstract void changed(T oldValue, T newValue);

  /**
   * Pomoćna metoda za postavljanje vrijednosti objekta Property.
   *
   * @param value vrijednost
   */
  public abstract void setValue(T value);

  // za o(ne)mogućavanje pozivanje evenata validate i changed
  public void enable() {
    enabled = true;
  }

  public void disable() {
    enabled = false;
  }

}