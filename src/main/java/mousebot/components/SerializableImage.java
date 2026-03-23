package mousebot.components;

import java.io.Serializable;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Omogućuje pohranjivanje neserializabilne klase javafx Image.
 *
 * @author josip
 */
public class SerializableImage implements Serializable {

  private static final long serialVersionUID = 1956916548118205353L;

  private transient Image img;
  private int[][] data;

  public SerializableImage(Image img) {
    PixelReader pr = img.getPixelReader();
    data = new int[(int) img.getWidth()][(int) img.getHeight()];
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        data[x][y] = pr.getArgb(x, y);
      }
    }
    this.img = img;
  }

  public Image get() {

    if (img == null) {

      WritableImage wi = new WritableImage(data.length, data[0].length);
      PixelWriter pw = wi.getPixelWriter();
      for (int y = 0; y < data[0].length; y++) {
        for (int x = 0; x < data.length; x++) {
          pw.setArgb(x, y, data[x][y]);
        }
      }

      img = wi;

    }

    return img;
  }

  public SerializableImage clone() {
    return new SerializableImage(get());
  }

  public void invert() {
    WritableImage wi = new WritableImage(data.length, data[0].length);
    PixelWriter pw = wi.getPixelWriter();
    for (int y = 0; y < data[0].length; y++) {
      for (int x = 0; x < data.length; x++) {
        int rgb = data[x][y] ^ 0x00ffffff;
        data[x][y] = rgb;
        pw.setArgb(x, y, rgb);
      }
    }
    img = wi;
  }

}