package geometry;

import javafx.scene.shape.Circle;

public class Point extends Circle {

    private Point PointLinked = null;

    public Point(){
        super();
    }

    public Point getPointLinked(){
        return this.PointLinked;
    }

    public void setPointLinked(Point Pointlinked){
        this.PointLinked = Pointlinked;
    }

}
