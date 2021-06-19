package geometry;

import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Point extends Circle {

    private Point PointLinked = null;
    private boolean corner = false;
    private Point Successor = null;
    private double local_beta;
    private Point Predecc = null;
    private Point linkedtocorner = null;
    private ArrayList<Point> Childs = new ArrayList<>();
    private Point child = null;
    private Point parent = null;


    public Point(){
        super();
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
        Childs.add(Child);
    }

    public ArrayList<Point> getTreeChild() {
        return Childs;
    }

    public void setTreeParent(Point Parent){this.parent = Parent;}

    public Point getTreeParent(){return this.parent;}

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
        Successor = successor;
    }

    public void setPredecessor(Point predecc) {
        Predecc = predecc;
    }

    public Point getSuccessor() {
        return Successor;
    }

    public Point getPredecessor() {
        return Predecc;
    }

    public void setCorner(){
        corner = true;
    }

    public boolean isCorner(){
        return corner;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // for VisPolygon
    // ----------------------------------------------------------------------------------------------------------------

    public Point getPointLinked(){
        return this.PointLinked;
    }

    public void setPointLinked(Point Pointlinked){
        this.PointLinked = Pointlinked;
    }





}
