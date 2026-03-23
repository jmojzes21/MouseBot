package mousebot.main;

import java.io.File;
import javafx.beans.binding.Bindings;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mousebot.commands.Invoker;
import mousebot.commands.LayerAdd;
import mousebot.commands.LayerMove;
import mousebot.commands.LayerRemove;
import mousebot.commands.SelAddComponents;
import mousebot.components.Point;
import mousebot.executing.Executor;
import mousebot.layers.Layer;
import mousebot.layers.LayerOptions;
import mousebot.layers.LayersManager;
import mousebot.options.Options;
import mousebot.parameters.ParamsManager;
import mousebot.reorder.Reorder;
import mousebot.tools.ChangeCursor;
import mousebot.tools.ToolsManager;

public class Controller {

  private static Controller controller = new Controller();

  // kontroler koristi singleton design pattern
  public static Controller getInstance() {
    return controller;
  }

  // metode za lakši pristup određenim modulima
  public static Project getProject() {
    return controller.project;
  }

  public static ToolsManager getToolsManager() {
    return controller.toolsManager;
  }

  public static LayersManager getLayersManager() {
    return controller.layersManager;
  }

  public static Invoker getInvoker() {
    if (Executor.isVisible()) {
      return Executor.getExecuteFrame().invoker;
    }
    return controller.project.invoker;
  }

  public static ParamsManager getParamsManager() {
    return controller.paramsManager;
  }

  /**
   * Postavlja pokazivač miša
   *
   * @param c pokazivač miša
   */
  public static void setCursor(Cursor c) {
    controller.canvas.setCursor(c);
  }

  public Stage stage; // MouseBot prozor

  public VBox toolPane; // komponente koja sadrži alate
  public ScrollPane paramsPane; // ploča u kojoj se nalaze parametri

  // izbornici
  public MenuItem menuNew, menuOpen, menuSave, menuSaveAs, menuOptions, menuExit;
  public MenuItem menuUndo, menuRedo, menuCut, menuCopy, menuPaste, menuDelete, menuSelectAll;
  public MenuItem menuHome, menuGridEnable, menuSnapEnable;

  public Pane canvasPane; // ploča u kojoj se nalazi canvas
  public Canvas canvas; // canvas - za prikaz grafike
  public ListView<Layer> layersListView;

  public ToolsManager toolsManager; // upravitelj alata
  public LayersManager layersManager; // upravitelj slojeva
  public ParamsManager paramsManager; // upravitelj parametara
  public Project project; // project
  public Snap snap; // za hvatanje za dio komponente ili rešetku

  // za onemogućavanje korištenja undo-redo tijekom držanja tipke miša
  public int mbPressed = 0;

  private ImageCursor rotateCursorBlack, rotateCursorWhite; // pokazivač miša za vrijeme rotiranja kontrolnog pravokutnika

  /**
   * Inicijaliziranje.
   */
  public void initialize() {

    paramsManager = new ParamsManager();
    toolsManager = new ToolsManager();
    project = new Project();
    project.g = new Graphics(canvas.getGraphicsContext2D());
    layersManager = new LayersManager(layersListView);
    project.lm = layersManager;

    toolsManager.init();
    initMenuBar();
    initCanvas();
    initLayersPane();

    Image rcImg = new Image(MouseBot.getResource("/icons/rotatecursorblack.png"));
    rotateCursorBlack = new ImageCursor(rcImg, rcImg.getWidth() / 2d, rcImg.getHeight() / 2d);
    rotateCursorWhite = new ImageCursor(new Image(MouseBot.getResource("/icons/rotatecursorwhite.png")),
        rcImg.getWidth() / 2d, rcImg.getHeight() / 2d);

    snap = new Snap();

  }

