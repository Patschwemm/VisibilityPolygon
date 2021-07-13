package geometry;

import javafx.scene.paint.Color;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;

public class Beta_Visibility_Brute extends VisPolygon {

    private final Stack<Point> P = GUI.vis_q.getBetavis_P();
    private final Stack<Point> Vis = GUI.vis_q.getBetavis_Vis();
    private final Stack<Point> B_vis = new Stack<Point>();
    private final ArrayList<Point> rec_c_points = new ArrayList<>();


    public Beta_Visibility_Brute(double beta) {
        //check if Vis Polygon is computed
        if (GUI.vis_q != null) {

            reset_angle();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("BETA: " + beta);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("q: ");
            System.out.println(GUI.polygon.get_q());
            // preprocessing
            // ----------------------------------------


            // copy array so we have it for several BetaVisibility runs
            Stack<Point> P_temp = (Stack<Point>) this.P.clone();
            Stack<Point> Vis_temp = (Stack<Point>) this.Vis.clone();


            Collections.rotate(Vis_temp, 1);
            Collections.rotate(P_temp, -1);


            // calculate visibility
            // ----------------------------------------
            beta_visibility(P_temp, Vis_temp, beta);

        } else {
            System.out.println("Cannot compute Beta Visibility \n");
        }
    }

    private void beta_visibility(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta) {


        System.out.println("V.size:" + Vis_temp.size());
        System.out.println("P.size:" + P_temp.size());
        System.out.println("Bvis.size:" + B_vis.size());


        while (P_temp.size() != 0 && Vis_temp.size() != 0) {
            polygonCycle(P_temp, Vis_temp, beta, GUI.polygon.get_q());
            System.out.println("polygoncycle entered");
            System.out.println("BETA VISIBILITY : " + GUI.polygon.get_q());
        }
        System.out.println("B_vis complete: ");
        printStackordertoPolygon(B_vis);
        connectEdges(B_vis);
        clearRecQueryPointList();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    private void polygonCycle(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q) {

        //synchronously on Vis_temp and P_temp Stacks finds the same points
        //if the points are not the same, a cave has been found with Vis.peek() at ending of the cave


        System.out.println("V.size in polygoncycle:" + Vis_temp.size());
        System.out.println("P_temo size in polygoncycle: " + P_temp.size());
        System.out.println("Q IN POLYGONCYCLE: " + q);

        System.out.println("P_temp: ");
        printStackordertoPolygon(P_temp);
        System.out.println("first element: ");
        System.out.println(P_temp.peek());

        System.out.println("Vis_temp: ");
        printStackordertoPolygon(Vis_temp);
        System.out.println("first element: ");
        System.out.println(Vis_temp.peek());

        while (P_temp.size() != 0 && Vis_temp.size() != 0 && P_temp.peek() == Vis_temp.peek()) {
            B_vis.push(P_temp.pop());
            Vis_temp.pop();
            System.out.println("Point ADDED BETA VIS");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
        }

        try{
            System.out.println("P peek out of sync: "+ P_temp.peek());
            System.out.println("Vis peek out of sync: "+ Vis_temp.peek());
        } catch (Exception e ){
            System.out.println("no corner");
        }

        System.out.println("B_vispeek: " + B_vis.peek().getCenterX() + " " + B_vis.peek().getCenterY() + " is corner?" + B_vis.peek().isCorner());

        if (B_vis.peek().isCorner() == true && P_temp.size() != 0 && Vis_temp.size() != 0) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Right Cave");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
            rightCave(P_temp, B_vis, Vis_temp, beta, q);
            polygonCycle(P_temp, Vis_temp, beta, q);
        } else if (B_vis.peek().isCorner() == false && P_temp.size() != 0 && Vis_temp.size() != 0) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Left Cave");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
            leftCave(P_temp, B_vis, Vis_temp, beta, q);
//            polygonCycle(P_temp,Vis_temp,beta,q);
        }

