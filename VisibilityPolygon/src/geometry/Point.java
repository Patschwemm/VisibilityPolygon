package geometry;

import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Point extends Circle {

    private Point pointLinked;
    private Point corner;
    private Point successor;
    private double local_beta;
    private Point predecc;
    private Point linkedtocorner;
    private ArrayList<Point> childs;
    private Point child;
    private Point parent;
    private Point intersect_v;


    public Point() {
        super();
        this.pointLinked = null;
        this.corner = null;
        this.intersect_v = null;
        this.successor = null;
        this.local_beta = 0;
        this.predecc = null;
        this.linkedtocorner = null;
        this.childs = new ArrayList<>();
        this.child = null;
        this.parent = null;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // for BetaVis
    // ----------------------------------------------------------------------------------------------------------------


    //setIntersectLink(Point $v$) & Verknüpft Eckpunkt $c$ mit Schnittpunkt $s$ \\
    //getIntersectLink() & Gibt entweder Punkt $c$ oder Punkt $s$ zurück\\
    //setCorner()  & Setzt eine Boolean für Eckpunkt $c$  \\
    //isCorner() & Gibt wieder ob der aktuelle Punkt $v$ auch ein Eckpunkt $c$ ist \\
    //link(Point $v$)& Verknüpft einen beliebigen Punkt mit $v$  \\
    //getlink() &  Gibt $v$ als verknüpften Punkt wieder \\
    //setSucc(Point $v$) & Setzt Verknüpfung zu dem vorherigen Nachbarpunkt  \\
    //getSucc() & Gibt den vorherigen Nachbarpunkt wieder \\
    //setPredec(Point $v$)& Setzt Verknüpfung zu dem nachfolgnden Nachbarpunkt  \\
    //getPredecc()& Gibt den nachfolgenden Nachbarpunkt wieder \\
    //setBeta(Float $\beta$)&  Gibt für einen $\beta$-sichtbaren Eckpunkt das restliche $\beta$ wieder\\
    //setParent(Point $c$)& Setzt den Elternknoten im Rekursionsbaum  \\
    //getParent() & Gibt den Elternknoten aus \\
    //setChild(Point $c$)& Setzt einen Kindknoten im Rekursionsbaum \\
    //getChild() & Gibt die Kindknoten aus \\

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

    public Point getLinkedtocorner() {
        return linkedtocorner;
    }

    public void setLinkedtocorner(Point linkedtocorner) {
        this.linkedtocorner = linkedtocorner;
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

    public void setPredecessor(Point predecc) {
        this.predecc = predecc;
    }

    public Point getSuccessor() {
        return successor;
    }

    public Point getPredecessor() {
        return predecc;
    }

    public void clearBetaLinkage(){
        this.successor = null;
        this.local_beta = 0;
        this.predecc = null;
        this.childs.clear();
        this.child = null;
        this.parent = null;
    }
    // ----------------------------------------------------------------------------------------------------------------
    // for VisPolygon
    // ----------------------------------------------------------------------------------------------------------------

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



    public Point getPointLinked() {
        return this.pointLinked;
    }

    public void setPointLinked(Point Pointlinked) {
        this.pointLinked = Pointlinked;
    }


}