  /**
   * Inicijaliziranje trake inbornika.
   */
  private void initMenuBar() {

    menuNew.setOnAction(e -> {
      onNewProject();
      project.render();
    });

    menuOpen.setOnAction(e -> {
      onOpenProject(null);
      project.render();
    });

    menuSave.setOnAction(e -> {
      onSaveProject();
    });

    menuSaveAs.setOnAction(e -> {
      onSaveProjectAs();
    });

    menuExit.setOnAction(e -> {
      if (onAppExit()) {
        Stage s = (Stage) canvas.getScene().getWindow();
        s.close();
      }
    });

    menuUndo.disableProperty().bind(project.invoker.canUndo.not());
    menuUndo.setOnAction(e -> {
      project.invoker.undo();
      project.render();
    });

    menuRedo.disableProperty().bind(project.invoker.canRedo.not());
    menuRedo.setOnAction(e -> {
      project.invoker.redo();
      project.render();
    });

    menuCut.disableProperty().bind(Bindings.not(project.isToolSelect.and(project.selectionModel.emptySelection.not())));
    menuCut.setOnAction(e -> {
      project.selectionModel.copy();
      project.selectionModel.removeSelectedComponents();
      project.render();
    });

    menuCopy.disableProperty()
        .bind(Bindings.not(project.isToolSelect.and(project.selectionModel.emptySelection.not())));
    menuCopy.setOnAction(e -> {
      project.selectionModel.copy();
      project.render();
    });

    menuPaste.disableProperty().bind(Bindings.not(project.isToolSelect.and(project.selectionModel.emptySelection)));
    menuPaste.setOnAction(e -> {
      project.selectionModel.paste();
      project.render();
    });

    menuDelete.disableProperty()
        .bind(Bindings.not(project.isToolSelect.and(project.selectionModel.emptySelection.not())));
    menuDelete.setOnAction(e -> {
      project.selectionModel.removeSelectedComponents();
      project.render();
    });

    menuSelectAll.disableProperty().bind(Bindings.not(project.isToolSelect.and(project.selectionModel.emptySelection)));
    menuSelectAll.setOnAction(e -> {
      if (project.currentLayer.components.isEmpty()) {return;}
      getInvoker().execute(new SelAddComponents(project.currentLayer));
      project.render();
    });

    menuHome.setOnAction(e -> {
      if (isMouseButtonPressed()) {return;}
      project.g.reset();
      project.render();
    });

    menuOptions.setOnAction(e -> {
      Options opt = new Options();
      opt.show();
    });

    menuGridEnable.setOnAction(e -> {
      boolean enabled = !project.snap.gridEnabled.get();
      project.snap.gridEnabled.set(enabled);
      menuGridEnable.setText(LanguageManager.getString("grid." + (enabled ? "hide" : "show")));
      project.render();
    });

    menuSnapEnable.setOnAction(e -> {
      boolean enabled = !project.snap.enabled.get();
      project.snap.enabled.set(enabled);
      menuSnapEnable.setText(LanguageManager.getString("snap." + (enabled ? "disable" : "enable")));
    });

  }

  /**
   * Inicijaliziranje canvas-a za prikazivanje grafike, tj. komponenti.
   */
  private void initCanvas() {

    // pri promjenu dimenzija prozora potrebno je postaviti
    canvasPane.widthProperty().addListener((o, oldVal, newVal) -> {
      canvas.setWidth(newVal.doubleValue());
      project.width = newVal.doubleValue();
      project.render();
    });
    canvasPane.heightProperty().addListener((o, oldVal, newVal) -> {
      canvas.setHeight(newVal.doubleValue());
      project.height = newVal.doubleValue();
      project.render();
    });

    canvas.setOnMousePressed(e -> {

      mbPressed++;

      Point pos = convertMousePos(e.getX(), e.getY());
      double x = pos.x;
      double y = pos.y;

      project.mousePressed(e.getX(), e.getY(), e.getButton());

      try {
        project.cpManager.mousePressed(x, y, e.getButton());
        project.tool.get().mousePressed(x, y, e.getButton());
      } catch (ChangeCursor exception) {
      }

      project.render();

    });

    canvas.setOnMouseReleased(e -> {

      mbPressed--;

      Point pos = convertMousePos(e.getX(), e.getY());
      double x = pos.x;
      double y = pos.y;

      project.mouseReleased(x, y, e.getButton());
      project.cpManager.mouseReleased(x, y, e.getButton());
      project.tool.get().mouseReleased(x, y, e.getButton());
      project.render();

    });

    canvas.setOnMouseMoved(e -> {

      Point pos = convertMousePos(e.getX(), e.getY());
      double x = pos.x;
      double y = pos.y;

      project.tool.get().mouseMoved(x, y);
      project.mouseLoc.set(x, y);

      try {
        project.cpManager.mouseMoved(x, y);
        setCursor(Cursor.DEFAULT);
      } catch (ChangeCursor exception) {
        setCursor(exception.cursor);
      }

    });

    canvas.setOnMouseDragged(e -> {

      Point pos = convertMousePos(e.getX(), e.getY());
      double x = pos.x;
      double y = pos.y;

      try {
        project.mouseDragged(e.getX(), e.getY(), e.getButton());
        project.cpManager.mouseDragged(x, y, e.getButton());
        project.tool.get().mouseDragged(x, y, e.getButton());
        project.mouseLoc.set(x, y);
      } catch (ChangeCursor ex) {}
    });

    canvas.setOnKeyPressed(e -> {

      if (e.getCode() == KeyCode.SHIFT) {
        project.shiftDown = true;
      } else if (project.isToolSelect.get()) {
        if (e.getCode() == KeyCode.SPACE) {
          project.selectionModel.changeMode();
        }
      }

      project.render();

    });
    canvas.setOnKeyReleased(e -> {
      if (e.getCode() == KeyCode.SHIFT) {
        project.shiftDown = false;
      }
      project.render();
    });

    canvas.setOnScroll(e -> {
      project.g.zoom(tx(e.getX()), ty(e.getY()), e.getDeltaY());
      project.render();
    });

    // omogućava dodavanje slike podloge i za ubacivanje drugog projekta
    canvas.setOnDragOver(e -> {
      if (e.getDragboard().hasFiles() && e.getDragboard().getFiles().size() == 1) {
        File f = e.getDragboard().getFiles().get(0);
        if (f.isFile() && isFileImportSupported(f.getName())) {
          e.acceptTransferModes(TransferMode.COPY);
        }
      }
    });
    canvas.setOnDragDropped(e -> {
      if (e.getDragboard().hasFiles()) {

        if (project.isToolSelect.get()) {
          if (!project.selectionModel.emptySelection.get()) {
            project.selectionModel.clear();
          }
        } else {
          toolsManager.toggleGroup.selectToggle(toolsManager.TSELECT.toolHolder);
        }

        File f = e.getDragboard().getFiles().get(0);
        project.selectionModel.paste(f, tx(e.getX()), ty(e.getY()));
        project.render();
      }
    });

    canvas.setFocusTraversable(true);

  }

