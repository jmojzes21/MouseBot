package mousebot.commands;

import mousebot.components.ControlRect;
import mousebot.components.ControlRectController;
import mousebot.components.Text;
import mousebot.main.Controller;
import mousebot.main.RectFinder;

public class TextAdd implements Command {

  private Text text;
  private ControlRect cr;

  public TextAdd(Text text) {
    this.text = text;
  }

  public void execute() {
    Controller.getProject().currentLayer.components.add(text);
    RectFinder.getInstance().reset();
    text.processRect();
    cr = new ControlRect(ControlRectController.TCOMPONENT);
    Controller.getProject().cpManager.cr = cr;
  }

  public void undo() {

    Controller.getProject().currentLayer.components.remove(text);
    Controller.getProject().tool.get().setComponent(null);
    cr = Controller.getProject().cpManager.cr;
    Controller.getProject().cpManager.cr = null;
  }

  public void redo() {
    Controller.getProject().currentLayer.components.add(text);
    Controller.getProject().tool.get().setComponent(text);
    Controller.getProject().cpManager.cr = cr;
  }

}