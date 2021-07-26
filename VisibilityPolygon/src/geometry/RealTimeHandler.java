package geometry;

import javafx.scene.input.MouseEvent;

public class RealTimeHandler implements javafx.event.EventHandler<MouseEvent> {


    //function to real time click the point and drag it through the given polygon
    @Override
    public void handle(MouseEvent mouseEvent) {

        if (EventHandler.p_moving == true ){
            GUI.polygon.move_q(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                if( Settings.get().get_vis_q_Status().isSelected()){
                    if(GUI.vis_q != null){
                        GUI.vis_q.deleteVisPolygon();
                    }
                    if(GUI.betavis_q_rec != null){
                        GUI.betavis_q_rec.deleteBetaVisPolygon();
                    }
                    Settings.setBetaZero();
                    try{
                        GUI.vis_q = new VisPolygon();
                    } catch (Exception e){
                        System.out.println("Query Point q is out of bounds");
                    }

                }
        }
    }
}

