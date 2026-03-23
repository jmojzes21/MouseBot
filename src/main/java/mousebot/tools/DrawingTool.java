package mousebot.tools;

import mousebot.components.Component;

/*
 * Razlika između Tool-a je što DrawingTool dodaje varijablu c, tj. komponentu kojom
 * alat upravlja.
 */
public abstract class DrawingTool<C extends Component> extends Tool {

  public C c;

  public DrawingTool(String name, String icon) {
    super(name, icon);
  }

  @SuppressWarnings("unchecked")
  public void setComponent(Component c) {
    this.c = (C) c;
  }

  public Component getComponent() {
    return c;
  }

}