package mousebot.layers;

import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * Omogućuje pohranjivanje neserializabilne klase javafx Color.
 *
 * @author josip
 */
public class SerializableColor implements Serializable {

  private static final long serialVersionUID = 5427540916414077159L;

  private double red, green, blue, opacity;

  public SerializableColor(Color c) {
    setColor(c);
  }

  public void setColor(Color c) {
    red = c.getRed();
    green = c.getGreen();
    blue = c.getBlue();
    opacity = c.getOpacity();
  }

  public Color getFXColor() {
    return Color.color(red, green, blue, opacity);
  }

}