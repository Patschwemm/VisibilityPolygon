package geometry;

import javafx.scene.input.MouseEvent;

public class EventHandler implements javafx.event.EventHandler<MouseEvent> {

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (!GUI.polygon.getPolygonDrawn() && mouseEvent.getSceneX() <= GUI.primary.getWidth()-315 ) {
            //create point if point is in field
            GUI.polygon.addNode(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        }
    }
}

