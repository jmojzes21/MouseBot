package mousebot.commands;

import mousebot.components.ControlRect;
import mousebot.components.Text;
import mousebot.main.Controller;

public class TextCancel implements Command {

  private Text text;
  private ControlRect cr;

  public TextCancel(Text text) {
    this.text = text;
  }

  public void execute() {
    Controller.getProject().tool.get().setComponent(null);
    cr = Controller.getProject().cpManager.cr;
    Controller.getProject().cpManager.cr = null;
  }

  public void undo() {
    Controller.getProject().tool.get().setComponent(text);
    Controller.getProject().cpManager.cr = cr;
  }

  public void redo() {
    execute();
  }

}