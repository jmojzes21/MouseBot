package mousebot.commands;

import java.util.ArrayList;
import mousebot.components.Component;
import mousebot.layers.Layer;

public class Reorder implements Command {

  private Layer layer;
  private int pos[];

  public Reorder(Layer layer, int pos[]) {
    this.layer = layer;
    this.pos = pos;
  }

  public void execute() {

    ArrayList<Component> components = layer.components;
    Component backup[] = new Component[components.size()];

//		System.out.print("before reorder: "); HASH_ARRAY(components);

    for (int i = 0; i < backup.length; i++) {
      backup[i] = components.get(i);
    }

    for (int i = 0; i < components.size(); i++) {
      components.set(i, backup[pos[i]]);
    }

//		System.out.print("after reorder:  "); HASH_ARRAY(components);

  }

  public void undo() {

    ArrayList<Component> components = layer.components;
    Component backup[] = new Component[components.size()];

    for (int i = 0; i < backup.length; i++) {
      backup[i] = components.get(i);
    }

    for (int i = 0; i < components.size(); i++) {
      Component e = null;
      for (int j = 0; j < pos.length; j++) {
        if (pos[j] == i) {
          e = backup[j];
          break;
        }
      }
      components.set(i, e);
    }

//		System.out.print("after undo:     "); HASH_ARRAY(components);

  }

  public void redo() {
    execute();
  }
	
	/*
	private static void HASH_ARRAY(ArrayList<Component> components) {
		
		String mystr = "gethash=";
		
		for(int i = 0; i < components.size(); i++) {
			String s = components.get(i).toString();
			mystr += Integer.toString(i);
			mystr += ":";
			mystr += s;
			mystr += ";";
		}
		
		//System.out.println(mystr);
		try {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte dg[] = md.digest(mystr.getBytes());
		String f = new String(Base64.getEncoder().encode(dg));
		System.out.println(f);
		}catch(Exception e) {}
	}
	*/
}