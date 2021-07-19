package geometry;

import javafx.scene.paint.Color;

import java.beans.Expression;
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


            // copy array so we have it for several BetaVisibility runs
            Stack<Point> P_temp = (Stack<Point>) this.P.clone();
            Stack<Point> Vis_temp = (Stack<Point>) this.Vis.clone();

            //rotate by VisPolygon calculated Stacks to have them in similar form
            Collections.rotate(Vis_temp, 1);
            Collections.rotate(P_temp, -1);


            // calculate visibility
            beta_visibility(P_temp, Vis_temp, beta);

        } else {
            System.out.println("Cannot compute Beta Visibility \n");
        }
    }

    private void beta_visibility(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta) {


        System.out.println("BETA VISIBILITY V.size:" + Vis_temp.size());
        System.out.println("BETA VISIBILITY P.size:" + P_temp.size());
        System.out.println("BETA VISIBILITY Bvis.size:" + B_vis.size());


        while (P_temp.size() != 0 && Vis_temp.size() != 0) {
            polygonCycle(P_temp, Vis_temp, beta, GUI.polygon.get_q());
            System.out.println("BETA VISIBILITY : " + GUI.polygon.get_q());
        }
        System.out.println("BETA VISIBILITY B_vis complete: ");
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


        System.out.println("POLYGONCYCLE V.size in polygoncycle:" + Vis_temp.size());
        System.out.println("POLYGONCYCLE  P_temo size in polygoncycle: " + P_temp.size());
        System.out.println("POLYGONCYCLE  Q IN POLYGONCYCLE: " + q);

        System.out.println("POLYGONCYCLE  P_temp: ");
        printStackordertoPolygon(P_temp);

        System.out.println("POLYGONCYCLE  Vis_temp: ");
        printStackordertoPolygon(Vis_temp);


        while (P_temp.size() != 0 && Vis_temp.size() != 0 && P_temp.peek() == Vis_temp.peek()) {
            System.out.println("POLYGONCYCLE  P_temp peek: " + P_temp.peek());
            System.out.println("POLYGONCYCLE  Vis_temp peek: " + Vis_temp.peek());
            B_vis.push(P_temp.pop());
            Vis_temp.pop();
            System.out.println("POLYGONCYCLE  Point ADDED BETA VIS");
            System.out.println("POLYGONCYCLE  V.size:" + Vis_temp.size());
            System.out.println("POLYGONCYCLE  P.size:" + P_temp.size());
        }

//        if (Vis_temp.size() >= 2 && get_second_peek(Vis_temp) == Vis_temp.peek()) {
//            Vis_temp.pop();
//            B_vis.push(P_temp.pop());
//            Vis_temp.pop();
//            P_temp.pop();
//        }

        try {
            System.out.println("POLYGONCYCLE  P peek out of sync: " + P_temp.peek());
            System.out.println("POLYGONCYCLE Vis peek out of sync: " + Vis_temp.peek());
            System.out.println("POLYGONCYCLE B_vis peek: " + B_vis.peek());
            System.out.println("POLYGONCYCLE B_vis peek get intersect: " + B_vis.peek().getIntersect());
        } catch (Exception e) {
            System.out.println("no corner");
        }

        System.out.println("POLYGONCYCLE B_vispeek: " + B_vis.peek().getCenterX() + " " + B_vis.peek().getCenterY() + " is corner?" + B_vis.peek().getIntersect());
        System.out.println("POLYGONCYCLE B_vispeek inner turn corner: "+ B_vis.peek().isInner_turn_corner());

        if (B_vis.peek().getIntersect() != null && B_vis.peek().isInner_turn_corner() == false && P_temp.size() != 0 && Vis_temp.size() != 0) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Right Cave");
            System.out.println("--------------------------------------------------------------------------------");
            rightCave(P_temp, B_vis, Vis_temp, beta, q);
        } else if(B_vis.peek().getIntersect() != null && B_vis.peek().isInner_turn_corner() == true && P_temp.size() != 0 && Vis_temp.size() != 0) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("FIRST Left Cave");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("POLYGONCYCLE first left cave");
            leftCave(P_temp, B_vis, Vis_temp, beta, q);
        } else if (B_vis.peek().getIntersect() == null  && P_temp.size() != 0 && Vis_temp.size() != 0) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("SECOND Left Cave");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("POLYGONCYCLE second left cave");
            leftCave(P_temp, B_vis, Vis_temp, beta, q);
        }

        while (P_temp.size() != 0 && Vis_temp.size() != 0 && P_temp.peek() == Vis_temp.peek()) {
            polygonCycle(P_temp, Vis_temp, beta, q);
        }

        System.out.println("breakpoint");
    }

    // ----------------------------------------------------------------------------------------------------------
    // Right cave functions
    // ----------------------------------------------------------------------------------------------------------


    public void rightCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {

        Point c_outerturn = B_vis.peek();

        System.out.println("RIGHTCAVE V.size:" + Vis_temp.size());
        System.out.println("RIGHTCAVE P.size:" + P_temp.size());
        System.out.println("RIGHTCAVE  corner c_outerturn : " + c_outerturn);
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), c_outerturn));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(c_outerturn, Vis_temp.peek()));

        double alpha_init = Math.abs(checkAngle(P_temp.peek(), c_outerturn, Vis_temp.peek()));
        GUI.betapolygonscene.getChildren().add(createNode(Vis_temp.peek().getCenterX(), Vis_temp.peek().getCenterY()));
        System.out.println("RIGHTCAVE Ptemp peek:" + P_temp.peek());
        System.out.println("RIGHTCAVE couter: " + c_outerturn);
        System.out.println("RIGHTCAVE getscondevis : " + get_second_peek(Vis_temp));


        System.out.println("RIGHTCAVE alpha : " + alpha_init + "Beta: " + beta);

        //recursive beta calculated from point q
        System.out.println("RIGHTCAVE q: " + q);
        System.out.println("RIGHTCAVE c outerturn: " + c_outerturn);

        if (q != GUI.polygon.get_q()) {
            Point q_parent = q.getTreeParent();
            System.out.println("RIGHTCAVE q_parent:" + q_parent);
            Point prev_end = simulateRayEndPoint(q_parent, q);
            System.out.println("RIGHTCAVE old beta :" + beta);
            beta = beta - Math.abs(checkAngle(prev_end, q, c_outerturn));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(prev_end, q));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(q, c_outerturn));
            System.out.println("RIGHTCAVE new beta set:" + beta);
        }


        if (alpha_init > beta) {
            c_outerturn.setSuccessor(P_temp.peek());
            c_outerturn.setTreeParent(q);
            q.setTreeChild(c_outerturn);
            c_outerturn.setLocalBeta(beta);

            Point intersect_v = caveIntersectRotateRight(P_temp, Vis_temp, beta, q, c_outerturn, get_second_peek(Vis_temp));
            System.out.println("RIGHTCAVE interscet in right cave: " + intersect_v);
            cycleToIntersectPoint(P_temp, intersect_v);
            System.out.println("RIGHTCAVE B_vis: " + B_vis.peek());
            System.out.println("RIGHTCAVE getsecond: " + get_second_peek(B_vis));
            partlyRecursivePolygon(P_temp, B_vis, Vis_temp, beta, c_outerturn, intersect_v, Vis_temp.peek());

        } else if (alpha_init <= beta) {

            c_outerturn.setSuccessor(P_temp.peek());
            c_outerturn.setTreeParent(q);
            q.setTreeChild(c_outerturn);
            c_outerturn.setLocalBeta(beta);
            System.out.println("RIGHTCAVE getsecond: " + get_second_peek(B_vis));
            recursivePolygonVisibility(P_temp, B_vis, Vis_temp, beta, c_outerturn, Vis_temp.peek());
        }
    }




    public Point caveIntersectRotateRight(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q, Point c, Point end) {


        Stack<Point> P_clone = (Stack<Point>) P_temp.clone();
        Point q_rotation;


        System.out.println("CAVEINTERSECTROTATE  RIGHT Start: " + B_vis.peek());
        System.out.println("CAVEINTERSECTROTATE  RIGHT end in caveintersectrotat: " + end);
        q_rotation = rotatePredeccPointClockwise(beta, c, q);
        Point intersect_v = null;
        Point temp_intersect;
        double min_range = 1000000000;


        System.out.println("CAVEINTERSECTROTATE  RIGHT P_TEMP PEEK IN CAVE INTERSECT ROTATE: " + P_clone.peek());
        System.out.println("CAVEINTERSECTROTATE  RIGHT END:: " + end);
        printStackorder(P_clone);

        while ( (P_clone.peek() != end ) && P_clone.size() >= 2) {
            System.out.println("CAVEINTERSECTROTATE  RIGHT P_temp.size in cave intersect rotate: " + P_clone.size());
            System.out.println("CAVEINTERSECTROTATE  RIGHT visible1:" + visibleAngle(P_clone.peek(), c, Vis_temp.peek()) + "visible2: " + visibleAngle(P_clone.peek(), c, get_second_peek(P_clone)));
            try{
                if( P_clone.peek() == end.getSuccessor()){
                    break;
                }
            }catch(Exception e){
                System.out.println("in loop: "+ e);
            }
                if (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true
                        && visibleAngle(P_clone.peek(), c, Vis_temp.peek())
                        && visibleAngle(P_clone.peek(), c, get_second_peek(P_clone))) {
                    System.out.println("CAVEINTERSECTROTATE  RIGHT intersection found");
                    temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone));
                    temp_intersect.setSuccessor(get_second_peek(P_clone));
                    temp_intersect.setPredecessor(P_clone.peek());
                    System.out.println("CAVEINTERSECTROTATE  RIGHT min range > inrange" + min_range +  " > " + inRange(q_rotation, temp_intersect)+ " ?");
                    if (min_range > inRange(q_rotation, temp_intersect) && visibleAngle(temp_intersect, c, Vis_temp.peek())) {
                        System.out.println("CAVEINTERSECTROTATE  RIGHT range shorter!!" + inRange(q_rotation, temp_intersect));
                        min_range = inRange(q_rotation, temp_intersect);
                        intersect_v = temp_intersect;
                    } else {
                    System.out.println("CAVEINTERSECTROTATE  RIGHT not shorter");

                }
            }
            P_clone.pop();
                if (P_clone.size()==1 && end != null && lineLineSegIntersection(q_rotation, c, P_clone.peek(), end) == true) {
                    System.out.println("CAVEINTERSECTROTATE  RIGHT intersection found");
                    temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), end);
                    temp_intersect.setSuccessor(get_second_peek(P_clone));
                    temp_intersect.setPredecessor(P_clone.peek());
                    System.out.println("CAVEINTERSECTROTATE  RIGHT min range > inrange" + min_range + " > " + inRange(q_rotation, temp_intersect) + " ?");
                    System.out.println("CAVEINTERSECTROTATE  RIGHT visibleangle 1: "+ visibleAngle(temp_intersect,c, end));
                    if (min_range > inRange(q_rotation, temp_intersect)) {
                        System.out.println("CAVEINTERSECTROTATE  RIGHT range shorter!!" + inRange(q_rotation, temp_intersect));
                        min_range = inRange(q_rotation, temp_intersect);
                        intersect_v = temp_intersect;
                        System.out.println("CAVEINTERSECTROTATE  RIGHT end" + end);
                        System.out.println("CAVEINTERSECTROTATE  RIGHT peek: " + P_clone.peek());
                    }
                }
        }

        System.out.println("CAVEINTERSECTROTATE  RIGHT intersect linkage null?" + intersect_v.getPredecessor() + " " + intersect_v.getSuccessor());
        System.out.println();
        return intersect_v;
    }


    protected void cycleToIntersectPoint(Stack<Point> P_temp, Point intersect_v) {


        System.out.println("CYCLETOINTERSECTPOINT intersect _ v " + intersect_v);
        System.out.println("CYCLETOINTERSECTPOINT intersect predecc:" + intersect_v.getPredecessor());
        System.out.println("CYCLETOINTERSECTPOINT intersect succ:" + intersect_v.getSuccessor());


        while (P_temp.peek() != intersect_v.getPredecessor()) {
            P_temp.pop();
            System.out.println("CYCLETOINTERSECTPOINT in cycleintersectloop");
        }

        System.out.println("cycled to: "+ P_temp.peek());
    }

    protected Stack<Point> getPartPolygonRight(Stack<Point> P_temp, Stack<Point> Vis_temp, Point end) {
        Stack<Point> temp = new Stack<>();


//        try{
//            if(get_second_peek(Vis_temp).getCorner().isInner_turn_corner() ==true){
//                end = get_second_peek(Vis_temp).getCorner();
//                end.setSuccessor(null);
//            }
//        }catch (Exception e){
//            System.out.println("GETPARTPOLYGONRIGHT tried get croner inner turn boolean");
//        }


        //get second mit end ersetzen
//      while ((P_temp.size() != 0 && Vis_temp.size() == 1) || ( P_temp.size() != 0 && P_temp.peek() != get_second_peek(Vis_temp)) ) {

        while ((P_temp.size() != 0) && (Vis_temp.size() == 1 || P_temp.peek() != end )) {
            try {
                if (P_temp.peek() == end.getSuccessor()) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("GETPARTPOLYGONRIGHT no get second peek vis in get part polygon right");
            }
            System.out.println("GETPARTPOLYGONRIGHT P_clone: " + P_temp.peek());
            temp.push(P_temp.pop());
        }
        System.out.println("GETPARTPOLYGONRIGHT finish at: " + P_temp.peek());
        return temp;
    }

    protected Stack<Point> getWholePolygonRight(Stack<Point> P_temp, Stack<Point> Vis_temp, Point end) {

        Stack<Point> temp = new Stack<>();

        System.out.println("GETWHOLEPOLYGONRIGHT  getsecond vis: " + get_second_peek(Vis_temp));

        while (P_temp.size() != 0 && P_temp.peek() != get_second_peek(Vis_temp)) {
            try {
                if (P_temp.peek() == end.getSuccessor()) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("GETWHOLEPOLYGONRIGHT  no get second peek vis in get whole polygon right");
            }
            System.out.println("GETWHOLEPOLYGONRIGHT  P_clone: " + P_temp.peek());
            temp.push(P_temp.pop());
        }
        return temp;
    }



    protected void partlyRecursivePolygon(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        System.out.println("PARTYLRECURSIVEPOLYGON B_vispeek: " + B_vis.peek());
        System.out.println("PARTYLRECURSIVEPOLYGON intersect v: " + intersect_v);
        System.out.println("PARTYLRECURSIVEPOLYGON end: " + end);
        System.out.println("PARTYLRECURSIVEPOLYGON q: " + q);
        System.out.println("PARTYLRECURSIVEPOLYGON vis peek:"+ Vis_temp.peek());
        System.out.println("PARTYLRECURSIVEPOLYGON vis second peek:"+ get_second_peek(Vis_temp));
        System.out.println("PARTYLRECURSIVEPOLYGON vis peek corner :"+ Vis_temp.peek().getCorner());
        try{
            System.out.println("PARTYLRECURSIVEPOLYGON vis second peek corner:"+ get_second_peek(Vis_temp).getCorner());
            System.out.println("PARTYLRECURSIVEPOLYGON vis second peek corner inner corner:"+ get_second_peek(Vis_temp).getCorner().isInner_turn_corner());
        }catch (Exception e){
            System.out.println("tried inner turn booolean");
        }

        P_temp.pop();

        temp = getPartPolygonRight(P_temp, Vis_temp, end);


        System.out.println("PARTYLRECURSIVEPOLYGON ORIGINAL TEMP:");
        printStackorder(temp);
        System.out.println();


        if (temp.size() == 0) {


            B_vis.push(intersect_v);
            B_vis.push(Vis_temp.pop());


        } else {
            //construct polygon for recursive visibility


            System.out.println("PARTYLRECURSIVEPOLYGON end: " + end);
            System.out.println("PARTYLRECURSIVEPOLYGON Vis_temp.peek: " + Vis_temp.peek());
            temp.push(Vis_temp.pop());
            temp.push(B_vis.peek());
            temp.push(intersect_v);


            Collections.rotate(temp, 1);
            Collections.reverse(temp);

            P_rec = (Stack<Point>) temp.clone();


            B_vis.peek().setLocalBeta(beta);

            Vis_rec.push(end);
            Vis_rec.push(B_vis.peek());

            System.out.println("PARTYLRECURSIVEPOLYGON P_rec: ");
            printStackordertoPolygon(P_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON first element: ");
            System.out.println(P_rec.peek());

            System.out.println("PARTYLRECURSIVEPOLYGON Vis_rec: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON first element: ");
            System.out.println(Vis_rec.peek());

            algorithm_default(temp, Vis_rec, q);

            System.out.println("PARTYLRECURSIVEPOLYGON P_rec: ");
            printStackordertoPolygon(P_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON first element: ");
            System.out.println(P_rec.peek());

            System.out.println("PARTYLRECURSIVEPOLYGON Vis_rec: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON first element: ");
            System.out.println(Vis_rec.peek());

            // remove point q, because they are already added or obsolete for the polygon
            P_rec.remove(q);
            Vis_rec.remove(q);




            Collections.reverse(Vis_rec);
            Collections.rotate(Vis_rec, 1);



            //cycle polygon with new query Point q
            polygonCycle(P_rec, Vis_rec, beta, q);


            System.out.println("PARTYLRECURSIVEPOLYGON after loop");
            System.out.println("PARTYLRECURSIVEPOLYGON after loop");
            printStackordertoPolygon(B_vis);


        }
    }


    protected void recursivePolygonVisibility(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point end) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();






        //construct polygon for recursive visibility
        temp = getWholePolygonRight(P_temp, Vis_temp, end);

        System.out.println("RECURSIVEPOLYGONVISIBILITY ORIGINAL TEMP:");
        printStackorder(temp);
        System.out.println();





        try{
            System.out.println("temp peek :"+ temp.peek());
            System.out.println("temp peek corner :"+ temp.peek().getCorner());
            System.out.println("temp peek croner inner turn :"+ temp.peek().getCorner().isInner_turn_corner());
        } catch(Exception e){
            System.out.println(e);
        }


//        System.out.println("RECURSIVEPOLYGONVISIBILITY P peek: "+ P_temp.peek());
        System.out.println("RECURSIVEPOLYGONVISIBILITY B_vispeek: " + B_vis.peek());
        System.out.println("RECURSIVEPOLYGONVISIBILITY q in recursivepolygonvis: " + q);
        System.out.println("RECURSIVEPOLYGONVISIBILITY end: " + end);
        System.out.println("RECURSIVEPOLYGONVISIBILITY end successor : " + end.getSuccessor());
        System.out.println("RECURSIVEPOLYGONVISIBILITY end predecessor : " + end.getPredecessor());
        System.out.println("RECURSIVEPOLYGONVISIBILITY Vis_temp popped: " + Vis_temp.peek());
        temp.push(end);
        temp.push(Vis_temp.pop());
        temp.push(B_vis.peek());


        Collections.rotate(temp, 1);
        Collections.reverse(temp);
        printStackordertoPolygon(temp);

        P_rec = (Stack<Point>) temp.clone();


        System.out.println("RECURSIVEPOLYGONVISIBILITY  Vis_temp");
        printStackordertoPolygon(Vis_temp);

        B_vis.peek().setLocalBeta(beta);

        System.out.println("RECURSIVEPOLYGONVISIBILITY pushing end on Vis_rec: "+ end);
        System.out.println("RECURSIVEPOLYGONVISIBILITY pushing B_vis on Vis_rec: "+ B_vis.peek());
        Vis_rec.push(end);
        Vis_rec.push(B_vis.peek());


        Collections.reverse(P_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY P_REC DUPLICATE CHECK");
        printStackorder(P_rec);
        Collections.reverse(P_rec);


        System.out.println("RECURSIVEPOLYGONVISIBILITY q before algorithm_default: " + q);
        System.out.println("RECURSIVEPOLYGONVISIBILITY end before algorithm_default:"+end);
        System.out.println("RECURSIVEPOLYGONVISIBILITY P_rec: ");
        printStackorder(P_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY Vis_rec: ");
        printStackorder(Vis_rec);


        algorithm_default(temp, Vis_rec, q);

        System.out.println("RECURSIVEPOLYGONVISIBILITY P_rec: ");
        printStackordertoPolygon(P_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY first element: ");
        System.out.println(P_rec.peek());

        System.out.println("RECURSIVEPOLYGONVISIBILITY Vis_rec: ");
        printStackordertoPolygon(Vis_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY first element: ");
        System.out.println(Vis_rec.peek());


        System.out.println("RECURSIVEPOLYGONVISIBILITY vis rec peek: " + Vis_rec.peek());

        // remove point q, because they are already added or obsolete for the polygon
        System.out.println("RECURSIVEPOLYGONVISIBILITY P_rec remove q: "+ q);
        System.out.println("RECURSIVEPOLYGONVISIBILITY Vis_rec remove q: "+ q);
        System.out.println("RECURSIVEPOLYGONVISIBILITY Vis_rec remove B_vis.peek: "+ B_vis.peek());
        P_rec.remove(q);
//        P_rec.remove(end);
        Vis_rec.remove(q);
        Vis_rec.remove(B_vis.peek());




        Collections.reverse(Vis_rec);
        Collections.rotate(Vis_rec, 1);



        polygonCycle(P_rec, Vis_rec, beta, q);

    }


    // ----------------------------------------------------------------------------------------------------------
    // Left Cave functions
    // ----------------------------------------------------------------------------------------------------------


    public void leftCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {
        // B_vis.peek as point before intersection Point of inner turn
        // P_temp peek is the next point in non visible cave
        // Vis peek is inner turn intersect, get second peek vis is the corner point of the cave.


        Point c_innerturn = Vis_temp.peek().getCorner();
        Point c_innerturn_prev = c_innerturn.getPredecessor();
        System.out.println("LEFTCAVE c inner turn: " + c_innerturn);
        System.out.println("LEFTCAVE predecessor: " + c_innerturn_prev);
        System.out.println("LEFTCAVE VIS TEMP PEEK BEFORE ALPHA: " + Vis_temp.peek());

        //get cave beginning and calculate angle:

        double alpha = Math.abs(checkAngle(Vis_temp.peek(), c_innerturn, c_innerturn_prev));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(Vis_temp.peek(), c_innerturn));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(c_innerturn, c_innerturn_prev));


        System.out.println("LEFTCAVE alpha: " + alpha + "beta: " + beta);
        System.out.println("LEFTCAVE c predecessor: " + c_innerturn_prev);


        if (q != GUI.polygon.get_q()) {
            Point q_parent = q.getTreeParent();
            System.out.println("LEFTCAVE q:" + q );
            System.out.println("LEFTCAVE q_parent:" + q_parent);
            Point prev_end = simulateRayEndPoint(q_parent, q);
            System.out.println("LEFTCAVE old beta :" + beta);

            beta = beta - Math.abs(checkAngle(prev_end, q, c_innerturn));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(prev_end, q));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(q, c_outerturn));
            System.out.println("LEFTCAVE new beta set:" + beta);
        }


        if (alpha > beta) {


            Point Vis_peek = Vis_temp.pop();
            c_innerturn.setSuccessor(Vis_peek.getSuccessor());
            Vis_temp.push(Vis_peek);
            c_innerturn.setTreeParent(q);
            q.setTreeChild(c_innerturn);
            c_innerturn.setLocalBeta(beta);


            System.out.println("LEFTCAVE c_innterturn prev: " + c_innerturn_prev);
            Point intersect_v = caveIntersectRotateLeft(P_temp, Vis_temp, beta, q, c_innerturn, c_innerturn_prev);
            System.out.println("LEFTCAVE intersect v: " + intersect_v);

            System.out.println("LEFTCAVE c_innerturn as q: " + c_innerturn);
            System.out.println("LEFTCAVE VIS TEMP PEEK IN LEFT CAVE: " + Vis_temp.peek());
            partlyRecursivePolygonLEFT(P_temp, B_vis, Vis_temp, beta, c_innerturn, intersect_v, Vis_temp.peek());

        } else if (alpha <= beta) {

            c_innerturn.setSuccessor(P_temp.peek());
            c_innerturn.setTreeParent(q);
            q.setTreeChild(c_innerturn);
            c_innerturn.setLocalBeta(beta);
            recursivePolygonVisibilityLeft(P_temp, B_vis, Vis_temp, beta, c_innerturn, Vis_temp.peek());
        }
    }


    public Point caveIntersectRotateLeft(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q, Point c, Point end) {


        Stack<Point> P_clone = (Stack<Point>) P_temp.clone();
        Point q_rotation;


        System.out.println("CAVEINTERSECTROTATE LEFT Start: " + B_vis.peek());
        System.out.println("CAVEINTERSECTROTATE LEFT end in caveintersectrotat: " + end);
        System.out.println("CAVEINTERSECTROTATE LEFT counterclockwise rotation ");
        q_rotation = rotatePredeccPointCounterClockwise(beta, c, q);
        P_clone.push(B_vis.peek());
        Point intersect_v = null;
        Point temp_intersect;
        double min_range = 1000000000;


        System.out.println("CAVEINTERSECTROTATE LEFT P_TEMP PEEK IN CAVE INTERSECT ROTATE: " + P_clone.peek());
        System.out.println("CAVEINTERSECTROTATE LEFT END:: " + end);

        while (P_clone.peek() != end && P_clone.size() >= 2) {
            if (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true
//                        && (visibleAngle(Vis_temp.peek(), c, get_second_peek(P_clone)))
                    && visibleAngle(P_clone.peek(), c, get_second_peek(P_clone))) {
                System.out.println("CAVEINTERSECTROTATE LEFT intersection found");
                temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone));
                temp_intersect.setSuccessor(get_second_peek(P_clone));
                temp_intersect.setPredecessor(P_clone.peek());
                System.out.println("CAVEINTERSECTROTATE LEFT min range > inrange" + min_range + " > " + inRange(q_rotation, temp_intersect)+ " ?");
                if (min_range > inRange(q_rotation, temp_intersect) && visibleAngle(Vis_temp.peek(), c, temp_intersect)) {
//                    if (min_range > inRange(q_rotation, temp_intersect) ) {
                    System.out.println("CAVEINTERSECTROTATE LEFT range shorter!!" + inRange(q_rotation, temp_intersect));
                    min_range = inRange(q_rotation, temp_intersect);
                    intersect_v = temp_intersect;
                } else {
                    System.out.println("CAVEINTERSECTROTATE LEFT line intersect: " + (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true));
                    System.out.println("CAVEINTERSECTROTATE LEFT visible angle between vistemp: " + (visibleAngle(Vis_temp.peek(), c, get_second_peek(P_clone)) || visibleAngle(Vis_temp.peek(), c, P_clone.peek())));
                    System.out.println("CAVEINTERSECTROTATE LEFT visible angle between c and edgepoints: " + visibleAngle(P_clone.peek(), c, get_second_peek(P_clone)));
                    System.out.println("CAVEINTERSECTROTATE LEFT line intersect: " + (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true));
                    System.out.println("CAVEINTERSECTROTATE LEFT not shorter!!");
                }
            }
            P_clone.pop();
        }
        GUI.polygonscene.getChildren().add(createNode(intersect_v.getCenterX(), intersect_v.getCenterY()));
        System.out.println("CAVEINTERSECTROTATE LEFT intersect linkage null?" + intersect_v.getPredecessor() + " " + intersect_v.getSuccessor());
        System.out.println();
        return intersect_v;
    }

    protected Stack<Point> getPartPolygonLeft(Stack<Point> P_temp, Stack<Point> Vis_temp, Point intersect_v) {

        Stack<Point> temp = new Stack<>();

        //gets Points until intersection Point
        while (P_temp.peek() != intersect_v.getSuccessor()) {
            System.out.println("GETPARTPOLYGON LEFT P_clone: " + P_temp.peek());
            temp.push(P_temp.pop());
        }

        System.out.println("GETPARTPOLYGON LEFT Points until intersect pushed");
        System.out.println("GETPARTPOLYGON LEFT pushed intersect_v on temp: "+ intersect_v);
        temp.push(intersect_v);

        //deletes all Points until inner corner Point
//        while (P_temp.peek() != get_second_peek(Vis_temp)) {

        while ((P_temp.size() != 0) && (Vis_temp.size() == 1 || P_temp.peek() != get_second_peek(Vis_temp))) {
            System.out.println("GETPARTPOLYGON LEFT P_clone: " + P_temp.peek());
            P_temp.pop();
        }

        return temp;
    }


    protected Stack<Point> getWholePolygonLeft(Stack<Point> P_temp, Stack<Point> Vis_temp, Point c_innerturn) {

        Stack<Point> P_rec = new Stack<>();
        Point end = Vis_temp.peek();


        P_rec.push(end);

        while (P_temp.peek() != c_innerturn) {
            System.out.println("GETWHOLEPOLYGON LEFT P_clone: " + P_temp.peek());
            P_rec.push(P_temp.pop());
        }


        printStackordertoPolygon(P_rec);
        System.out.println("stop");

        return P_rec;
    }

    protected void partlyRecursivePolygonLEFT(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();




            temp = getPartPolygonLeft(P_temp, Vis_temp, intersect_v);



        System.out.println("PARTYLRECURSIVEPOLYGON LEFT temp.size " + temp.size());
        System.out.println("PARTYLRECURSIVEPOLYGON LEFT P_temp peek in REC VISIBILITY: " + P_temp.peek());
        System.out.println("PARTYLRECURSIVEPOLYGON LEFT VIs_temp peek in REC VISIBILITY: " + Vis_temp.peek());
        System.out.println("PARTYLRECURSIVEPOLYGON LEFT TEMP PRINTED");
        printStackordertoPolygon(temp);
        Point c_innerturn = end.getCorner();
        Point c_innerturn_prev = c_innerturn.getPredecessor();
        System.out.println("PARTYLRECURSIVEPOLYGON LEFT c_innerturn:" + c_innerturn);
        System.out.println("PARTYLRECURSIVEPOLYGON LEFT c_innerturn:" + c_innerturn.getPredecessor());


        if (temp.size() == 0) {



                B_vis.push(Vis_temp.pop());
                B_vis.push(intersect_v);



        } else {
            //construct polygon for recursive visibility

                System.out.println("PARTYLRECURSIVEPOLYGON LEFT ORIGINAL TEMP:");
                printStackorder(temp);
                System.out.println();


                System.out.println("PARTYLRECURSIVEPOLYGON LEFT B_vispeek: " + B_vis.peek());
                System.out.println("PARTYLRECURSIVEPOLYGON LEFT intersect v: " + intersect_v);
                System.out.println("PARTYLRECURSIVEPOLYGON LEFT end: " + end);
                System.out.println("PARTYLRECURSIVEPOLYGON LEFT q: " + q);

                temp.push(intersect_v);
//                temp.push(get_second_peek(Vis_temp).getPredecessor());
//                temp.push(get_second_peek(Vis_temp));
                temp.push(c_innerturn_prev);
                temp.push(c_innerturn);





            System.out.println("PARTYLRECURSIVEPOLYGON LEFT IN POlYGON CONSTRUCTION:");
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT intersect_v:  " + intersect_v);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT end : " + end);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT Vis.peek: " + Vis_temp.peek());
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT get second peek VIs temp:" + get_second_peek(Vis_temp));


            Collections.reverse(temp);

            temp.push(end);

            P_rec = (Stack<Point>) temp.clone();


            B_vis.peek().setLocalBeta(beta);

            Vis_rec.push(c_innerturn_prev);
            Vis_rec.push(c_innerturn);

            System.out.println("PARTYLRECURSIVEPOLYGON LEFT P_rec before vispolygon: ");
            printStackordertoPolygon(P_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT first element before vispolygon: ");
            System.out.println(P_rec.peek());

            System.out.println("PARTYLRECURSIVEPOLYGON LEFT Vis_rec before vispolygon: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT first element before vispolygon: ");
            System.out.println(Vis_rec.peek());


            System.out.println("PARTYLRECURSIVEPOLYGON LEFT q: " + q);
            algorithm_default(temp, Vis_rec, q);

            System.out.println("PARTYLRECURSIVEPOLYGON LEFT remove c_innerturn:" + c_innerturn);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT remove c_innerturn predecessor:" + c_innerturn.getPredecessor());

            P_rec.remove(c_innerturn);
            Vis_rec.remove(c_innerturn);
            P_rec.remove(c_innerturn_prev);
            Vis_rec.remove(c_innerturn_prev);


            Collections.reverse(Vis_rec);


            System.out.println("PARTYLRECURSIVEPOLYGON LEFT P_rec: ");
            printStackordertoPolygon(P_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT first element: ");
            System.out.println(P_rec.peek());

            System.out.println("PARTYLRECURSIVEPOLYGON LEFT Vis_rec: ");
            printStackordertoPolygon(Vis_rec);
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT first element: ");
            System.out.println(Vis_rec.peek());

            //cycle polygon with new query Point q
            polygonCycle(P_rec, Vis_rec, beta, q);

            //delete Vis_temp pop as artefact, it is already included
            System.out.println("PARTYLRECURSIVEPOLYGON delete Vis temp as artifect pop: "+ Vis_temp.peek());
            Vis_temp.pop();


            System.out.println("PARTYLRECURSIVEPOLYGON LEFT after loop");
            System.out.println("PARTYLRECURSIVEPOLYGON LEFT after loop");


        }
    }


    protected void recursivePolygonVisibilityLeft(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point end) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT B_vispeek: " + B_vis.peek());
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT q in recursivepolygonvis: " + q);
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT end: " + end);



            //construct polygon for recursive visibility

            temp = getWholePolygonLeft(P_temp, Vis_temp, q);


        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT ORIGINAL TEMP:");
        printStackorder(temp);
        System.out.println();


        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT push second vis temp on temp:"+ get_second_peek(Vis_temp));
        temp.push(get_second_peek(Vis_temp));
//        Collections.rotate(temp, 1);
        Collections.reverse(temp);


        P_rec = (Stack<Point>) temp.clone();


        B_vis.peek().setLocalBeta(beta);

        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT add second Vis_temp:");
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT add second Vis_temp predecessor:");
        Vis_rec.push(get_second_peek(Vis_temp).getPredecessor());
        Vis_rec.push(get_second_peek(Vis_temp));

        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT P_rec before vispolygon: ");
        printStackordertoPolygon(P_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT first element before vispolygon: ");
        System.out.println(P_rec.peek());

        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT Vis_rec before vispolygon: ");
        printStackordertoPolygon(Vis_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT first element before vispolygon: ");
        System.out.println(Vis_rec.peek());


        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT q: " + q);
        algorithm_default(temp, Vis_rec, q);

        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT remove second Vis_temp:");
        P_rec.remove(get_second_peek(Vis_temp));
        Vis_rec.remove(get_second_peek(Vis_temp));
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT delete secondpeek.predecessor: " + get_second_peek(Vis_temp).getPredecessor());
//        P_rec.remove(get_second_peek(Vis_temp).getPredecessor());
        Vis_rec.remove(get_second_peek(Vis_temp).getPredecessor());


        Collections.reverse(Vis_rec);


        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT P_rec: ");
        printStackordertoPolygon(P_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT first element: ");
//        System.out.println(P_rec.peek());

        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT Vis_rec: ");
        printStackordertoPolygon(Vis_rec);
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT first element: ");
//        System.out.println(Vis_rec.peek());

        //cycle polygon with new query Point q
        polygonCycle(P_rec, Vis_rec, beta, q);

        //delete Vis_temp pop as artefact, it is already included
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT delete Vis temp as artifect pop: "+ Vis_temp.peek());
        Vis_temp.pop();
        System.out.println("RECURSIVEPOLYGONVISIBILITY push vistemp predecessor on B_vis: "+ Vis_temp.peek().getPredecessor());
        B_vis.push(Vis_temp.peek().getPredecessor());


        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT after loop");
        System.out.println("RECURSIVEPOLYGONVISIBILITY LEFT after loop");
        printStackordertoPolygon(B_vis);

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


    // ----------------------------------------------------------------------------------------------------------------
    // Structure -, Variable Handling
    // ----------------------------------------------------------------------------------------------------------------


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
        betavis = false;
    }
}