        System.out.println("breakpoint");
    }


    protected void partlyRecursivePolygon(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end, String direction) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        System.out.println("B_vispeek: " + B_vis.peek());
        System.out.println("intersect v: " + intersect_v);
        System.out.println("end: " + end);
        System.out.println("q: " + q);


        if (direction == "right") {

            get_second_peek(P_temp).setPredecessor(P_temp.peek());
            P_temp.pop();
            temp = getPartPolygonRight(P_temp, Vis_temp);

        } else if (direction == "left") {

            temp = getPartPolygonLeft(P_temp, Vis_temp, intersect_v);

        }


        System.out.println("temp.size " + temp.size());
        System.out.println("P_temp peek in REC VISIBILITY: " + P_temp.peek());
        System.out.println("VIs_temp peek in REC VISIBILITY: " + Vis_temp.peek());
        printStackordertoPolygon(temp);

        if (temp.size() == 0) {

            if (direction == "right") {

                B_vis.push(intersect_v);
                B_vis.push(Vis_temp.pop());

            } else if (direction == "left") {


                B_vis.push(Vis_temp.pop());
                B_vis.push(intersect_v);

            }


        } else {
            //construct polygon for recursive visibility

            if (direction == "right") {

                temp.push(Vis_temp.pop());
                temp.push(B_vis.peek());
                temp.push(intersect_v);

            } else if (direction == "left") {

                temp.push(get_second_peek(Vis_temp));
                temp.push(intersect_v);

            }


            Collections.rotate(temp, 1);
            Collections.reverse(temp);

            P_rec = (Stack<Point>) temp.clone();


            B_vis.peek().setLocalBeta(beta);

            Vis_rec.push(end);
            Vis_rec.push(B_vis.peek());

            System.out.println("q: " + q);
            algorithm_default(temp, Vis_rec, q);

            // remove point q, because they are already added or obsolete for the polygon
            P_rec.remove(q);
            Vis_rec.remove(q);


            Collections.reverse(Vis_rec);
            Collections.rotate(Vis_rec, 1);

            System.out.println("P_rec: ");
            printStackordertoPolygon(P_rec);
            System.out.println("first element: ");
            System.out.println(P_rec.peek());

            System.out.println("Vis_rec: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("first element: ");
            System.out.println(Vis_rec.peek());

            //cycle polygon with new query Point q
            polygonCycle(P_rec, Vis_rec, beta, q);


            System.out.println("after loop");
            System.out.println("after loop");
            printStackordertoPolygon(B_vis);


        }
    }


    protected void recursivePolygonVisibility(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point end, String direction) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        System.out.println("B_vispeek: " + B_vis.peek());
        System.out.println("q in recursivepolygonvis: " + q);
        System.out.println("end: " + end);


        if (direction == "right") {

            //construct polygon for recursive visibility
            temp = getWholePolygonRight(P_temp, Vis_temp);

            System.out.println("Vis_temp popped: " + Vis_temp.peek());
            temp.push(end);
            temp.push(Vis_temp.pop());
            temp.push(B_vis.peek());
            System.out.println("Vis_temp end: " + end);

        } else if (direction == "left") {

            //construct polygon for recursive visibility

            temp = getWholePolygonLeft(P_temp, Vis_temp);
            System.out.println("Vis_temp popped: " + Vis_temp.peek());
//            temp.push(end);
            temp.push(Vis_temp.pop());
            temp.push(B_vis.peek());
            System.out.println("Vis_temp end: " + end);
        }


        Collections.rotate(temp, 1);
        Collections.reverse(temp);
        printStackordertoPolygon(temp);

        P_rec = (Stack<Point>) temp.clone();


        System.out.println("asdadss");
        printStackordertoPolygon(Vis_temp);

        B_vis.peek().setLocalBeta(beta);

        Vis_rec.push(end);
        Vis_rec.push(B_vis.peek());


        System.out.println("q: " + q);
        algorithm_default(temp, Vis_rec, q);


        System.out.println("vis rec: " + Vis_rec.peek());

        // remove point q, because they are already added or obsolete for the polygon
        P_rec.remove(q);
        P_rec.remove(end);
        Vis_rec.remove(q);
        Vis_rec.remove(B_vis.peek());


        Collections.reverse(Vis_rec);
        Collections.rotate(Vis_rec, 1);

        System.out.println("P_rec: ");
        printStackordertoPolygon(P_rec);
        System.out.println("first element: ");
        System.out.println(P_rec.peek());

        System.out.println("Vis_rec: ");
        printStackordertoPolygon(Vis_rec);
        System.out.println("first element: ");
        System.out.println(Vis_rec.peek());

        System.out.println("q before polygoncycle: " + q);
        System.out.println("q before polygoncycle: " + q);
        polygonCycle(P_rec, Vis_rec, beta, q);

    }

    // ----------------------------------------------------------------------------------------------------------
    // Right cave functions
    // ----------------------------------------------------------------------------------------------------------


    public void rightCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {

        Point c_outerturn = B_vis.peek();

        System.out.println("V.size:" + Vis_temp.size());
        System.out.println("P.size:" + P_temp.size());
        System.out.println("right cave corner c_outerturn : " + c_outerturn);
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), c_outerturn));
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(c_outerturn, Vis_temp.peek()));

        double alpha_init = Math.abs(checkAngle(P_temp.peek(), c_outerturn, Vis_temp.peek()));
        GUI.betapolygonscene.getChildren().add(createNode(Vis_temp.peek().getCenterX(), Vis_temp.peek().getCenterY()));
        System.out.println("Ptemp peek:" + P_temp.peek());
        System.out.println("couter: " + c_outerturn);
        System.out.println("getscondevis : " + get_second_peek(Vis_temp));


        System.out.println("alpha : " + alpha_init + "Beta: " + beta);

        //recursive beta calculated from point q
        System.out.println("q: " + q);
        System.out.println("c outerturn: " + c_outerturn);
        double old_beta = -100;

        if (q != GUI.polygon.get_q()) {
            Point q_parent = q.getTreeParent();
            System.out.println("q_parent:" + q_parent);
            Point prev_end = simulateRayEndPoint(q_parent, q);
            System.out.println("old beta :" + beta);
            old_beta = beta;
            beta = beta - Math.abs(checkAngle(prev_end, q, c_outerturn));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(prev_end, q));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(q, c_outerturn));
            System.out.println("new beta set:" + beta);
        }


        if (alpha_init > beta) {

            //predecessor is q, so that we get the artificial edge
            c_outerturn.setPredecessor(q);
            c_outerturn.setSuccessor(P_temp.peek());
            c_outerturn.setTreeParent(q);
            q.setTreeChild(c_outerturn);
            c_outerturn.setLocalBeta(beta);

            Point intersect_v = caveIntersectRotate(P_temp, Vis_temp, beta, q, c_outerturn, get_second_peek(Vis_temp));
            System.out.println("interscet in right cave: " + intersect_v);
            cycleToIntersectPoint(P_temp, intersect_v);
            System.out.println("B_vis: " + B_vis.peek());
            System.out.println("getsecond: " + get_second_peek(B_vis));
            partlyRecursivePolygon(P_temp, B_vis, Vis_temp, beta, c_outerturn, intersect_v, Vis_temp.peek(), "right");

        } else if (alpha_init <= beta) {

            //predecessor is q, so that we get the artificial edge
            c_outerturn.setPredecessor(q);
            c_outerturn.setSuccessor(P_temp.peek());
            c_outerturn.setTreeParent(q);
            q.setTreeChild(c_outerturn);
            c_outerturn.setLocalBeta(beta);

//            B_vis.push(P_temp.pop());
//            Point intersect_v = caveIntersectRotate(P_temp, 0, q, c_outerturn, get_second_peek(Vis_temp));
//            P_temp.push(B_vis.pop());
            recursivePolygonVisibility(P_temp, B_vis, Vis_temp, beta, c_outerturn, Vis_temp.peek(), "right");
        }
    }


    public Point caveIntersectRotate(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q, Point c, Point end) {


        Stack<Point> P_clone = (Stack<Point>) P_temp.clone();
        Point q_rotation;


        System.out.println("Start: " + B_vis.peek());
        System.out.println("B_vis püeek corner: " + B_vis.peek().isCorner());
        System.out.println("end in caveintersectrotat: " + end);
        if (B_vis.peek().isCorner()) {
            System.out.println("clockwise rotation ");
            q_rotation = rotatePredeccPointClockwise(beta, c, q);
        } else {
            System.out.println("counterclockwise rotation ");
            q_rotation = rotatePredeccPointCounterClockwise(beta, c, q);
            P_clone.push(B_vis.peek());
        }
        GUI.polygonscene.getChildren().add(q_rotation);
        Point intersect_v = null;
        Point temp_intersect;
        double min_range = 1000000000;


        System.out.println("AFTER ROTATION:");
        System.out.println("end: " + end);

//        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(q_rotation, c));
        System.out.println("P_TEMP PEEK IN CAVE INTERSECT ROTATE: " + P_clone.peek());
        System.out.println("END:: "+ end);

        while (P_clone.peek() != end && P_clone.size() >= 2) {
            System.out.println("P_temp.size in cave intersect rotate: " + P_clone.size());
            System.out.println("angle1:" + visibleAngle(P_clone.peek(), c, Vis_temp.peek()) + "visible2: " + visibleAngle(P_clone.peek(), c, get_second_peek(P_clone)));
            if (B_vis.peek().isCorner()) {
                if (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true
                        && visibleAngle(P_clone.peek(), c, Vis_temp.peek())
                        && visibleAngle(P_clone.peek(), c, get_second_peek(P_clone))) {
                    System.out.println("intersection found");
                    temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone));
                    temp_intersect.setSuccessor(get_second_peek(P_clone));
                    temp_intersect.setPredecessor(P_clone.peek());
                    System.out.println("min range > inrange" + min_range + " " + inRange(q_rotation, temp_intersect));
                    if (min_range > inRange(q_rotation, temp_intersect)) {
                        System.out.println("range shorter!!" + inRange(q_rotation, temp_intersect));
                        min_range = inRange(q_rotation, temp_intersect);
                        intersect_v = temp_intersect;
                        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), get_second_peek(P_temp)));
                    }
                } else {
                    System.out.println("not shorter!!");
                }
            } else {
                if (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true
                        && visibleAngle(Vis_temp.peek(), c, get_second_peek(P_clone))
                        && visibleAngle(P_clone.peek(), c, get_second_peek(P_clone))) {
                    System.out.println("intersection found");
//                GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), get_second_peek(P_temp)));
                    temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone));
                    temp_intersect.setSuccessor(get_second_peek(P_clone));
                    temp_intersect.setPredecessor(P_clone.peek());
                    System.out.println("min range > inrange" + min_range + " " + inRange(q_rotation, temp_intersect));
                    if (min_range > inRange(q_rotation, temp_intersect)) {
                        System.out.println("range shorter!!" + inRange(q_rotation, temp_intersect));
                        min_range = inRange(q_rotation, temp_intersect);
                        intersect_v = temp_intersect;
                        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), get_second_peek(P_temp)));
                    } else {
                        System.out.println("not shorter!!");
                    }
                }
            }
            P_clone.pop();
        }
                GUI.polygonscene.getChildren().add(createNode(intersect_v.getCenterX(), intersect_v.getCenterY()));
        return intersect_v;
    }



    protected void cycleToIntersectPoint(Stack<Point> P_temp, Point intersect_v) {


        System.out.println("intersect _ v " + intersect_v);
        System.out.println("intersect predecc:" + intersect_v.getPredecessor());
        System.out.println("intersect succ:" + intersect_v.getSuccessor());


        while (P_temp.peek() != intersect_v.getPredecessor()) {
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(P_temp.peek(), get_second_peek(P_temp)));
            P_temp.pop();
            System.out.println("in cycleintersectloop");
        }

