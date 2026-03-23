package mousebot.layers;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import mousebot.components.Component;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.LanguageManager;

public class Layer {

  public StringProperty name; // ime sloja
  public SerializableColor color = new SerializableColor(Color.WHITE); // boja
  public BooleanProperty enabled; // omogućen ili ne

  public ArrayList<Component> components; // sve komponente koje mu pripadaju

  public Layer() {
    this(Controller.getLayersManager().layers.size() + 1);
  }

  public Layer(int i) {
    name = new SimpleStringProperty(LanguageManager.getString("layer.name") + " " + i);
    components = new ArrayList<>();
    enabled = new SimpleBooleanProperty(true);
    enabled.addListener((o, oldVal, newVal) -> Controller.getProject().render());
  }

  public Layer(String name, SerializableColor color, boolean enabled, ArrayList<Component> components) {
    this.name = new SimpleStringProperty(name);
    this.components = components;
    this.color = color;
    this.enabled = new SimpleBooleanProperty(enabled);
    this.enabled.addListener((o, oldVal, newVal) -> Controller.getProject().render());
    this.components.forEach(c -> {
      c.layer = this;
    });
  }

  public void render(Graphics g) {

    if (!enabled.get()) {return;}

    g.gc.setStroke(color.getFXColor());
    for (int i = 0; i < components.size(); i++) {
      components.get(i).preRender(g);
    }

  }
	
	/*public void render(Graphics g, boolean moved[]) {
		for(int i = 0; i < components.size(); i++) {
			g.gc.setStroke(moved[i] ? Color.GREEN : Color.RED);
			components.get(i).preRender(g);
		}
	}*/

  public boolean isEnabled() {
    return enabled.get();
  }

}