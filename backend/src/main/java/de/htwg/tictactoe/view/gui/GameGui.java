package de.htwg.tictactoe.view.gui;

import de.htwg.tictactoe.controller.IController;
import de.htwg.tictactoe.model.impl.Messages;
import de.htwg.util.observer.IObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;

/**
 * @author Youssef Idelhoussain
 */
public class GameGui extends Application implements IObserver {

   double mousePosX;
   double mousePosY;
   double mouseOldX;
   double mouseOldY;
   double mouseDeltaX;
   double mouseDeltaY;
   final Cam camOffset = new Cam();
   final Cam cam = new Cam();
   private static IController controller;
   final Shear shear = new Shear();
   private static StatusPanel statusPanel;
   private static final int GRIDS_SIZE = 3;
   private GridPanel[] grids;
   Scene scene;
   Stage stage;
   Group appGroup;

   public GameGui() {
   }

   public void setController(IController controller) {
      GameGui.controller = controller;
   }

   public void init(Stage stage) {
      this.stage = stage;
      appGroup = new Group();
      GameGui.statusPanel = new StatusPanel();
      GameGui.statusPanel.setText(Messages.MOVEMENT + controller.getStatus());
   }

   @Override
   public void start(final Stage stage) {
      init(stage);
      controller.addObserver(this);
      stage.setTitle(Messages.TITLE);
      camOffset.getChildren().add(cam);
      appGroup.getChildren().addAll(camOffset, statusPanel);
      resetCam();
      scene = new Scene(appGroup, 800, 600, true);
      scene.setFill(new RadialGradient(225, 0.85, 300, 300, 500, false,
            CycleMethod.NO_CYCLE, new Stop[]
            {new Stop(0f, Color.BLUE),
                  new Stop(1f, Color.LIGHTBLUE)}));
      scene.setCamera(new PerspectiveCamera());
      cam.setScaleX(0.5);
      cam.setScaleY(0.5);

      cam.getChildren().addAll(setGrids());

      frameCam(stage, scene);

      /**
       * Set on key pressed
       */
      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         public void handle(KeyEvent ke) {
            if (KeyCode.X.equals(ke.getCode())) {
               controller.exit();
            }
            if (KeyCode.R.equals(ke.getCode())) {
               controller.reset();
            }
            if (KeyCode.D.equals(ke.getCode())) {
               cam.getRy().setAngle(cam.getRy().getAngle() - 5);
            }
            if (KeyCode.A.equals(ke.getCode())) {
               cam.getRy().setAngle(cam.getRy().getAngle() + 5);
            }
            if (KeyCode.W.equals(ke.getCode()) && cam.getRx().getAngle() > -8) {
               cam.getRx().setAngle(cam.getRx().getAngle() - 5);
            }
            if (KeyCode.S.equals(ke.getCode()) && cam.getRx().getAngle() < 47) {
               cam.getRx().setAngle(cam.getRx().getAngle() + 5);
            }
         }
      });
      stage.setScene(scene);
      stage.show();
   }

   /**
    * set Grids
    *
    * @return
    */
   public Group setGrids() {
      Group gridsGroup = new Group();
      gridsGroup.getTransforms().add(shear);
      gridsGroup.setDepthTest(DepthTest.ENABLE);

      grids = new GridPanel[GRIDS_SIZE];
      int position = -30;

      for (int i = 0; i < GRIDS_SIZE; i++) {
         grids[i] = new GridPanel(controller, 1.0, 1.0, i);
         grids[i].setTranslateX(50);
         grids[i].setTranslateZ(300);
         grids[i].setTranslateY(position);
         grids[i].setScaleX(50);
         grids[i].setScaleZ(50);
         grids[i].setScaleY(5);
         position += 15;
      }
      gridsGroup.getChildren().addAll(grids[2], grids[1], grids[0]);
      gridsGroup.setScaleX(2.5);
      gridsGroup.setScaleY(2.5);
      gridsGroup.setScaleZ(2.5);
      return gridsGroup;
   }

   /**
    * Frame Cam
    *
    * @param stage
    * @param scene
    */
   public void frameCam(final Stage stage, final Scene scene) {
      setCamOffset(camOffset, scene);
      setCamPivot(cam);
      setCamTranslate(cam);
      setCamScale(cam, scene);

   }

   /**
    * Cam offset
    *
    * @param camOffset
    * @param scene
    */
   public void setCamOffset(final Cam camOffset, final Scene scene) {
      double width = scene.getWidth();
      double height = scene.getHeight();
      camOffset.getT().setX(width / 2.0);
      camOffset.getT().setY(height / 2.0);
   }

   /**
    * Cam scale
    *
    * @param cam
    * @param scene
    */
   public void setCamScale(final Cam cam, final Scene scene) {
      final Bounds bounds = cam.getBoundsInLocal();
      double width = scene.getWidth();
      double height = scene.getHeight();

      double scaleFactor = 1.0;
      double scaleFactorY = 1.0;
      double scaleFactorX = 1.0;
      if (bounds.getWidth() > 0.0001) {
         scaleFactorX = width / bounds.getWidth();
      }
      if (bounds.getHeight() > 0.0001) {
         scaleFactorY = height / bounds.getHeight();
      }
      if (scaleFactorX > scaleFactorY) {
         scaleFactor = scaleFactorY;
      } else {
         scaleFactor = scaleFactorX;
      }
      cam.getS().setX(scaleFactor);
      cam.getS().setY(scaleFactor);
      cam.getS().setZ(scaleFactor);
   }

   /**
    * Cam pivot
    *
    * @param cam
    */
   public void setCamPivot(final Cam cam) {
      final Bounds bounds = cam.getBoundsInLocal();
      final double pivotX = bounds.getMinX() + bounds.getWidth() / 2;
      final double pivotY = bounds.getMinY() + bounds.getHeight() / 2;
      final double pivotZ = bounds.getMinZ() + bounds.getDepth() / 2;
      cam.getP().setX(pivotX);
      cam.getP().setY(pivotY);
      cam.getP().setZ(pivotZ);
      cam.getIp().setX(-pivotX);
      cam.getIp().setY(-pivotY);
      cam.getIp().setZ(-pivotZ);
   }

   /**
    * Cam Translate
    *
    * @param cam
    */
   public void setCamTranslate(final Cam cam) {
      final Bounds bounds = cam.getBoundsInLocal();
      final double pivotX = bounds.getMinX() + bounds.getWidth() / 2;
      final double pivotY = bounds.getMinY() + bounds.getHeight() / 2;
      cam.getT().setX(-pivotX);
      cam.getT().setY(-pivotY);
   }

   /**
    * Reset Cam
    */
   public void resetCam() {
      cam.getT().setX(0.0);
      cam.getT().setY(0.0);
      cam.getT().setZ(0.0);
      cam.getRx().setAngle(27.0);
      cam.getRy().setAngle(-92.0);
      cam.getRz().setAngle(0.0);
      cam.getS().setX(1.25);
      cam.getS().setY(1.25);
      cam.getS().setZ(1.25);

      setCamPivot(cam);
   }

   @Override
   public void update() {
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            cam.getChildren().addAll(setGrids());
            statusPanel.setText(Messages.MOVEMENT + controller.getStatus());
         }
      });
   }
}
