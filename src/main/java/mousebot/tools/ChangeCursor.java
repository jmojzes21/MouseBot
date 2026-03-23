package mousebot.tools;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;

/**
 * Ako je potrebno prekinuti provjeru utjecaja eventa za određene dijelove te promjeniti pokazivač miša.
 *
 * @author josip
 *
 */
public class ChangeCursor extends Exception {


  public Cursor cursor; // pokazivač miša

  public ChangeCursor(ImageCursor cursor) {
    this.cursor = cursor;
  }

  public ChangeCursor(Cursor cursor) {
    this.cursor = cursor;
  }

  public ChangeCursor() {
    this.cursor = Cursor.HAND;
  }

}