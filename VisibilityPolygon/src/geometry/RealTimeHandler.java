package geometry;

import javafx.scene.input.MouseEvent;

public class RealTimeHandler implements javafx.event.EventHandler<MouseEvent> {



    @Override
    public void handle(MouseEvent mouseEvent) {


        if (EventHandler.p_moving == true ){
            GUI.polygon.move_p(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                if( Settings.get().get_vis_p_Status().isSelected()){
                    GUI.vis_p.deleteVisPolygon();
                    GUI.vis_p = new VisPolygon();
                }
        }
//
//        System.out.println("clicked");
//        if(GUI.polygon.getPolygonDrawn() && GUI.polygon.is_p_set() && mouseEvent.getSceneX() <= GUI.primary.getWidth() - 315 ){
//            if (GUI.polygon.inRange(GUI.polygon.createNode(mouseEvent.getSceneX(), mouseEvent.getSceneY()),GUI.polygon.get_p()) <15){
//                GUI.polygon.move_p(mouseEvent.getSceneX(), mouseEvent.getSceneY());
//                if( Settings.get().get_vis_p_Status().isSelected()){
//                    GUI.vis_p.deleteVisPolygon();
//                    GUI.vis_p = new VisPolygon();
//                }
//            }
//        }
    }
}