  /**
   * Inicijaliziranje ploče u kojoj se nalaze slojevi.
   */
  private void initLayersPane() {

    layersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    ContextMenu cm = new ContextMenu(); // izbornik za trenutni sloj
    layersListView.setContextMenu(cm);

    MenuItem lopt = new MenuItem(LanguageManager.getString("layer.options"));
    lopt.setOnAction(e -> {
      LayerOptions lp = new LayerOptions();
      lp.show();
    });

    MenuItem lr = new MenuItem(LanguageManager.getString("reorder"));
    lr.setOnAction(e -> {
      if (project.currentLayer.components.size() > 0) {
        Reorder r = new Reorder();
        r.showStage();
      }
    });

    MenuItem ladd = new MenuItem(LanguageManager.getString("layer.add"));
    ladd.setOnAction(e -> getInvoker().execute(new LayerAdd()));

    MenuItem lup = new MenuItem(LanguageManager.getString("layer.moveup"));
    lup.setOnAction(e -> {
      int curr = Controller.getLayersManager().listView.getSelectionModel().getSelectedIndex();
      if (curr > 0) {
        getInvoker().execute(new LayerMove(curr, curr - 1));
      }
    });

    MenuItem ldown = new MenuItem(LanguageManager.getString("layer.movedown"));
    ldown.setOnAction(e -> {
      int curr = Controller.getLayersManager().listView.getSelectionModel().getSelectedIndex();
      if (curr < Controller.getLayersManager().layers.size() - 1) {
        getInvoker().execute(new LayerMove(curr, curr + 1));
      }
    });

    MenuItem lrem = new MenuItem(LanguageManager.getString("layer.remove"));
    lrem.setOnAction(e -> {
      if (getLayersManager().layers.size() > 1) {
        getInvoker().execute(new LayerRemove());
      }
    });

    cm.getItems().addAll(lopt, lr, ladd, lup, ldown, lrem);

  }

  /**
   * Prilagođavanje koordinata pokazivača miša za trenutnu translaciju i zoom. Ako je hvatanje omogućeno, koordinate
   * miša pokušat će se uhvatiti za neku kontrolnu točku komponente i za rešetku.
   *
   * @param x
   * @param y
   * @return nova x i y kordinata
   */
  private Point convertMousePos(double x, double y) {

    Point pos = new Point(tx(x), ty(y));

    if (project.snap.enabled.get()) {
      project.snap.snap(pos);
    }

    return pos;
  }

