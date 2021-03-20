package geometry;


import java.awt.*;
import java.awt.geom.Line2D;

import com.sun.javafx.geom.Edge;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Stack;

public class Polygon {

    private ArrayList<Point> PointList = new ArrayList<>(50);
    private ArrayList<Line> EdgeList = new ArrayList<>(50);
    private Point p = null;
    private boolean isPolygonDrawn = false;
    private boolean is_p_set = false;


    public void addNode(double x, double y) {

        Point point = this.createNode(x, y);

        if (PointList.size() != 0 && this.inRange(PointList.get(0), point) <= 15) {
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


    public Point createNode(double x, double y) {
        //create new Point as Polygon node
        Point point = new Point();
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

        for (int i = (connector) ? 1 : 0; i < EdgeList.size() - 1; i++) {
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

    public boolean p_in_Polygon(Point p) {

        double total_angle = 0;
        for (int i = 0; i < EdgeList.size(); i++) {
            total_angle += checkAngle(PointList.get(i), p, PointList.get(incrementIdx(i)));
        }
        if (Math.round(total_angle) != 0) {
            return true;
        } else {
            return false;
        }
    }


    public double inRange(Point c1, Point c2) {
        return ((Math.sqrt((c1.getCenterX() - c2.getCenterX()) * (c1.getCenterX() - c2.getCenterX())
                + (c1.getCenterY() - c2.getCenterY()) * (c1.getCenterY() - c2.getCenterY()))));
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

    // v1 first node, v2 second node, p point of visibility
    public double checkAngle(Point v1, Point p, Point v2) {

        double angle = 0;
        double x1 = v1.getCenterX() - p.getCenterX();
        double y1 = v1.getCenterY() - p.getCenterY();
        double x2 = v2.getCenterX() - p.getCenterX();
        double y2 = v2.getCenterY() - p.getCenterY();

        //determinant and skalar product
        angle = Math.atan2(x2 * y1 - y2 * x1, x2 * x1 + y2 * y1) * 180 / Math.PI;

        return angle;
    }


    // ------------------------------------------------------------------------------------------------
    // Getters and Setters
    // ------------------------------------------------------------------------------------------------

    //increments so that if idx = last polygon node +1 -> idx = 0, first polygo node
    public int incrementIdx(int idx) {
        idx++;
        idx = idx % PointList.size();
        return idx;
    }

    //increments so that if idx = 0, first polygon node -> idx = last polygon node
    public int decrementIdx(int idx) {
        idx--;
        idx = idx + PointList.size();
        idx = idx % PointList.size();
        return idx;
    }


    public void setPolygonDrawn() {
        this.isPolygonDrawn = true;
    }

    public boolean getPolygonDrawn() {
        return this.isPolygonDrawn;
    }

    public boolean is_p_set() {
        return this.is_p_set;
    }

    public void setPointList(ArrayList<Point> pointList) {
        this.PointList = pointList;
    }

    public ArrayList<Point> getPointList() {
        return PointList;
    }


    public Point get_p() {
        return this.p;
    }

    public void move_p(double x, double y) {
        if (p_in_Polygon(p)) {
            this.p.setCenterX(x);
            this.p.setCenterY(y);
        } else {
            System.out.println("Out of Bounds, deleting p. \n Set a new p");
            p = null;
            this.is_p_set = false;
            GUI.pointscene.getChildren().remove(GUI.pointscene.getChildren().size() - 1);
            EventHandler.p_moving = false;
            EventHandler.clicks = 0;
        }
    }

    public void set_p(double x, double y) {
        this.p = createNode(x, y);
        this.p.setStroke(Color.DARKRED);
        this.p.setFill(Color.DARKRED);
        if (p_in_Polygon(p)) {
            this.is_p_set = true;
            GUI.pointscene.getChildren().add(p);
            System.out.println("p in polygon");
        } else {
            System.out.println("p not in polygon, set another p");
        }

    }

    public void colorBlack() {
        for(int i = 0; i < PointList.size(); i++){
            PointList.get(i).setFill(Color.BLACK);
        }
    }

}



