package mousebot.commands;

import java.util.ArrayList;
import mousebot.components.Component;
import mousebot.components.SelectionModel;
import mousebot.main.Controller;

public class SelRemove implements Command {

  private SelectionModel sm;
  private ArrayList<Component> backup = new ArrayList<>();

  public SelRemove() {
    sm = Controller.getProject().selectionModel;
    sm.selected.forEach(backup::add);
  }

  public void execute() {
    backup.forEach(c -> c.layer.components.remove(c));
    Controller.getInvoker().execute(new SelClear());
  }

  public void undo() {
    backup.forEach(c -> c.layer.components.add(c));
  }

  public void redo() {
    backup.forEach(c -> c.layer.components.remove(c));
    Controller.getInvoker().redo();
  }

}