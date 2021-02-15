package geometry;


import java.awt.geom.Line2D;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Stack;

public class Polygon {

    private Stack<Circle> P = new Stack<Circle>();
    private ArrayList<Circle> PointList = new ArrayList<>(50);
    private ArrayList<Line> EdgeList = new ArrayList<>(50);
    private boolean isPolygonDrawn = false;

    public void addNode(double x, double y) {

        Circle point = this.createNode(x, y);

        if (PointList.size() != 0 && this.inRange(PointList.get(0), point)) {
            if (PointList.size() <= 2) {
                System.out.println("tried to draw Polygon with 2 or less nodes");
            } else {
                Line edge = this.createEdge(PointList.get(PointList.size() - 1).getCenterX(),
                        PointList.get(PointList.size() - 1).getCenterY(),
                        PointList.get(0).getCenterX(),
                        PointList.get(0).getCenterY());
                if (!checkIntersection(edge, true)) {
                    System.out.println("Edge is invalid");
                } else {
                    EdgeList.add(edge);
                    GUI.edgescene.getChildren().add(edge);
                    this.setPolygonDrawn();
                    System.out.println("Polygon drawn");
                }
            }
        } else {
            if (PointList.size() == 0){
                PointList.add(point);
                GUI.pointscene.getChildren().add(point);
            }else if (PointList.size() >= 1) {
                Line edge = this.createEdge(PointList.get(PointList.size() - 1).getCenterX(),
                        PointList.get(PointList.size() - 1).getCenterY(),
                        point.getCenterX(),
                        point.getCenterY());
                if (!checkIntersection(edge,false)) {
                    System.out.println("Edge is invalid");
                } else {
                    PointList.add(point);
                    GUI.pointscene.getChildren().add(point);
                    EdgeList.add(edge);
                    GUI.edgescene.getChildren().add(edge);
                }
            }
        }
        System.out.println("Edge Length: " + EdgeList.size());
        System.out.println("Point Length: " + PointList.size());
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

    public Line createEdge(double x1, double y1, double x2, double y2) {
        Line edge = new Line((float) x1, (float) y1, (float) x2, (float) y2);
        edge.setStrokeWidth(5);
        edge.setStroke(Color.DARKRED);
        return edge;
    }

    public boolean checkIntersection(Line edge, boolean connector) {

        float x1 = (float)edge.getStartX();
        float y1 = (float)edge.getStartY();
        float x2 = (float)edge.getEndX();
        float y2 = (float)edge.getEndY();



        for (int i=(connector)? 1 : 0; i < EdgeList.size()-1; i++) {
            double x3 = EdgeList.get(i).getStartX();
            double y3 = EdgeList.get(i).getStartY();
            double x4 = EdgeList.get(i).getEndX();
            double y4 = EdgeList.get(i).getEndY();



            if ( Line2D.linesIntersect(x1,y1,x2,y2,x3,y3,x4,y4)) {
                return false;
            }
        }
        return true;
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

    public void setEdgeList(ArrayList<Line> edgeList) {
        EdgeList = edgeList;
    }

    public ArrayList<Line> getEdgeList() {
        return EdgeList;
    }

}

