package geometry;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import java.util.ArrayList;
import java.util.Stack;

public class Polygon {

    private Stack<Circle> P = new Stack<Circle>();
    private ArrayList<Circle> PointList = new ArrayList<>(50);
    private ArrayList<Path> EdgeList = new ArrayList<>(50);
    private boolean isPolygonDrawn = false;

    public void addNode(double x, double y) {

        Circle point = this.createNode(x, y);

        if (PointList.size() != 0 && this.inRange(PointList.get(0), point)) {
            if (PointList.size() <= 2) {
                System.out.println("tried to draw Polygon with 2 or less nodes");
            } else {
                Path edge = this.createEdge(PointList.get(PointList.size() - 1).getCenterX(),
                        PointList.get(PointList.size() - 1).getCenterY(),
                        PointList.get(0).getCenterX(),
                        PointList.get(0).getCenterY());
                EdgeList.add(edge);
                GUI.edgescene.getChildren().add(edge);
                this.setPolygonDrawn();
                System.out.println("Polygon drawn");
            }
        } else {
            PointList.add(point);

            //add to Point Scene
            GUI.pointscene.getChildren().add(point);

            //connect edges between nodes
            if (PointList.size() != 1) {
                Path edge = this.createEdge(PointList.get(PointList.indexOf(point) - 1).getCenterX(),
                        PointList.get(PointList.indexOf(point) - 1).getCenterY(),
                        PointList.get(PointList.indexOf(point)).getCenterX(),
                        PointList.get(PointList.indexOf(point)).getCenterY());
                EdgeList.add(edge);
                GUI.edgescene.getChildren().add(edge);
            }
        }
        System.out.println("Edge Length: "+ EdgeList.size());
        System.out.println("Point Length: "+ PointList.size());
    }

    public Circle createNode(double x, double y) {
        //create new Point as Polygon node
        Circle point = new Circle();
        point.toFront();
        point.setCenterX(x);
        point.setCenterY(y);
        point.setRadius(5.5);
        point.setStroke(Color.DARKRED);
        point.setFill(Color.DARKRED);
        return point;
    }

    public Path createEdge(double x1, double y1, double x2, double y2) {
        Path edge = new Path();
        edge.getElements().add(new MoveTo(x1, y1));
        edge.getElements().add(new LineTo(x2, y2));
        edge.setStrokeWidth(4);
        edge.setStroke(Color.DARKRED);
        edge.toBack();
        return edge;
    }


    public boolean inRange(Circle c1, Circle c2) {
        System.out.println("Euclid distance: " + (Math.sqrt((c1.getCenterX() - c2.getCenterX()) * (c1.getCenterX() - c2.getCenterX())
                + (c1.getCenterY() - c2.getCenterY()) * (c1.getCenterY() - c2.getCenterY()))));

        return ((Math.sqrt((c1.getCenterX() - c2.getCenterX()) * (c1.getCenterX() - c2.getCenterX())
                + (c1.getCenterY() - c2.getCenterY()) * (c1.getCenterY() - c2.getCenterY()))) <= 15);
    }


    // ------------------------------------------------------------------------------------------------
    // Getters and Setters
    // ------------------------------------------------------------------------------------------------


    public void setPolygonDrawn() {
        this.isPolygonDrawn = true;
    }

    public boolean getPolygonDrawn() {
        return this.isPolygonDrawn;
    }

    public void setPointList(ArrayList<Circle> pointList) {
        PointList = pointList;
    }

    public ArrayList<Circle> getPointList() {
        return PointList;
    }

    public void setEdgeList(ArrayList<Path> edgeList) {
        EdgeList = edgeList;
    }

    public ArrayList<Path> getEdgeList() {
        return EdgeList;
    }

}

