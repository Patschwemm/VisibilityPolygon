package geometry;


import java.awt.geom.Line2D;

import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Stack;

public class Polygon {

    private Stack<Circle> P = new Stack<>();
    private ArrayList<Circle> PointList = new ArrayList<>(50);
    private ArrayList<Line> EdgeList = new ArrayList<>(50);
    private Circle p = null;
    private boolean isPolygonDrawn = false;
    private boolean is_p_set = false;


    public void addNode(double x, double y) {

        Circle point = this.createNode(x, y);

        if (PointList.size() != 0 && this.inRange(PointList.get(0), point)<= 15 ) {
            if (PointList.size() <= 2) {
                System.out.println("tried to draw Polygon with 2 or less nodes");
            } else {
                Line edge = this.createEdge(PointList.get(PointList.size() - 1).getCenterX(),
                        PointList.get(PointList.size() - 1).getCenterY(),
                        PointList.get(0).getCenterX(),
                        PointList.get(0).getCenterY());
                if (!checkPolygonIntersection(edge, true)) {
                    System.out.println("Edge is invalid");
                } else {
                    EdgeList.add(edge);
                    GUI.edgescene.getChildren().add(edge);
                    this.setPolygonDrawn();
                    System.out.println("Polygon drawn");
                }
            }
        } else {
            if (PointList.size() == 0) {
                PointList.add(point);
                GUI.pointscene.getChildren().add(point);
            } else if (PointList.size() >= 1) {
                Line edge = this.createEdge(PointList.get(PointList.size() - 1).getCenterX(),
                        PointList.get(PointList.size() - 1).getCenterY(),
                        point.getCenterX(),
                        point.getCenterY());
                if (!checkPolygonIntersection(edge, false)) {
                    System.out.println("Edge is invalid");
                } else {
                    PointList.add(point);
                    GUI.pointscene.getChildren().add(point);
                    EdgeList.add(edge);
                    GUI.edgescene.getChildren().add(edge);
                }
            }
        }
    }

    public Circle createNode(double x, double y) {
        //create new Point as Polygon node
        Circle point = new Circle();
        point.toFront();
        point.setCenterX(x);
        point.setCenterY(y);
        point.setRadius(5.5);
        point.setStroke(Color.BLACK);
        point.setFill(Color.BLACK);
        return point;
    }

    public Line createEdge(double x1, double y1, double x2, double y2) {
        Line edge = new Line((float) x1, (float) y1, (float) x2, (float) y2);
        edge.setStrokeWidth(5);
        edge.setStroke(Color.BLACK);
        return edge;
    }

    public boolean checkPolygonIntersection(Line edge, boolean connector) {

        double x1 = edge.getStartX();
        double y1 = edge.getStartY();
        double x2 = edge.getEndX();
        double y2 = edge.getEndY();

        for (int i = (connector) ? 1 : 0; i < EdgeList.size() -1; i++) {
            double x3 = EdgeList.get(i).getStartX();
            double y3 = EdgeList.get(i).getStartY();
            double x4 = EdgeList.get(i).getEndX();
            double y4 = EdgeList.get(i).getEndY();


            if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
                return false;
            }
        }
        return true;
    }

    public boolean p_in_Polygon(Circle p) {

        int count = 0;
        double x1 = p.getCenterX();
        double y1 = p.getCenterY();
        double x2 = 5000;
        double y2 = p.getCenterY();

        for (int i =  0; i < EdgeList.size() -1; i++) {
            double x3 = EdgeList.get(i).getStartX();
            double y3 = EdgeList.get(i).getStartY();
            double x4 = EdgeList.get(i).getEndX();
            double y4 = EdgeList.get(i).getEndY();


            if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
                count ++;
            }
        }

        if (count % 2 == 1){
            return true;
        }else {
            return false;
        }

    }


    public double inRange(Circle c1, Circle c2) {
        return ((Math.sqrt((c1.getCenterX() - c2.getCenterX()) * (c1.getCenterX() - c2.getCenterX())
                + (c1.getCenterY() - c2.getCenterY()) * (c1.getCenterY() - c2.getCenterY()))) );
    }

    public void deletePolygon() {
        this.EdgeList.clear();
        this.PointList.clear();
        GUI.pointscene.getChildren().clear();
        GUI.edgescene.getChildren().clear();
        p = null;
        this.is_p_set = false;
        this.isPolygonDrawn = false;
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

    public boolean is_p_set() {
        return this.is_p_set;
    }

    public void setPointList(ArrayList<Circle> pointList) {
        this.PointList = pointList;
    }

    public ArrayList<Circle> getPointList() {
        return PointList;
    }


    public Circle get_p() {
        return this.p;
    }

    public void move_p(double x, double y ){
        this.p.setCenterX(x);
        this.p.setCenterY(y);
    }

    public void set_p(double x, double y) {
        this.p = createNode(x, y);
        this.p.setStroke(Color.DARKRED);
        this.p.setFill(Color.DARKRED);
        if (p_in_Polygon(p)){
            this.is_p_set = true;
            GUI.pointscene.getChildren().add(p);
        } else {
            System.out.println("p not in polygon, set another p");
        }
    }

}

