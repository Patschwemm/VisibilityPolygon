package geometry;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Stack;

public class BetaVis extends VisPolygon {

    private final Stack<Point> P = GUI.vis_q.getBetavis_P();
    private final Stack<Point> Vis = GUI.vis_q.getBetavis_Vis();
    private final Stack<Point> B_vis = new Stack<Point>();
    private final ArrayList<Point> rec_c_points = new ArrayList<>();
    private final double angle_sum = 0;
    private Point vi_prev;


    public BetaVis(double beta) {
        //check if Vis Polygon is computed
        if (GUI.vis_q != null) {

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("BETA: " + beta);
            System.out.println("--------------------------------------------------------------------------------");
            // preprocessing
            // ----------------------------------------


            System.out.println("P in beta:" + P.size());
            System.out.println("Vis in beta:" + Vis.size());
            // copy array so we have it for several BetaVisibility runs
            Stack<Point> P_temp = (Stack<Point>) this.P.clone();
            Stack<Point> Vis_temp = (Stack<Point>) this.Vis.clone();
            System.out.println("P_temp in beta:" + P_temp.size());
            System.out.println("Vis_temp in beta:" + Vis_temp.size());

            //pre processing of Stack P
            P_temp = reorderP_temp(P_temp);
            System.out.println(P_temp.peek());
            System.out.println(Vis_temp.peek());
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(Vis_temp.peek())).setFill(Color.WHITE);
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P_temp.peek())).setFill(Color.WHITE);


            System.out.println("P_temp.size:" + P_temp.size());
            System.out.println("V_temp.size:" + Vis_temp.size());


            // calculate visibility
            // ----------------------------------------
            beta_visibility(P_temp, Vis_temp, beta);

        } else {
            System.out.println("Cannot compute Beta Visibility \n");
        }
    }

    private void beta_visibility(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta) {
//        Point v_start = Vis_temp.peek();

        //start condition, push the two visible starting points
//        B_vis.push(P_temp.pop());
//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.GREEN);
//        B_vis.push(P_temp.pop());
//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.GREEN);
//        Vis_temp.pop();
//        Vis_temp.pop();


        System.out.println(P_temp.peek());
        System.out.println(Vis_temp.peek());
        System.out.println(P.firstElement());

        System.out.println("V.size:" + Vis_temp.size());
        System.out.println("P.size:" + P_temp.size());
        System.out.println("Bvis.size:" + B_vis.size());


        while (P_temp.size() != 0 && Vis_temp.size() != 0) {
            PolygonCycle(P_temp, Vis_temp, beta);
            System.out.println("polygoncycle entered");
        }
        connectEdges(B_vis);
        clearRecQueryPointList();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    private void PolygonCycle(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta) {


        System.out.println("V.size in polygoncycle:" + Vis_temp.size());
        System.out.println("P_temo size in polygoncycle: " + P_temp.size());

        while (P_temp.size() != 1 && Vis_temp.size() != 0 && P_temp.peek() == Vis_temp.peek()) {
            B_vis.push(P_temp.pop());
            Vis_temp.pop();
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Point ADDED BETA VIS");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
            System.out.println("Bvis.size:" + B_vis.size());


        }
        if (B_vis.peek().isCorner() == true) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Right Cave");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
            System.out.println("Bvis.size:" + B_vis.size());
            RightCave(P_temp, B_vis, Vis_temp, beta);
        } else if (B_vis.peek().isCorner() == false) {
            LeftCave(P_temp, B_vis, Vis_temp, beta);
        }
//        System.out.println(P_temp.peek());
//        System.out.println(Vis_temp.peek());
//        System.out.println(B_vis.peek());
//        if(Vis_temp.size() != 0  && P_temp.peek() != Vis_temp.peek()){

        if ((Vis_temp.size() != 0 || P_temp.size() != 1)) {
            Vis_temp.pop();
        }

//        System.out.println(P_temp.peek());
//        System.out.println(Vis_temp.peek());
//        System.out.println(B_vis.peek());

    }


    public void RightCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta) {

        Point c_outerturn = B_vis.peek();

        System.out.println("V.size:" + Vis_temp.size());
        System.out.println("P.size:" + P_temp.size());
        System.out.println("Bvis.size:" + B_vis.size());
        System.out.println("right cave corner c_outerturn : " + c_outerturn);
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), c_outerturn));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(c_outerturn, Vis_temp.peek()));

        double alpha_init = Math.abs(checkAngle(P_temp.peek(), c_outerturn, Vis_temp.peek()));
        GUI.betapolygonscene.getChildren().add(createNode(Vis_temp.peek().getCenterX(), Vis_temp.peek().getCenterY()));
        System.out.println(P_temp.peek());
        System.out.println(c_outerturn);
        System.out.println(get_second_peek(Vis_temp));

        //predecessor is q, so that we get the artificial edge
        c_outerturn.setPredecessor(GUI.polygon.get_q());
        c_outerturn.setSuccessor(P_temp.peek());


        System.out.println("alpha : " + alpha_init + "Beta: " + beta);

        Point rotation = rotatePredeccPointClockwise(beta, c_outerturn, GUI.polygon.get_q());

        GUI.polygonscene.getChildren().add(rotation);

        rec_c_points.add(c_outerturn);

        if (alpha_init > beta) {
            forwardIntersect(P_temp, B_vis, rotatePredeccPointClockwise(beta, c_outerturn, GUI.polygon.get_q()), c_outerturn);
//            B_vis.peek().setLinkedtocorner(c_outerturn);
//            B_vis.peek().setTreeParent(c_outerturn);
//            c_outerturn.setTreeChild(B_vis.peek());
            System.out.println("remaining beta: " + beta);
            c_outerturn.setLocalBeta(beta);
            RecursiveVisibility(P_temp, B_vis, Vis_temp, beta, c_outerturn, c_outerturn, Vis_temp.peek());
        } else if (alpha_init <= beta) {
            B_vis.push(P_temp.pop());
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.BLUE);
//            B_vis.peek().setLinkedtocorner(c_outerturn);
//            B_vis.peek().setTreeParent(c_outerturn);
//            c_outerturn.setTreeChild(B_vis.peek());
            System.out.println("remaining beta: " + beta);
            c_outerturn.setLocalBeta(beta);
            RecursiveVisibility(P_temp, B_vis, Vis_temp, beta, c_outerturn, c_outerturn, Vis_temp.peek());
        }
    }


    public void LeftCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta) {

    }


    public void RecursiveVisibility(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point c_outerturn, Point start, Point end) {

        //c_outerturn is q

//        while (lineLineSegIntersection(start, end, P.peek(), get_second_peek(P)) == false){
        System.out.println(" in REC VIS V.size:" + Vis_temp.size());
        System.out.println(" in REC VIS P.size:" + P_temp.size());
        System.out.println(" in REC VIS Bvis.size:" + B_vis.size());
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(start, end));
        while (P_temp.size() >= 1 && lineLineSegIntersection(start, end, B_vis.peek(), P_temp.peek()) == false) {
            if (getEvent(beta, B_vis.peek(), c_outerturn, P_temp.peek(), get_second_peek(B_vis), end, P_temp, B_vis, Vis_temp) == false) {
                System.out.println("in LOOOP");
//                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.BLUE);
                GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(B_vis.peek(), P_temp.peek()));
                System.out.println("P.peek in recvis" + P_temp.peek());
                B_vis.push(P_temp.pop());
                B_vis.peek().setLinkedtocorner(c_outerturn);
                System.out.println("beta in recvis: "+ beta);
            }
        }
        System.out.println("P_temp size in rec vis ENDING " + P_temp.size());