//        while (lineLineSegIntersection(P_temp.peek(), get_second_peek(P_temp), v_intersect_norm[0], v_intersect_norm[1]) == false) {
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(P_temp.peek(), get_second_peek(P_temp)));
//            P_temp.pop();
//            v_intersect_norm = getPointsOnNormal(P_temp.peek(), get_second_peek(P_temp), intersect_v);
//            System.out.println("in cycleintersectloop");
//        }

        System.out.println("p.temp: " + P_temp.peek());
    }

    protected Stack<Point> getPartPolygonRight( Stack<Point> P_temp,  Stack<Point> Vis_temp){
        Stack<Point> temp = new Stack<>();

        while (P_temp.peek() != get_second_peek(Vis_temp)) {
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P_temp.peek())).setFill(Color.BLUE);
            System.out.println("P_clone: " + P_temp.peek());
            temp.push(P_temp.pop());
        }
        return temp;
    }

    protected Stack<Point> getWholePolygonRight(Stack<Point> P_temp, Stack<Point> Vis_temp){

        Stack<Point> temp = new Stack<>();

        while (P_temp.size() != 0 && P_temp.peek() != get_second_peek(Vis_temp)) {
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P_temp.peek())).setFill(Color.BLUE);
            System.out.println("P_clone: " + P_temp.peek());
