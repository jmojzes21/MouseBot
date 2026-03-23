package mousebot.commands;

import mousebot.components.HandLine;
import mousebot.main.Controller;
import mousebot.tools.THandLine;

public class HandLineAdd implements Command {

  private HandLine handLine;

  public HandLineAdd(HandLine handLine) {
    this.handLine = handLine;
  }

  public void execute() {
    Controller.getProject().currentLayer.components.add(handLine);
  }

  public void undo() {
    Controller.getProject().currentLayer.components.remove(handLine);
    ((THandLine) Controller.getProject().tool.get()).finishHandLine();
  }

  public void redo() {
    Controller.getProject().currentLayer.components.add(handLine);
  }

}