//        if(P_temp.size() != 0){
//            B_vis.push(P_temp.pop());
//            B_vis.peek().setLinkedtocorner(c_outerturn);
//        }
    }


    private void OuterRightTurn(double beta, Point q, Point end, Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp) {

        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(q, B_vis.peek()));

//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(end, q));
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(q, B_vis.peek()));

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("OUTER RIGHT TURN");
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("beta: "+ beta + "checkangle beta: "+ checkAngle( B_vis.peek(),q ,end) );
        double beta_chain = beta - Math.abs(checkAngle( B_vis.peek(),q ,end));
        Point c_outerturn = B_vis.peek();
        end = simulateRayEndPoint(q, c_outerturn);
        double alpha = Math.abs(checkAngle(P_temp.peek(), B_vis.peek(), end));
        System.out.println("outer turn corner: " + c_outerturn);
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), c_outerturn));
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(c_outerturn, end));
        System.out.println("END: " + end);


        rec_c_points.add(c_outerturn);
        System.out.println("alpha: " + alpha + "beta: " + beta_chain);
        if (alpha > beta_chain) {
            c_outerturn.setPredecessor(get_second_peek(B_vis));
            c_outerturn.setSuccessor(P_temp.peek());
            q.setTreeChild(c_outerturn);
            c_outerturn.setTreeParent(q);
            c_outerturn.setLocalBeta(beta_chain);
            System.out.println("OUTER RIGHT TURN PREDECESSOR SET:  "+ get_second_peek(B_vis));
            System.out.println("OUTER RIGHT TURN SET:  "+ c_outerturn);
            System.out.println("Outer RIght Turn Forward Intersect");
            forwardIntersect(P_temp, B_vis, rotatePredeccPointClockwise(beta_chain, c_outerturn, q), B_vis.peek());
            RecursiveVisibility(P_temp, B_vis, Vis_temp, beta_chain, c_outerturn, c_outerturn, end);
        } else if (alpha <= beta_chain) {
            System.out.println("Outer Right Turn Recursive Veisbility");
// TODO ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            c_outerturn.setPredecessor(get_second_peek(B_vis));
            c_outerturn.setSuccessor(P_temp.peek());
            q.setTreeChild(c_outerturn);
            c_outerturn.setTreeParent(q);
//            beta_chain = beta_chain - alpha;
            c_outerturn.setLocalBeta(beta_chain);
            B_vis.push(P_temp.pop());
            B_vis.peek().setLinkedtocorner(c_outerturn);
            RecursiveVisibility(P_temp, B_vis, Vis_temp, beta_chain, c_outerturn, c_outerturn, end);
        }
    }

    private void InnerTurnChain(double beta, Point c, Point end, Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp) {
        double beta_chain = beta;
        //P.peek = c, one inner turn has been identified and can be skipped already
        // c = q query point

//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(c)).setFill(Color.PINK);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("In Inner Turn Chain");
        System.out.println("--------------------------------------------------------------------------------");


        // pushes chainelements to chain
        while (ChainVertex(B_vis.peek(), c, P_temp.peek(), get_second_peek(B_vis), P_temp, B_vis) == true) {
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.YELLOW);
            B_vis.push(P_temp.pop());
            System.out.println("chainvertex: "+ (ChainVertex(B_vis.peek(), c, P_temp.peek(), get_second_peek(B_vis), P_temp, B_vis) == true));
            B_vis.peek().setLinkedtocorner(c);
        }

        //because the ending is always an innerturn
        System.out.println("INNER TURN CHAIN END:" + B_vis.peek());
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.BROWN);


        // TODO ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        System.out.println(" INNER TURN CHAIN BEFORE end: "+ end);
        System.out.println(" INNER TURN c: "+ c);

        //check if chain is out of recursion area
        Point v_chainend = B_vis.peek();
        Point c_current = CheckRecursionArea(Vis_temp, B_vis, beta, c, end);

        // TODO ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        end = simulateRayEndPoint(c_current.getPredecessor(), c_current);
        System.out.println("C_current predecessor: "+ c_current.getPredecessor());
        System.out.println("C_current: "+ c_current);

        // TODO ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        System.out.println(" INNER TURN CHAIN AFTER end: "+ end);
        System.out.println(" INNER TURN CHAIN AFTER c: "+ c_current);


        //innerTurn deletes all necessary angles


        System.out.println("beta in innerturnchain "+ beta + "c.getlocalbeta: "+ c.getLocalBeta());
        boolean recursion = InnerTurn(c.getLocalBeta(), c_current, end, P_temp, B_vis, Vis_temp);

        //push visible chain elements back on Stack B vis if no recursion is entered

