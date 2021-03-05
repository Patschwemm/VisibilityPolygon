package geometry;

import javafx.scene.input.MouseEvent;

public class EventHandler implements javafx.event.EventHandler<MouseEvent> {

    public static boolean p_moving = false;
    public static int clicks=0;

    @Override
    public void handle(MouseEvent mouseEvent) {

        System.out.println("Mouse X: "+ mouseEvent.getSceneX());
        System.out.println("Mouse Y: "+ mouseEvent.getSceneY()+ " \n");
        if (GUI.polygon.getPolygonDrawn() && !GUI.polygon.is_p_set() && mouseEvent.getSceneX() <= GUI.primary.getWidth() - 315) {
            // Polygon drawn, draw Point from which visibility is to be calculated from
            GUI.polygon.set_p(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        }

        if (!GUI.polygon.getPolygonDrawn() && mouseEvent.getSceneX() <= GUI.primary.getWidth() - 315) {
            //create point if point is in field
            GUI.polygon.addNode(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        }

        if (GUI.polygon.getPolygonDrawn() && GUI.polygon.is_p_set()
                && mouseEvent.getSceneX() <= GUI.primary.getWidth() - 315) {
            if ( GUI.polygon.inRange(GUI.polygon.createNode(mouseEvent.getSceneX(), mouseEvent.getSceneY()),GUI.polygon.get_p()) <15){
                clicks++;
                if (clicks >= 2){
                    p_moving = !p_moving;
                }
                System.out.println("p_moving");
            }
        }

    }
}

