package geometry;

import javafx.scene.input.MouseEvent;

public class EventHandler implements javafx.event.EventHandler<MouseEvent> {

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (GUI.polygon.getPolygonDrawn() && !GUI.polygon.is_p_set() && mouseEvent.getSceneX() <= GUI.primary.getWidth() - 315) {
            // Polygon drawn, draw Point from which visibility is to be calculated from
            GUI.polygon.set_p(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        }

        if (!GUI.polygon.getPolygonDrawn() && mouseEvent.getSceneX() <= GUI.primary.getWidth() - 315) {
            //create point if point is in field
            GUI.polygon.addNode(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        }

//        System.out.println(" Click X: "+ mouseEvent.getSceneX() + " Click Y: "+ mouseEvent.getSceneY());


    }
}