//        if (recursion == false) {
//
//            while (B_vis.peek() != v_chainend) {
//                B_vis.push(P_temp.pop());
//                B_vis.peek().setLinkedtocorner(c_current);
//            }
//            B_vis.push(P_temp.pop());
//            B_vis.peek().setLinkedtocorner(c_current);
//
//            if (c_current != c) {
//                RecursiveVisibility(P_temp, B_vis, Vis_temp, c_current.getLocalBeta(), c_current, c_current, end);
//            }
//
//        }
    }

    private Point CheckRecursionArea(Stack<Point> Vis_temp, Stack<Point> B_vis, double beta, Point c, Point end) {
        double alpha = Math.abs(checkAngle(Vis_temp.peek(), c, c.getSuccessor()));
        double alpha_child;
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(Vis_temp.peek(), c ));
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(c, c.getSuccessor()));
        double gamma = Math.abs(checkAngle(Vis_temp.peek(), c, B_vis.peek()));

        Point c_temp = c;
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(Vis_temp.peek(), c ));
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(c, B_vis.peek()));

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("CHECK RECURSION AREA");
        System.out.println("--------------------------------------------------------------------------------");


        System.out.println("alpha: " + alpha + "gamma: " + gamma);


        while (alpha < gamma) {
            //get treechild that has more area, usually the first childs are the ones with outerturn
            try{
                System.out.println("alpha prev: "+alpha);
                alpha_child = checkAngle(c_temp.getTreeChild().get(0).getSuccessor(),c,Vis_temp.peek());
                System.out.println("alpha after: "+alpha);
                System.out.println("c_temp: "+ c_temp);
                System.out.println("c get child: "+ c.getTreeChild().get(0));
                System.out.println("c childlist: "+  c.getTreeChild());
                //check if left to the rec beginning line, and right to rec end line:
                if ( alpha < alpha_child ){
                    c_temp = c_temp.getTreeChild().get(0);
                    alpha = alpha_child;
                    System.out.println("alpha after if statement: "+alpha);
                }
            }catch (Exception e){
                System.out.println("Exception, no Treechild available");
                return c_temp;
            }
            System.out.println("alpha: " + alpha + "gamma: " + gamma);
        }
        return c_temp;
    }

    private boolean InnerTurn(double beta, Point c, Point end, Stack<Point> P, Stack<Point> B_vis, Stack<Point> Vis_temp) {

        Point v_cover = B_vis.peek();
        double beta_chain = beta - Math.abs(checkAngle( end, c,B_vis.peek()));
        double alpha = Math.abs(checkAngle(simulateRayEndPoint(c, v_cover), v_cover, get_second_peek(B_vis)));

        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(end, c));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(c, B_vis.peek()));

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("INNER TURN");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" INNER TURN end: "+ end);
        System.out.println(" INNER TURN c: "+ c);
        System.out.println(" INNER TURN B_vispeek: "+ B_vis.peek());

