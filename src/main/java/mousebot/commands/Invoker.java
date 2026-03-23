package mousebot.commands;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import mousebot.executing.Executor;
import mousebot.main.Controller;

/**
 * Invoker je objekt koji upravlja naredbama te tako omogućuje undo-redo sustav.
 *
 * @author josip
 */
public class Invoker {

  private ArrayList<Command> commands; // lista svih naredbi
  private int current; // indeks određene naredbe u listi

  public BooleanProperty canUndo = new SimpleBooleanProperty(), canRedo = new SimpleBooleanProperty();

  public Invoker() {
    commands = new ArrayList<>();
    reset();
  }

  /**
   * Naredba će se prvo dodati u listu tako da se kasnije može poništiti ili ponoviti, a zatim će se izvršiti.
   * Korištenjem metode poništi, naredba se ne mora dodati na kraj liste. U tom slučaju, sve naredbe nakon novododane
   * naredbe će se ukloniti iz liste.
   *
   * @param command naredba koja će se dodati i izvršiti
   */
  public void execute(Command command) {

    if (!Controller.getProject().unsaved && command.shouldUnsave() && !Executor.isVisible()) {
      Controller.getProject().unsaved = true;
      Controller.getInstance().updateTitle();
    }

    current++;

    while (commands.size() > current) {
      commands.remove(commands.size() - 1);
    }

    commands.add(command);
    command.execute();

    canUndo.set(true);
    canRedo.set(false);
  }

  /**
   * Poništavanje prethodne naredbe.
   */
  public void undo() {

    if (Controller.getInstance().isMouseButtonPressed()) {
      return;
    }

    if (current < 0) {return;}
    commands.get(current--).undo();

    canUndo.set(current >= 0);
    canRedo.set(true);
  }

  /**
   * Ponavljanje sljedeće naredbe.
   */
  public void redo() {

    if (Controller.getInstance().isMouseButtonPressed()) {
      return;
    }

    if (++current == commands.size()) {
      current--;
      return;
    }
    commands.get(current).redo();

    canUndo.set(true);
    canRedo.set(current < commands.size() - 1);
  }

  /**
   *
   * @return prethodna nareda ili null ako smo poništili sve naredbe
   */
  public Command getNextUndo() {
    if (current < 0) {return null;}
    return commands.get(current);
  }

  /**
   * Ponovno postavlja invoker te uklanja sve naredbe. Koristi se kod kreiranja novog ili otvaranju projekta.
   */
  public void reset() {
    commands.clear();
    current = -1;
    canUndo.set(false);
    canRedo.set(false);
  }

}