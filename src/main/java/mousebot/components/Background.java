package mousebot.components;

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;
import mousebot.parameters.Param;

/**
 * Komponenta koja se odnosi na sliku podloge.
 *
 * @author josip
 */
public class Background extends Component {

  private static final long serialVersionUID = -5780861795323240519L;

  public SerializableImage img;

  public Background() {
    super(2, Param.INVERTED_IMAGE, Param.TRANSPARENCY);
  }

  public Background(Image img) {
    this();
    cp[0] = new Point(0, 0);
    cp[1] = new Point(img.getWidth(null), img.getHeight(null));
    setParamValue(Param.INVERTED_IMAGE, false);
    this.img = new SerializableImage(SwingFXUtils.toFXImage((BufferedImage) img, null));
  }

  public void render(Graphics g) {

    Point start = new Point(cp[0].x * g.scale() + g.translation.x, cp[0].y * g.scale() + g.translation.y);
    Point end = new Point(cp[1].x * g.scale() + g.translation.x, cp[1].y * g.scale() + g.translation.y);

    g.gc.setGlobalAlpha((double) getParamValue(Param.TRANSPARENCY));
    g.gc.drawImage(img.get(), start.x, start.y, end.x - start.x, end.y - start.y);
    g.gc.setGlobalAlpha(1);

  }

  public void processRect() {
    for (int i = 0; i < cp.length; i++) {
      RectFinder.getInstance().process(cp);
    }
  }

  public boolean intersects(Rectangle2D rect) {
    return false;
  }

  public void execute() throws Exception {}

  public void setParamValue(Param p, Object v) {
    if (paramValues.containsKey(p)) {
      paramValues.put(p, v);
    }

    if (p.equals(Param.INVERTED_IMAGE) && img != null) {
      img.invert();
    }

  }

  public Point getSnapped(Point p) {
    return null;
  }

}