//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(v_cover, simulateRayEndPoint(c, v_cover)));
//        GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(v_cover, get_second_peek(B_vis)));


        System.out.println("INNER TURN ENTERED ");

        System.out.println("INNER TURN ENTERED alpha: " + alpha + " beta_chain: " + beta_chain);
        System.out.println("beta: " + beta + "checkangle: " + checkAngle( B_vis.peek(), c, end));

        //round beta_chain to 0 if negative:
        beta_chain = beta_chain < 0 ? 0 : beta_chain;
        System.out.println("INNER TURN ENTERED alpha: " + alpha + " beta_chain: " + beta_chain);
        System.out.println("INNER TURN ENTERED alpha: " + alpha + " beta_chain: " + beta_chain);
        System.out.println("INNER TURN ENTERED alpha: " + alpha + " beta_chain: " + beta_chain);
        System.out.println("INNER TURN ENTERED alpha: " + alpha + " beta_chain: " + beta_chain);

        rec_c_points.add(v_cover);
        if (alpha < beta_chain) {
            v_cover.setPredecessor(get_second_peek(B_vis));
            v_cover.setSuccessor(P.peek());
            v_cover.setTreeChild(c);
            c.setTreeParent(v_cover);
            beta = beta_chain - alpha;
            v_cover.setLocalBeta(beta_chain);
            System.out.println(P.peek());
            B_vis.push(P.pop());
            B_vis.peek().setLinkedtocorner(v_cover);
            RecursiveVisibilityInverse(P, B_vis, Vis_temp, beta, v_cover, v_cover, end);
            return true;
        } else {
            //delete first line for delete covered points:
            v_cover.setTreeParent(c);
//            c.setTreeChild(v_cover);
//            beta_chain = beta_chain-alpha;
//            beta_chain = beta_chain < 0 ? 0 : beta_chain ;
            System.out.println("beta_chain: "+ beta_chain);
            Point rotatedintersect = simulateRayEndPoint( rotatePredeccPointCounterClockwise(beta_chain,  v_cover, c), v_cover);
            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(v_cover, rotatedintersect));
            delete_covered_points(P, B_vis, v_cover,rotatedintersect);
