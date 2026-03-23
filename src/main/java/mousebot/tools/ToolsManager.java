package mousebot.tools;


import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import mousebot.commands.ToolSet;
import mousebot.components.Ellipse;
import mousebot.components.Rectangle;
import mousebot.main.Controller;
import mousebot.main.FixedChangeListener;
import mousebot.parameters.Param;

/**
 * Upravitelj alatima.
 *
 * @author josip
 */
public class ToolsManager {

  public ToggleGroup toggleGroup = new ToggleGroup(); // prikazuje koji je alat trenutno označen
  public FixedChangeListener<Toggle> listener;

  public Tool TSELECT;

  public void init() {

    Controller con = Controller.getInstance();

    // inicijalizacija početnog alata
    Tool defaultTool = new TLine();
    con.project.tool.set(defaultTool);
    con.paramsManager.createParams(defaultTool.createParams());
    defaultTool.initParamValues();

    TSELECT = new TSelect();

    // dodavanje grupiranih alata
    con.toolPane.getChildren().addAll(
        new ToolLibrary("toollib.basic")
            .addTool(TSELECT)
            .addTool(defaultTool)
            .addTool(new THandLine())
            .addTool(new TBezier())
        ,
        new ToolLibrary("toollib.shapes")
            .addTool(new TShape<Rectangle>("rect") {
              public Rectangle createInstance(double x, double y) {
                return new Rectangle(x, y);
              }
            })
            .addTool(new TShape<Ellipse>("ellipse", Param.ACCURACY_BEZIER) {
              public Ellipse createInstance(double x, double y) {
                return new Ellipse(x, y);
              }
            })
        ,
        new ToolLibrary("toollib.misc")
            .addTool(new TText())
    );

    listener = new FixedChangeListener<Toggle>() {
      /*
       * ako kliknemo na drugi alat, prvi više neće biti označen
       * no problem nastaje pri klikom na već označeni alat
       *
       * uporabom FixedChangeListener-a problem se rješava
       * tako što nova vrijednost označenog alata (null, jer niti jedan
       * nije označen) ne prođe provjeru pa se postavi stara vrijednost
       */
      public Toggle validate(Toggle current, Toggle newValue) {
        return newValue == null ? current : newValue;
      }

      public void changed(Toggle oldValue, Toggle newValue) {
        // izvrši promjenu
        Controller.getInvoker().execute(new ToolSet((Tool) oldValue.getUserData(), (Tool) newValue.getUserData()));
        Controller.getProject().render();
      }

      public void setValue(Toggle value) {
        toggleGroup.selectToggle(value);
      }
    };
    toggleGroup.selectToggle(defaultTool.toolHolder);
    toggleGroup.selectedToggleProperty().addListener(listener);

  }

}