//            get_second_peek(P_temp).setPredecessor(P_temp.peek());
            temp.push(P_temp.pop());
        }

        return temp;
    }

    // ----------------------------------------------------------------------------------------------------------
    // Left Cave functions
    // ----------------------------------------------------------------------------------------------------------


    public void leftCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {
        // B_vis.peek as point before intersection Point of inner turn
        // P_temp peek is the next point in non visible cave
        // Vis peek is inner turn intersect, get second peek vis is the corner point of the cave.

        Stack<Point> P_rec = new Stack<>();


        Point c_innerturn = get_second_peek(Vis_temp);
        Point c_innerturn_prev = c_innerturn.getPredecessor();
        System.out.println("c inner turn: " + c_innerturn);
        System.out.println("predecessor: "+ c_innerturn_prev);
        System.out.println("VIS TEMP PEEK BEFORE ALPHA: "+ Vis_temp.peek());

        //get cave beginning and calculate angle:

        double alpha = checkAngle(Vis_temp.peek(), c_innerturn,  c_innerturn_prev);
        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(Vis_temp.peek(), c_innerturn));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue( c_innerturn,  c_innerturn_prev));

        System.out.println("alpha: " + alpha + "beta: " + beta);
        System.out.println("c predecessor: " +  c_innerturn_prev);
        System.out.println();


        if (alpha > beta) {

            //predecessor is q, so that we get the artificial edge

            c_innerturn.setPredecessor(q);
            Point Vis_peek = Vis_temp.pop();
            c_innerturn.setSuccessor(get_second_peek(Vis_temp));
            Vis_temp.push(Vis_peek);
            c_innerturn.setTreeParent(q);
            q.setTreeChild(c_innerturn);
            c_innerturn.setLocalBeta(beta);


            System.out.println("c_innterturn prev: "+ c_innerturn_prev);
            Point intersect_v = caveIntersectRotate(P_temp, Vis_temp, beta, q, c_innerturn, c_innerturn_prev);
            System.out.println("intersect v: "+ intersect_v);

//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(intersect_v, c_innerturn));


            System.out.println("c_innerturn as q: "+ c_innerturn);
            System.out.println("VIS TEMP PEEK IN LEFT CAVE: "+ Vis_temp.peek());
            partlyRecursivePolygonLEFT(P_temp, B_vis, Vis_temp, beta, c_innerturn, intersect_v, Vis_temp.peek(), "left");

        } else if (alpha <= beta) {

            //predecessor is q, so that we get the artificial edge
            c_innerturn.setPredecessor(q);
            c_innerturn.setSuccessor(P_temp.peek());
            c_innerturn.setTreeParent(q);
            q.setTreeChild(c_innerturn);
            c_innerturn.setLocalBeta(beta);

//            B_vis.push(P_temp.pop());
//            Point intersect_v = caveIntersectRotate(P_temp, 0, q, c_outerturn, get_second_peek(Vis_temp));
//            P_temp.push(B_vis.pop());
            recursivePolygonVisibility(P_temp, B_vis, Vis_temp, beta, c_innerturn, Vis_temp.peek(), "left");
        }


    }

    protected Stack<Point> getPartPolygonLeft( Stack<Point> P_temp,  Stack<Point> Vis_temp, Point intersect_v){

        Stack<Point> temp = new Stack<>();

        //gets Points until intersection Point
        while (P_temp.peek() != intersect_v.getSuccessor()) {
            System.out.println("P_clone: " + P_temp.peek());
            get_second_peek(P_temp).setPredecessor(P_temp.peek());
            temp.push(P_temp.pop());
        }

        System.out.println("Points until intersect pushed");
//        temp.push(intersect_v);

        //deletes all Points until inner corner Point
        while (P_temp.peek() != get_second_peek(Vis_temp)) {
            System.out.println("P_clone: " + P_temp.peek());
            get_second_peek(P_temp).setPredecessor(P_temp.peek());
            P_temp.pop();
        }

        return temp;
    }


    protected Stack<Point> getWholePolygonLeft(Stack<Point> P_temp, Stack<Point> Vis_temp) {

        Stack<Point> P_rec = new Stack<>();
        Point end = Vis_temp.peek();

        P_rec.push(end);

        while (P_temp.peek() != get_second_peek(Vis_temp)) {
            get_second_peek(P_temp).setPredecessor(P_temp.peek());
            P_rec.push(P_temp.pop());
        }


        printStackordertoPolygon(P_rec);
        System.out.println("stop");

        return P_rec;
    }

    protected void partlyRecursivePolygonLEFT(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end, String direction) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        System.out.println("B_vispeek: " + B_vis.peek());
        System.out.println("intersect v: " + intersect_v);
        System.out.println("end: " + end);
        System.out.println("q: "+ q);

        if( direction == "left"){

            temp = getPartPolygonLeft(P_temp, Vis_temp, intersect_v);

        }


        System.out.println("temp.size " + temp.size());
        System.out.println("P_temp peek in REC VISIBILITY: " + P_temp.peek());
        System.out.println("VIs_temp peek in REC VISIBILITY: " + Vis_temp.peek());
        System.out.println("TEMP PRINTED");
        printStackordertoPolygon(temp);

        if (temp.size() == 0) {

            if( direction == "left"){


                B_vis.push(Vis_temp.pop());
                B_vis.push(intersect_v);

            }


        } else {
            //construct polygon for recursive visibility
            if( direction == "left"){

                temp.push(intersect_v);
                temp.push(get_second_peek(Vis_temp).getPredecessor());
                temp.push(get_second_peek(Vis_temp));



            }


            System.out.println("IN POlYGON CONSTRUCTION:");
            System.out.println("intersect_v:  "+ intersect_v);
            System.out.println("end : "+ end);
            System.out.println("Vis.peek: "+ Vis_temp.peek());
            System.out.println("get second peek VIs temp:"+ get_second_peek(Vis_temp));


            Collections.reverse(temp);

            temp.push(end);

            P_rec = (Stack<Point>) temp.clone();


            B_vis.peek().setLocalBeta(beta);

            Vis_rec.push(get_second_peek(Vis_temp).getPredecessor());
            Vis_rec.push(get_second_peek(Vis_temp));

            System.out.println("P_rec before vispolygon: ");
            printStackordertoPolygon(P_rec);
            System.out.println("first element before vispolygon: ");
            System.out.println(P_rec.peek());

            System.out.println("Vis_rec before vispolygon: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("first element before vispolygon: ");
            System.out.println(Vis_rec.peek());


            System.out.println("q: " + q);
            algorithm_default(temp, Vis_rec, q);

            P_rec.remove(get_second_peek(Vis_temp));
            Vis_rec.remove(get_second_peek(Vis_temp));
            P_rec.remove(get_second_peek(Vis_temp).getPredecessor());
            Vis_rec.remove(get_second_peek(Vis_temp).getPredecessor());



            Collections.reverse(Vis_rec);


            System.out.println("P_rec: ");
            printStackordertoPolygon(P_rec);
            System.out.println("first element: ");
            System.out.println(P_rec.peek());

            System.out.println("Vis_rec: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("first element: ");
            System.out.println(Vis_rec.peek());

            //cycle polygon with new query Point q
            polygonCycle(P_rec, Vis_rec, beta, q);

            //delete Vis_temp pop as artefact, it is already included
            Vis_temp.pop();


            System.out.println("after loop");
            System.out.println("after loop");
            System.out.println("P Peek after loop: "+ P_temp.peek());
            System.out.println("Vis Peek after loop: "+ Vis_temp.peek());
            printStackordertoPolygon(B_vis);


        }
    }


    // ----------------------------------------------------------------------------------------------------------------
    // geometry helper
    // ----------------------------------------------------------------------------------------------------------------


    private Point rotatePredeccPointClockwise(double beta, Point c, Point q) {
        Point pseudo_q;

        Point p1 = c;
        Point p2 = q;

        //translate point back to origin point c:
        double px = p2.getCenterX() - p1.getCenterX();
        double py = p2.getCenterY() - p1.getCenterY();

        // rotate point
        double sin = Math.sin(Math.toRadians(beta));
        double cos = Math.cos(Math.toRadians(beta));

        //clockwise rotation
        double xnew = px * cos + py * -sin;
        double ynew = px * sin + py * cos;

        px = xnew + p1.getCenterX();
        py = ynew + p1.getCenterY();

        pseudo_q = createNode(px, py);

        return pseudo_q;
    }

    private Point rotatePredeccPointCounterClockwise(double beta, Point c, Point q) {
        Point pseudo_q;

        Point p1 = c;
        Point p2 = q;

        //translate point back to origin point c:
        double px = p2.getCenterX() - p1.getCenterX();
        double py = p2.getCenterY() - p1.getCenterY();

        // rotate point
        double sin = Math.sin(Math.toRadians(beta));
        double cos = Math.cos(Math.toRadians(beta));

        //clockwise rotation
        double xnew = px * cos + py * sin;
        double ynew = px * -sin + py * cos;

        px = xnew + p1.getCenterX();
        py = ynew + p1.getCenterY();

        pseudo_q = createNode(px, py);

        return pseudo_q;
    }


    private Point simulateRayEndPoint(Point q, Point c) {

        double m_x;
        double m_y;


        //y = mx +b;
        m_x = (c.getCenterX() - q.getCenterX()) * 0.5;
        m_y = (c.getCenterY() - q.getCenterY()) * 0.5;


        double x = c.getCenterX() + m_x;
        double y = c.getCenterY() + m_y;

//        System.out.println("x: "+ x + "y: "+ y);
        return createNode(x, y);
    }

    private double[] getGradientNormal(Point v1, Point v2) {
        double[] xy = new double[2];

        double m_x = v1.getCenterX() - v2.getCenterX();
        double m_y = v1.getCenterY() - v2.getCenterY();

        double norm = Math.sqrt(m_x * m_x + m_y * m_y);

        m_x = m_x / norm; //  / norm
        m_y = m_y / norm;//  / norm

        xy[0] = m_y;
        xy[1] = -m_x;

        return xy;
    }

    private Point[] getPointsOnNormal(Point v1, Point v2, Point v_intersect) {

        Point[] Points = new Point[2];

        double[] xy = getGradientNormal(v1, v2);

        Points[0] = createNode(v_intersect.getCenterX() + xy[0], v_intersect.getCenterY() + xy[1]);
        Points[1] = createNode(v_intersect.getCenterX() - xy[0], v_intersect.getCenterY() - xy[1]);


        return Points;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Structure -, Variable Handling
    // ----------------------------------------------------------------------------------------------------------------

    public void pushStackToStack(Stack<Point> Stack1, Stack<Point> Stack2) {

        int size = Stack1.size();

        for (int i = 0; i < size; i++) {
            Stack2.push(Stack1.pop());
        }

    }

    public void printStackorder(Stack<Point> Stack) {

        Stack<Point> temp = (java.util.Stack<Point>) Stack.clone();

        int size = Stack.size();
        for (int i = 0; i < size; i++) {
            System.out.println("number: " + i + temp.pop());
        }
    }

    public void printStackordertoPolygon(Stack<Point> Stack) {

        Stack<Point> temp = (java.util.Stack<Point>) Stack.clone();

        int size = Stack.size();
        for (int i = 0; i < size; i++) {
            System.out.println("GUI.polygon.addNode(" + temp.peek().getCenterX() + "," + temp.pop().getCenterY() + ");");
        }
    }

    @Override
    protected Point get_second_peek(Stack<Point> Stack) {
        Point prev = null;
        Point current;


        current = Stack.pop();
        if (Stack.size() >= 1) {
            prev = Stack.peek();
        }
        Stack.push(current);
        return prev;
    }

    private void clearRecQueryPointList() {
        int size = rec_c_points.size();
        for (int i = 0; i < size; i++) {
            rec_c_points.get(i).clearBetaLinkage();
        }
    }

    @Override
    protected void connectEdges(Stack<Point> B_vis) {

        javafx.scene.shape.Polygon beta_vis_polygon = new javafx.scene.shape.Polygon();


        int stack_size = B_vis.size();
        for (int i = 0; i < stack_size; i++) {
            beta_vis_polygon.getPoints().addAll(B_vis.peek().getCenterX(), B_vis.pop().getCenterY());
        }

        beta_vis_polygon.setStroke(Color.GOLD.darker());
        beta_vis_polygon.setStrokeWidth(4);

        beta_vis_polygon.setFill(Color.GOLD);
        beta_vis_polygon.setOpacity(0.4);

        GUI.betapolygonscene.getChildren().add(beta_vis_polygon);
    }


    //clears BetaVisPolygon
    public void deleteBetaVisPolygon() {
        B_vis.clear();
        GUI.betapolygonscene.getChildren().clear();
    }
}
