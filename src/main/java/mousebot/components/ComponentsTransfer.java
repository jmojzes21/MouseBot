package mousebot.components;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Koristi se za mogućnost pohranjivanja komponenti u sustavni međuspremnik te tako omogućuje prenošenje komponenti s
 * jednog MouseBot prozora na drugi.
 *
 * @author josip
 */
public class ComponentsTransfer implements Transferable {

  public static final DataFlavor df = new DataFlavor(ArrayList.class, "mousebot-data-flavor");

  private ArrayList<Component> data;

  public ComponentsTransfer(ArrayList<Component> data) {
    this.data = data;
  }

  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[]{df};
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(df);
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      return data;
    } else {
      throw new UnsupportedFlavorException(df);
    }
  }

}