package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Stack;

public class DrawPolygon extends Application {

    public static Stack<Circle> P = new Stack<Circle>();
    public static ArrayList<Circle> CList = new ArrayList<>(50);
    private boolean polygondrawn = false;


    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Group pointscene = new Group();
        Group polygonscene = new Group();
        primaryStage.setTitle("Sichtbarkeitspolygon Applet");


        Polygon polygon = new Polygon();

        //polygon.getPoints().add()

       root.getChildren().add(polygonscene);

        Scene scene = new Scene(root, 1200, 720, Color.LIGHTGRAY);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("mouse click detected! " + mouseEvent.getSource());
                System.out.println("mouse x " + mouseEvent.getSceneX());
                System.out.println("mouse y " + mouseEvent.getSceneY());

                //create new Point as Polygon node
                Circle point = new Circle();
                point.setCenterX(mouseEvent.getSceneX());
                point.setCenterY(mouseEvent.getSceneY());
                point.setRadius(6.5);
                CList.add(point);
                pointscene.getChildren().add(point);


            }
        });
        root.getChildren().add(pointscene);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void drawpolgon (){
        polygondrawn = true;
    }
}