  // tx i ty su metode koje prilagođavaju x i y za trenutnu translaciju i zoom
  private double tx(double x) {
    return (x - project.g.translation.x) * project.g.scaleI();
  }

  private double ty(double y) {
    return (y - project.g.translation.y) * project.g.scaleI();
  }

  /**
   * @return objekt Graphics
   */
  public static Graphics getGraphics() {
    if (Executor.isVisible()) {
      return Executor.getExecuteFrame().g;
    }
    return controller.project.g;
  }


  /**
   * Poziva se pri zahtjevu za zatvaranje aplikacije. U slučaju da je potrebno spremiti projekt, pošalje upit.
   *
   * @return true ako se aplikacija može zatvoriti, inače false
   * @see closeProjectQuery
   */
  public boolean onAppExit() {
    return closeProjectQuery();
  }

  /**
   * Kreira novi projekt.
   */
  private void onNewProject() {

    if (!closeProjectQuery()) {return;}

    project.clearOrOpen(null, 0);
    project.unsaved = false;
    updateTitle();
  }

  /**
   * Otvara projekt iz datoteke.
   *
   * @param file
   */
  public void onOpenProject(File file) {

    if (!closeProjectQuery()) {return;}

    if (file == null) {
      file = Dialogs.openFile();
      if (file == null) {return;}
    }
    if (!project.openProject(file)) {
      return;
    }
    project.file = file;
    project.unsaved = false;
    updateTitle();
  }

  /**
   * Sprema projekt.
   *
   * @return true ako je projekt spremljen, inače false
   */
  private boolean onSaveProject() {
    if (project.file == null) {
      File file = Dialogs.saveFile();
      if (file == null) {return false;}
      project.file = file;
      updateTitle();
    }
    if (!project.saveProject()) {
      project.file = null;
      project.unsaved = true;
      updateTitle();
      return false;
    }
    project.unsaved = false;
    updateTitle();
    return true;
  }

  /**
   * Sprema projekt, ali pošalje upit za novu lokaciju.
   */
  private void onSaveProjectAs() {
    File file = Dialogs.saveFile();
    if (file != null) {
      project.file = file;
      if (!project.saveProject()) {
        project.file = null;
        project.unsaved = true;
        updateTitle();
      }
      project.unsaved = false;
      updateTitle();
    }
  }

  /**
   * Poziva se pri zahtjevu za zatvaranje projekta. U slučaju da je potrebno spremiti projekt, pošalje upit. Ako ne
   * treba spremiti projekt, vraća true.
   *
   * @return true ako se projekt može zatvoriti, inače false
   */
  private boolean closeProjectQuery() {
    if (!project.unsaved) {
      return true;
    }
    ButtonType bt = Dialogs.show(AlertType.CONFIRMATION, "dialogs.saveprojectquery", Dialogs.SAVE, Dialogs.DONT_SAVE,
        Dialogs.CANCEL);
    if (bt == Dialogs.SAVE) {
      return onSaveProject();
    }
    return bt == Dialogs.DONT_SAVE;
  }

  public boolean isShiftDown() {
    if (Executor.isVisible()) {
      return Executor.getExecuteFrame().shiftDown;
    }
    return project.shiftDown;
  }

  public ImageCursor getRotateCursor() {
    return Executor.isVisible() ? rotateCursorBlack : rotateCursorWhite;
  }

  /**
   * Ažirira naslov pri spremanju projekta ili promjeni.
   */
  public void updateTitle() {
    if (project.file != null) {
      stage.setTitle(MouseBot.APP_TITLE + " - " + (project.unsaved ? "*" : "") + project.file.getAbsolutePath());
    } else {
      stage.setTitle((project.unsaved ? "*" : "") + MouseBot.APP_TITLE);
    }
  }

  private static final String[] SUPPORTED_EXTENSIONS = {".mousebot", ".png", ".jpg"};

  /**
   * Može li određena datoteka biti ubačena u aplikaciju.
   *
   * @param name ime datoteke s nastavkom
   * @return true ako je nastavak datoteke podržan, inače false
   */
  private boolean isFileImportSupported(String name) {
    for (int i = 0; i < SUPPORTED_EXTENSIONS.length; i++) {
      if (name.endsWith(SUPPORTED_EXTENSIONS[i])) {
        return true;
      }
    }
    return false;
  }

  public boolean isMouseButtonPressed() {
    return mbPressed != 0;
  }

  public boolean notMouseButtonPressed() {
    return mbPressed == 0;
  }

}