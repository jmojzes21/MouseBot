package mousebot.commands;

import java.util.ArrayList;
import mousebot.components.Component;
import mousebot.components.Point;
import mousebot.components.Text;
import mousebot.main.Controller;
import mousebot.main.RectFinder;

public class Paste implements Command {

  private ArrayList<Component> data;

  public Paste(ArrayList<Component> data, Point p) {
    this.data = data;
    RectFinder.getInstance().reset();
    data.forEach(c -> {
      c.layer = Controller.getProject().currentLayer;
      c.processRect();
    });
    Point start = RectFinder.getInstance().getStart();
    double dx = p.x - start.x;
    double dy = p.y - start.y;
    data.forEach(c -> {
      if (c.cp == null) {
        Text text = (Text) c;
        for (int j = 0; j < text.subcomponents.size(); j++) {
          Point cp[] = text.subcomponents.get(j);
          for (int i = 0; i < cp.length; i++) {
            cp[i].x += dx;
            cp[i].y += dy;
          }
        }
      } else {
        for (int i = 0; i < c.cp.length; i++) {
          c.cp[i].x += dx;
          c.cp[i].y += dy;
        }
      }
    });
  }

  public Paste(ArrayList<Component> data) {
    this(data, Controller.getProject().getMouseLoc());
  }

  public void execute() {
    data.forEach(Controller.getProject().currentLayer.components::add);
  }

  public void undo() {
    data.forEach(Controller.getProject().currentLayer.components::remove);
  }

  public void redo() {
    execute();
  }

}