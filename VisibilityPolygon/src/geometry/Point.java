package geometry;

import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Point extends Circle {

    private Point pointLinked;
    private Point corner;
    private Point successor;
    private double local_beta;
    private Point predec;
    private Point linkedtocorner;
    private ArrayList<Point> childs;
    private Point parent;
    private Point intersect_v;
    private boolean inner_turn_corner;


    public Point() {
        super();
        this.pointLinked = null;
        this.corner = null;
        this.intersect_v = null;
        this.successor = null;
        this.predec = null;
        this.local_beta = 0;
        this.predec = null;
        this.linkedtocorner = null;
        this.childs = new ArrayList<>();
        this.parent = null;
        this.inner_turn_corner = false;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // for BetaVis
    // ----------------------------------------------------------------------------------------------------------------



    //adds a tree child to this point
    public void setTreeChild(Point Child) {
        childs.add(Child);
    }

    //returns the child list
    public ArrayList<Point> getTreeChild() {
        return childs;
    }

    //sets a tree parent to this corner
    public void setTreeParent(Point Parent) {
        this.parent = Parent;
    }

    //gets the tree parent of this corner
    public Point getTreeParent() {
        return this.parent;
    }

    //gets the current beta on this corner
    public double getLocalBeta() {
        return this.local_beta;
    }

    //sets the current beta of this corner
    public void setLocalBeta(double local_beta) {
        this.local_beta = local_beta;
    }

    //sets the following point
    public void setSuccessor(Point successor) {
        this.successor = successor;
    }

    //sets the previous point
    public void setPredecessor(Point predec) {
        this.predec = predec;
    }

    //gets the following points
    public Point getSuccessor() {
        return successor;
    }

    //gets the previous point
    public Point getPredecessor() {
        return predec;
    }

    //gets the corner if this point is an intersection of the boundary and a corner
    public void setCorner(Point corner) {
        this.corner = corner;
    }

    //gets the corner if this point is an intersection of the boundary and a corner
    public Point getCorner() {
        return corner;
    }

    //sets the intersection of a corner and boundary if this point is a corner
    public void setIntersect(Point intersect_v) {
        this.intersect_v = intersect_v;
    }

    //gets the intersection of a corner and boundy if this point is a corner
    public Point getIntersect() {
        return intersect_v;
    }

    //returns if this corner is an inner corner
    public boolean isInner_turn_corner() {
        return inner_turn_corner;
    }

    //sets this corner as an inner corner
    public void setInner_turn_corner(boolean inner_turn_corner) {
        this.inner_turn_corner = inner_turn_corner;
    }

    //clears all linkages
    public void clearBetaLinkage(){
        this.successor = null;
        this.local_beta = 0;
        this.predec = null;
        this.childs.clear();
        this.parent = null;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // for VisPolygon
    // ----------------------------------------------------------------------------------------------------------------


    //gets the point linked as intersection or corner
    public Point getPointLinked() {
        return this.pointLinked;
    }

    //sets the point linked as intersection or corner
    public void setPointLinked(Point Pointlinked) {
        this.pointLinked = Pointlinked;
    }


}