//            Point v_cover_succ = B_vis.pop();
//            B_vis.push(v_cover_succ);
            System.out.println("DELETE COVERED POINTS DONE");
            return false;
        }
    }

    public void RecursiveVisibilityInverse(Stack<Point> P, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point c_outerturn, Point start, Point end) {

        //c_outerturn is q

        while (lineLineSegIntersection(start, end, B_vis.peek(), get_second_peek(B_vis)) == false) {
            if (getEventInverse(beta, P.peek(), c_outerturn, B_vis.peek(), get_second_peek(P), end, P, B_vis, Vis_temp) == false) {

                P.push(B_vis.pop());
                P.peek().setLinkedtocorner(c_outerturn);
            }
        }
        P.push(B_vis.pop());
        P.peek().setLinkedtocorner(c_outerturn);
    }


    private boolean getEvent(double beta, Point c, Point q, Point v2, Point prev_v1, Point end, Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp) {
        double angle = 0;
        double prev_angle = 0;


        // x y coords of points v1
        double x_v1 = q.getCenterX() - c.getCenterX();
        double y_v1 = q.getCenterY() - c.getCenterY();

        // x y coords of points v2
        double x_v2 = v2.getCenterX() - c.getCenterX();
        double y_v2 = v2.getCenterY() - c.getCenterY();

        // x y coords of prev_v1
        double x_prev = prev_v1.getCenterX() - c.getCenterX();
        double y_prev = prev_v1.getCenterY() - c.getCenterY();

        // angle between line of q and current vertex
        angle = Math.atan2(x_v2 * y_v1 - y_v2 * x_v1, x_v2 * x_v1 + y_v2 * y_v1) * 180 / Math.PI;
        // angle between line of q and prev vertex
        prev_angle = Math.atan2(x_prev * y_v1 - y_prev * x_v1, x_prev * x_v1 + y_prev * y_v1) * 180 / Math.PI;

        //cases:
        //turn inner: depending on sign. prev_angle has always a bigger angle than angle
        //turn outer right: angle bigger than prev_angle
        //turn outer left: angle bigger than prev_angle and previous vertex not visible

        boolean collinear = Math.round(angle) == 180.0 || Math.round(angle) == -180.0 || (Math.round(prev_angle) == 0 && angle < 0);
        boolean inner_turn_event = ((prev_angle > angle && angle > 0 && Math.round(prev_angle) >= 0)
                || (prev_angle < angle && angle < 0 && Math.round(prev_angle) <= 0)
                || (((Math.round(angle) == 180) || (Math.round(angle) == -180)) && prev_angle < 0)
                || (angle > 0 && Math.round(prev_angle) == 0 && inner_turn_before == true)
                || (prev_angle < 0 && angle > 0)) && visibleAngle(c, q, v2) == false && !collinear
                && !(angle > 0 && Math.round(prev_angle) == 0 && inner_turn_before == false);
//                && !(Math.ceil(angle) == 180 && prev_angle < 0);
        boolean outer_right_turn_event = ((prev_angle < angle && Math.round(prev_angle) >= 0
                && !visibleAngle(c, q, v2) && !collinear)
                || (angle > 0 && Math.round(prev_angle) == 0)) && inner_turn_before == false;
        boolean outer_left_turn_event = (((prev_angle > angle && angle < 0 && prev_angle < 0)
                && !visibleAngle(prev_v1, q, c) && visibleAngle(c, q, v2)
                && !collinear)
                || (Math.round(angle) == -180 || Math.round(angle) == 180)
                || (Math.round(prev_angle) == 0 && Math.round(angle) <= -180)
                || (Math.round(angle) == -90 && Math.round(prev_angle) == 0))
                && inner_turn_before == true;

        //catch edge case of two collinear lines
        if (angle == 180.0 && prev_angle == 0.0){
            return false;
        }

        System.out.println("angle: " + angle + " prev_angle: " + prev_angle);
        if (inner_turn_event) {
            System.out.println("inner turn entered");
            vi_prev = B_vis.peek();
            P_temp.peek().setCorner();
            InnerTurnChain(beta, q, end, P_temp, B_vis, Vis_temp);
            return true;
        }


        if (outer_right_turn_event) {
            System.out.println("outer right turn entered");
            c.setCorner();
            OuterRightTurn(beta, q, end, P_temp, B_vis, Vis_temp);
            return true;
        }

        return false;
    }


    private boolean getEventInverse(double beta, Point c, Point q, Point v2, Point prev_v1, Point end, Stack<Point> P, Stack<Point> B_vis, Stack<Point> Vis_temp) {
        double angle = 0;
        double prev_angle = 0;


        // x y coords of points v1
        double x_v1 = q.getCenterX() - c.getCenterX();
        double y_v1 = q.getCenterY() - c.getCenterY();

        // x y coords of points v2
        double x_v2 = v2.getCenterX() - c.getCenterX();
        double y_v2 = v2.getCenterY() - c.getCenterY();

        // x y coords of prev_v1
        double x_prev = prev_v1.getCenterX() - c.getCenterX();
        double y_prev = prev_v1.getCenterY() - c.getCenterY();

        // angle between line of q and current vertex
        angle = Math.atan2(x_v2 * y_v1 - y_v2 * x_v1, x_v2 * x_v1 + y_v2 * y_v1) * 180 / Math.PI;
        // angle between line of q and prev vertex
        prev_angle = Math.atan2(x_prev * y_v1 - y_prev * x_v1, x_prev * x_v1 + y_prev * y_v1) * 180 / Math.PI;

        //cases:
        //turn inner: depending on sign. prev_angle has always a bigger angle than angle
        //turn outer right: angle bigger than prev_angle
        //turn outer left: angle bigger than prev_angle and previous vertex not visible

        boolean collinear = Math.round(angle) == 180.0 || Math.round(angle) == -180.0 || (Math.round(prev_angle) == 0 && angle < 0);
        boolean inner_turn_event = ((prev_angle > angle && angle > 0 && Math.round(prev_angle) >= 0)
                || (prev_angle < angle && angle < 0 && Math.round(prev_angle) <= 0)
                || (((Math.round(angle) == 180) || (Math.round(angle) == -180)) && prev_angle < 0)
                || (angle > 0 && Math.round(prev_angle) == 0 && inner_turn_before == true)
                || (prev_angle < 0 && angle > 0)) && visibleAngle(c, q, v2) == false && !collinear
                && !(angle > 0 && Math.round(prev_angle) == 0 && inner_turn_before == false);
//                && !(Math.ceil(angle) == 180 && prev_angle < 0);
        boolean outer_right_turn_event = ((prev_angle < angle && Math.round(prev_angle) >= 0
                && !visibleAngle(c, q, v2) && !collinear)
                || (angle > 0 && Math.round(prev_angle) == 0)) && inner_turn_before == false;
        boolean outer_left_turn_event = (((prev_angle > angle && angle < 0 && prev_angle < 0)
                && !visibleAngle(prev_v1, q, c) && visibleAngle(c, q, v2)
                && !collinear)
                || (Math.round(angle) == -180 || Math.round(angle) == 180)
                || (Math.round(prev_angle) == 0 && Math.round(angle) <= -180)
                || (Math.round(angle) == -90 && Math.round(prev_angle) == 0))
                && inner_turn_before == true;

        System.out.println("angle: " + angle + " prev_angle: " + prev_angle);
        //exchange inner turn event with outer turn event
        if (inner_turn_event) {
            System.out.println("inner turn entered");
            vi_prev = B_vis.peek();
            P.peek().setCorner();
            InnerTurnChain(beta, q, end, B_vis, P, Vis_temp);
            return true;
        }

        //exchange outer turn event with inner turn event
        if (outer_right_turn_event) {
            System.out.println("outer right turn entered");
            c.setCorner();
            OuterRightTurn(beta, q, end, B_vis, P, Vis_temp);
            return true;
        }

        return false;
    }

    private boolean ChainVertex(Point c, Point q, Point v2, Point prev_v1, Stack<Point> P_temp, Stack<Point> B_vis) {
        double angle = 0;
        double prev_angle = 0;


//        while (ChainVertex(B_vis.peek(), c, P_temp.peek(), get_second_peek(B_vis), P_temp, B_vis) == true) {


            // x y coords of points v1
        double x_v1 = q.getCenterX() - c.getCenterX();
        double y_v1 = q.getCenterY() - c.getCenterY();

        // x y coords of points v2
        double x_v2 = v2.getCenterX() - c.getCenterX();
        double y_v2 = v2.getCenterY() - c.getCenterY();

        // x y coords of prev_v1
        double x_prev = prev_v1.getCenterX() - c.getCenterX();
        double y_prev = prev_v1.getCenterY() - c.getCenterY();

        // angle between line of q and current vertex
        angle = Math.atan2(x_v2 * y_v1 - y_v2 * x_v1, x_v2 * x_v1 + y_v2 * y_v1) * 180 / Math.PI;
        // angle between line of q and prev vertex
        prev_angle = Math.atan2(x_prev * y_v1 - y_prev * x_v1, x_prev * x_v1 + y_prev * y_v1) * 180 / Math.PI;

        //cases:
        //turn inner: depending on sign. prev_angle has always a bigger angle than angle
        //turn outer right: angle bigger than prev_angle
        //turn outer left: angle bigger than prev_angle and previous vertex not visible

        System.out.println("angle in chainvertex: "+ angle + "prev_angle in chainvertex: " +prev_angle);
        boolean collinear = Math.round(angle) == 180.0 || Math.round(angle) == -180.0 || (Math.round(prev_angle) == 0 && angle < 0);
        boolean inner_turn_event = ((prev_angle > angle && angle > 0 && Math.round(prev_angle) >= 0)
                || (prev_angle < angle && angle < 0 && Math.round(prev_angle) <= 0)
                || (((Math.round(angle) == 180) || (Math.round(angle) == -180)) && prev_angle < 0)
                || (prev_angle< angle && (prev_angle >=0)  && (angle >= 0) && (inner_turn_before ==true))
                || (angle > 0 && Math.round(prev_angle) == 0 && inner_turn_before == true)
                || (prev_angle < 0 && angle > 0)) && visibleAngle(c, q, v2) == false && !collinear
                && !(angle > 0 && Math.round(prev_angle) == 0 && inner_turn_before == false);
        boolean outer_left_turn_event = (((prev_angle > angle && angle < 0 && prev_angle < 0)
                && !visibleAngle(prev_v1, q, c) && visibleAngle(c, q, v2)
                && !collinear)
                || (Math.round(angle) == -180 || Math.round(angle) == 180)
                || (Math.round(prev_angle) == 0 && Math.round(angle) <= -180)
                || (Math.round(angle) == -90 && Math.round(prev_angle) == 0))
                && inner_turn_before == true;
        System.out.println("inner_turn_event: "+inner_turn_event+ " outer_left_turn_event: "+ outer_left_turn_event);

        if (inner_turn_event) {
            System.out.println("inner turn chain in chainvertex!!");
            inner_turn_before = true;
            return true;
        }

        if (outer_left_turn_event) {
            inner_turn_before=true;
                    B_vis.push(P_temp.pop());
                    vi_prev = B_vis.peek();
            return true;
        }


        return false;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // normal VisPolygon functions but overridden
    // ----------------------------------------------------------------------------------------------------------------


    @Override
    protected void forwardIntersect(Stack<Point> P, Stack<Point> Vis, Point q, Point c) {
        Point intersect_v;
        Point linked;
        Point c_prev = P.peek();
        Point c_prev_prev = get_second_peek(P);
//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(c)).setFill(Color.GRAY);
        update_angle_sum(Vis.peek(), q, P.peek());
        update_angle_sum(P.peek(), q, get_second_peek(P));


        while (lineLineSegIntersection(q, c, P.peek(), get_second_peek(P)) == false) {
            while (angle_sum_exceeded()) {
                if (P.size() == 1) {
                    break;
                }
                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.GREEN);


                R.push(P.pop());
                update_angle_sum(P.peek(), q, get_second_peek(P));
            }
            if (P.size() == 0) {
                break;
            }
        }

        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.WHITE);

        System.out.println(" °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°p.size:" + P.size());
        System.out.println(P.peek());
        System.out.println(get_second_peek(P));
        System.out.println(Vis.lastElement());
        System.out.println(Vis.firstElement());
        System.out.println(" °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        //line intersection of 4 points (2 node for first edge, 2 for second edge)
        intersect_v = lineLineIntersection(q, c, P.peek(), get_second_peek(P));
        System.out.println("intersection created");
        System.out.println(intersect_v);
        intersect_v.setPointLinked(c);
        GUI.betapolygonscene.getChildren().add(intersect_v);


        System.out.println("range: c " + inRange(q, c) + " linkp: " + inRange(q, intersect_v));
        if (inRange(q, c) >= inRange(q, intersect_v)) {
            linked = popLinkedChain(Vis, c_prev, c_prev_prev, null);

            if (linked != null) {
                linked = linked.getPointLinked();
                if (lineLineSegIntersection(q, linked, P.peek(), get_second_peek(P))) {
                    intersect_v = lineLineIntersection(q, linked, P.peek(), get_second_peek(P));
                    System.out.println("intersection at p.peek and get second peek");
                } else if (lineLineSegIntersection(q, linked, P.peek(), R.peek())) {
                    intersect_v = lineLineIntersection(q, linked, P.peek(), R.peek());
                    System.out.println("intersection at p.peek, R.peek");
                    Vis.push(P.pop());
                } else {
                    while (!lineLineSegIntersection(q, linked, R.peek(), get_second_peek(R))) {
                        R.pop();
                        System.out.println("popped");
                    }
                    System.out.println("intersection at R.pop");
                    intersect_v = lineLineIntersection(q, linked, R.peek(), get_second_peek(R));
                }
                vi_prev = P.pop();
                Vis.push(intersect_v);
            } else {
                //&& R.peek() != c_prev
                while (R.size() != 0) {
                    GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(R.peek())).setFill(Color.PINK);
                    P.push(R.pop());
                    System.out.println("r pushing on P bakc again");
                }
                System.out.println("r.size: " + R.size());
                System.out.println("Vis.ize: " + Vis.size());
                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.DARKVIOLET);
//                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(c)).setFill(Color.DARKVIOLET);
//                Vis.peek().setFill(Color.RED);
//                GUI.pointscene.getChildren().add(Vis.peek());
                Vis.push(c);
                delete_covered_points(P, Vis, q, P.peek());
                inner_turn_before = true;
            }
        } else {
            System.out.println("normal case reached");
//            System.out.println(" euclid distance c: " + inRange(p, c) + " intersect : " + inRange(p, intersect_v));
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.PINK);
            vi_prev = P.pop();
            Vis.push(intersect_v);
            P.push(intersect_v);
            intersect_v.setPointLinked(c);
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.PINK);
//            Vis.push(P.peek());
            inner_turn_before = false;
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.PINK);
        }
        reset_angle();
        R.clear();
    }


    @Override
    protected void delete_covered_points(Stack<Point> P, Stack<Point> B_vis, Point q, Point c) {
        //Point as new artificial edge node point
        Point linkedPoint = null;
        Point intersect_v = new Point();
        Point v_cover = B_vis.peek();
        B_vis.pop();
        System.out.println("q: in delete "+ q);
        System.out.println("c: in delete "+ c);
        System.out.println("v_cover: "+ v_cover);





        while ((lineLineSegIntersection(q, c, B_vis.peek(), get_second_peek(B_vis)) == false)) {
            System.out.println("in delete covered points");
            System.out.println();
            if (linkedPoint == null){
                linkedPoint = popLinked(B_vis);
            } else {
                if ( linkedPoint.getPointLinked() == null){
                    linkedPoint = popLinked(B_vis);
                } else if (inRange(q, c) > inRange(q, linkedPoint.getPointLinked() )){
                    break;
                }else {
                    linkedPoint = popLinked(B_vis);
                }
            }
            if (linkedPoint != null
                    && linkedPoint.getPointLinked() != null
                    && inRange(q, c) > inRange(q, linkedPoint.getPointLinked())) {

            }
        }



        try{
            System.out.println("linkedpoint in B_vis DELETE COVERED POINTS: "+ linkedPoint);
            System.out.println("CONDITION: get linkedpoint not null "+ linkedPoint.getPointLinked() != null);
            System.out.println("CONDITION: linkedpoint not null "+ linkedPoint!= null);
            System.out.println("CONDITION RANGE: "+ (inRange(q, c) > inRange(q, linkedPoint.getPointLinked())));
            System.out.println("CONDITION: range: c " + inRange(q, c) + " linkp: " + inRange(q, linkedPoint));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(q, c));
//            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(q, linkedPoint.getPointLinked()));

            System.out.println("q: "+ q);
            System.out.println("c: "+ c);
            System.out.println("getlinked: "+ linkedPoint.getPointLinked());
        } catch(Exception e){
            System.out.println(e);
        }


        if (linkedPoint != null
                && linkedPoint.getPointLinked() != null
                && inRange(q, c) > inRange(q, linkedPoint.getPointLinked())) {
            System.out.println("range: c " + inRange(q, c) + " linkp: " + inRange(q, linkedPoint));
            System.out.println("IN PREVIOUS POINT COVERED");
            //pop previous ntersect point and v_cover

            B_vis.pop();
            P.push(v_cover);


            System.out.println("P.peek: IN PREVIOIUS POINTS "+P.peek());
            previousPointCovered(P, B_vis, linkedPoint.getPointLinked(), c, linkedPoint);

        } else {


            //line intersection of 4 points (2 node for first edge, 2 for second edge)
            intersect_v = lineLineIntersection(q, c, B_vis.peek(), get_second_peek(B_vis));
            B_vis.pop();



            B_vis.push(intersect_v);
            B_vis.push(v_cover);


            System.out.println("intersection created");
            inner_turn_before = true;
        }
    }

    protected void previousPointCovered(Stack<Point> P, Stack<Point> Vis, Point q, Point c, Point linkpoint) {
        System.out.println("entered previous point covered");
        Point intersect_v;
        System.out.println("q: "+ q);
        q = linkpoint.getPointLinked();
        System.out.println("q: "+ q);
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(q, linkpoint));

        while (lineLineSegIntersection(q, linkpoint, P.peek(), get_second_peek(P)) == false) {
//            while (angle_sum_exceeded()) {
//                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.PINK);
//                System.out.println("--------- check angle:" + checkAngle(P.peek(), q, get_second_peek(P)));
//                R.push(P.pop());
//                update_angle_sum(P.peek(), q, get_second_peek(P));
//            }
            R.push(P.pop());
            System.out.println("--------- popping until intersection");
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.DARKORANGE);
        }


        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.DARKORANGE);

        //line intersection of 4 points (2 node for first edge, 2 for second edge)
        intersect_v = lineLineIntersection(q, linkpoint, P.peek(), get_second_peek(P));

        System.out.println("range q link: " + inRange(q, linkpoint) + " q intersect: " + inRange(q, intersect_v));
        System.out.println("linkpoint: " + linkpoint);
        System.out.println("intersect point:" + intersect_v);
