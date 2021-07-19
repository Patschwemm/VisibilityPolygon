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


    //setIntersect(Point $v$) & Verknüpft Eckpunkt $c$ mit Schnittpunkt $s$ \\
    //getIntersect() & Gibt Punkt $s$ zurück\\
    //setCorner()  & Setzt enen Eckpunkt $c$ für Schnittpunkt s \\
    //getCorner() & gibt den Eckpunkt $c$ für Schnittpunkt s aus \\
    //setSuccessor(Point $v$) & Setzt Verknüpfung zu dem vorherigen Nachbarpunkt  \\
    //getSuccessor() & Gibt den vorherigen Nachbarpunkt wieder \\
    //setPredecessor(Point $v$)& Setzt Verknüpfung zu dem nachfolgnden Nachbarpunkt  \\
    //getPredecessor()& Gibt den nachfolgenden Nachbarpunkt wieder \\
    //setLocalBeta(Float $\beta$)&  Gibt für einen $\beta$-sichtbaren Eckpunkt das restliche $\beta$ wieder\\
    //setTreeParent(Point $c$)& Setzt den Elternknoten im Rekursionsbaum  \\
    //getTreeParent() & Gibt den Elternknoten aus \\
    //setTreeChild(Point $c$)& Setzt einen Kindknoten im Rekursionsbaum \\
    //getTreeChild() & Gibt die Kindknoten aus \\

    public void setTreeChild(Point Child) {
        childs.add(Child);
    }

    public ArrayList<Point> getTreeChild() {
        return childs;
    }

    public void setTreeParent(Point Parent) {
        this.parent = Parent;
    }

    public Point getTreeParent() {
        return this.parent;
    }

    public double getLocalBeta() {
        return this.local_beta;
    }

    public void setLocalBeta(double local_beta) {
        this.local_beta = local_beta;
    }

    public void setSuccessor(Point successor) {
        this.successor = successor;
    }

    public void setPredecessor(Point predec) {
        this.predec = predec;
    }

    public Point getSuccessor() {
        return successor;
    }

    public Point getPredecessor() {
        return predec;
    }

    public void setCorner(Point corner) {
        this.corner = corner;
    }

    public Point getCorner() {
        return corner;
    }

    public void setIntersect(Point intersect_v) {
        this.intersect_v = intersect_v;
    }

    public Point getIntersect() {
        return intersect_v;
    }

    public boolean isInner_turn_corner() {
        return inner_turn_corner;
    }

    public void setInner_turn_corner(boolean inner_turn_corner) {
        this.inner_turn_corner = inner_turn_corner;
    }

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



    public Point getPointLinked() {
        return this.pointLinked;
    }

    public void setPointLinked(Point Pointlinked) {
        this.pointLinked = Pointlinked;
    }


}
