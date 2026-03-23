package mousebot.commands;

/**
 * Interface s osnovnim metodama za undo-redo sustav.
 *
 * @author josip
 */
public interface Command {

  public void execute(); // izvrši

  public void undo(); // poništi

  public void redo(); // ponovi

  /**
   * Nakon što se naredba izvrši, potrebno je provjeriti treba li postaviti projekt nespremljenim zbog učinjenih
   * promjena.
   *
   * @return true ako projekt treba postaviti nespremljenim, inače false
   */
  public default boolean shouldUnsave() {
    return true;
  }

}