//        if( inRange(q,linkpoint) >= inRange(q,intersect_v)){
//            while (R.size() != 0){
//                P.push(R.pop());
//            }
//            inner_turn_before = false;
//            System.out.println("CASTETAFUOBhdbuidgasuohpasdhioadgiohgijohagdiohasdgiohasgdhoigashogsdoha");
//        } else {

        System.out.println("intersection with linked point created");


        vi_prev = P.peek();
        intersect_v.setPointLinked(linkpoint);
        Vis.push(intersect_v);
        System.out.println("P.peek: IN PREVIOIUS POINTS " + P.peek());

        B_vis.push(P.pop());
        reset_angle();
        inner_turn_before = false;
//    }
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


        System.out.println("q: " + q);
        System.out.println("c: " + c);

        //y = mx +b;
        m_x = (c.getCenterX() - q.getCenterX()) * 2;
        m_y = (c.getCenterY() - q.getCenterY()) * 2;


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

    protected Stack<Point> reorderP_temp(Stack<Point> Stack) {
        Stack<Point> temp1 = new Stack<>();
        Stack<Point> temp2 = new Stack<>();

        int size = Stack.size();
        for (int i = 2; i < size; i++) {
            temp1.push(Stack.pop());
        }
        temp2.push(Stack.peek());
        for (int i = 0; i < size - 2; i++) {
            temp2.push(temp1.pop());
        }
        temp2.push(get_second_peek(Stack));
        temp2.push(Stack.pop());


        printStackorder(temp2);
        return temp2;
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

    private void clearRecQueryPointList(){
        int size = rec_c_points.size();
        for (int i= 0; i< size; i++){
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
