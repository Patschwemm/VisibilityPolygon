package geometry;

import com.sun.javafx.scene.traversal.Algorithm;
import javafx.scene.paint.Color;

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
            // preprocessing
            // ----------------------------------------


            System.out.println("P in beta:" + P.size());
            System.out.println("Vis in beta:" + Vis.size());
            // copy array so we have it for several BetaVisibility runs
            Stack<Point> P_temp = (Stack<Point>) this.P.clone();
            Stack<Point> Vis_temp = (Stack<Point>) this.Vis.clone();
            System.out.println("P_temp in beta:" + P_temp.size());
            System.out.println("Vis_temp in beta:" + Vis_temp.size());


            System.out.println("P:temp");
            printStackorder(P_temp);
            System.out.println("VIs:temp");
            Collections.rotate(Vis_temp, 1);
            Collections.rotate(P_temp, -1);


            System.out.println("P_temp rotate -1");
            printStackorder(P_temp);
            System.out.println("Vis_temp rotate -1");
            printStackorder(Vis_temp);


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
            polygonCycle(P_temp, Vis_temp, beta);
            System.out.println("polygoncycle entered");
        }
        connectEdges(B_vis);
        clearRecQueryPointList();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    private void polygonCycle(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta) {

        //synchronously on Vis_temp and P_temp Stacks finds the same points
        //if the points are not the same, a cave has been found with Vis.peek() at ending of the cave


        System.out.println("V.size in polygoncycle:" + Vis_temp.size());
        System.out.println("P_temo size in polygoncycle: " + P_temp.size());
        System.out.println("P PEEK IN POLYGONCYCLE: "+P_temp.peek());
        System.out.println("VIS PEEK IN POLYGONCYCLE: "+Vis_temp.peek());

        while (P_temp.size() != 0 && Vis_temp.size() != 0 && P_temp.peek() == Vis_temp.peek()) {
            B_vis.push(P_temp.pop());
            Vis_temp.pop();
            System.out.println("Point ADDED BETA VIS");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
            System.out.println("Bvis.size:" + B_vis.size());
        }

        System.out.println("B_vis.peek: "+ B_vis.peek());
        System.out.println("B_vis.corner: "+ B_vis.peek().isCorner());


        if (B_vis.peek().isCorner() == true) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Right Cave");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("V.size:" + Vis_temp.size());
            System.out.println("P.size:" + P_temp.size());
            System.out.println("Bvis.size:" + B_vis.size());
            rightCave(P_temp, B_vis, Vis_temp, beta, GUI.polygon.get_q());
        } else if (B_vis.peek().isCorner() == false) {
            leftCave(P_temp, B_vis, Vis_temp, beta, GUI.polygon.get_q());
        }


//        if ((Vis_temp.size() != 0 || P_temp.size() != 1)) {
//            System.out.println("VIS TEMP POPPED IN POLYGONCYCLE");
//            Vis_temp.pop();
//        }


//        System.out.println(Vis_temp.peek());
//        System.out.println(B_vis.peek());
        System.out.println("breakpoint");
        System.out.println("breakpoint");
        System.out.println("breakpoint");
        System.out.println("breakpoint");
        System.out.println("breakpoint");
    }


    public void rightCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {

        Point c_outerturn = B_vis.peek();

        System.out.println("V.size:" + Vis_temp.size());
        System.out.println("P.size:" + P_temp.size());
        System.out.println("Bvis.size:" + B_vis.size());
        System.out.println("right cave corner c_outerturn : " + c_outerturn);
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), c_outerturn));
        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(c_outerturn, Vis_temp.peek()));

        double alpha_init = Math.abs(checkAngle(P_temp.peek(), c_outerturn, Vis_temp.peek()));
        GUI.betapolygonscene.getChildren().add(createNode(Vis_temp.peek().getCenterX(), Vis_temp.peek().getCenterY()));
        System.out.println("Ptemp peek:"+ P_temp.peek());
        System.out.println("couter: "+ c_outerturn);
        System.out.println("getscondevis : " + get_second_peek(Vis_temp));

        //predecessor is q, so that we get the artificial edge
        c_outerturn.setPredecessor(q);
        c_outerturn.setSuccessor(P_temp.peek());


        System.out.println("alpha : " + alpha_init + "Beta: " + beta);


        if (alpha_init > beta) {

            Point intersect_v = caveIntersectRotate(P_temp, beta, q, c_outerturn, get_second_peek(Vis_temp));
            System.out.println("interscet in right cave: " + intersect_v);
            cycleToIntersectPoint(P_temp, intersect_v);
//            B_vis.push(intersect_v);
            System.out.println("B_vis: " + B_vis.peek());
            System.out.println("getsecond: " + get_second_peek(B_vis));
            recursive_VisPolygon(P_temp, B_vis, Vis_temp, beta, c_outerturn, intersect_v, Vis_temp.peek());

        } else if (alpha_init <= beta) {
            B_vis.push(P_temp.pop());
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(B_vis.peek())).setFill(Color.BLUE);
//            B_vis.peek().setLinkedtocorner(c_outerturn);
//            B_vis.peek().setTreeParent(c_outerturn);
//            c_outerturn.setTreeChild(B_vis.peek());
            System.out.println("remaining beta: " + beta);
            c_outerturn.setLocalBeta(beta);
            P_temp.pop();
//            RecursiveVisibilityInitial(P_temp, B_vis, Vis_temp, beta, c_outerturn, c_outerturn, Vis_temp.peek());
        }

    }


    public void leftCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {

    }


    public Point caveIntersectRotate(Stack<Point> P, double beta, Point q, Point c, Point end) {


        Stack<Point> P_temp = (Stack<Point>) P.clone();
        Point q_rotation;


        System.out.println("Start: " + B_vis.peek());
        P_temp.pop();
        System.out.println("B_vis püeek corner: " + B_vis.peek().isCorner());
        if (B_vis.peek().isCorner()) {
            q_rotation = rotatePredeccPointClockwise(beta, c, q);
        } else {
            q_rotation = rotatePredeccPointCounterClockwise(beta, c, q);
        }
        GUI.polygonscene.getChildren().add(q_rotation);
        Point intersect_v = null;
        Point temp_intersect;
        double min_range = 1000000000;


        System.out.println("AFTER ROTATION:");
        System.out.println("end: " + end);

        while (P_temp.peek() != end) {
            if (lineLineSegIntersection(q_rotation, c, P_temp.peek(), get_second_peek(P_temp)) == true) {
                System.out.println("intersection found");
                GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(P_temp.peek(), get_second_peek(P_temp)));
                temp_intersect = lineLineIntersection(q_rotation, c, P_temp.peek(), get_second_peek(P_temp));
                temp_intersect.setSuccessor(get_second_peek(P_temp));
                temp_intersect.setPredecessor(P_temp.peek());
                P_temp.pop();
                if (min_range > inRange(q_rotation, temp_intersect)) {
                    System.out.println("range shorter!!" + inRange(q_rotation, temp_intersect));
                    min_range = inRange(q_rotation, temp_intersect);
                    intersect_v = temp_intersect;
                } else {
                    System.out.println("not shorter!!");
                }
            } else {
                P_temp.pop();
            }
        }

        GUI.polygonscene.getChildren().add(createNode(intersect_v.getCenterX(), intersect_v.getCenterY()));
        return intersect_v;
    }

    protected void cycleToIntersectPoint(Stack<Point> P_temp, Point intersect_v) {

        System.out.println("intersect _ v " + intersect_v);
        Point[] v_intersect_norm = getPointsOnNormal(P_temp.peek(), get_second_peek(P_temp), intersect_v);
        System.out.println("intersect predecc:"+intersect_v.getPredecessor());
        System.out.println("intersect succ:"+intersect_v.getSuccessor());

        while (P_temp.peek()!=intersect_v.getPredecessor()){
            GUI.betapolygonscene.getChildren().add(createEdgeFromPointsBlue(P_temp.peek(), get_second_peek(P_temp)));
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

    protected void recursive_VisPolygon(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end) {

        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        System.out.println("B_vispeek: " + B_vis.peek());
        System.out.println("intersect v: " + intersect_v);
        System.out.println("end: " + end);


        //push points inbetweend intersect_v and end


//        Point[] end_norm = getPointsOnNormal(P_clone.peek(), get_second_peek(P_clone), end);
        Point[] end_norm = getPointsOnNormal(P_temp.peek(), get_second_peek(P_temp), end);

        GUI.betapolygonscene.getChildren().add(createEdgeFromPoints(end_norm[0], end_norm[1]));

        System.out.println("secondbpeek vistemp;" +get_second_peek(Vis_temp));

        P_temp.pop();



        while (P_temp.peek() != get_second_peek(Vis_temp)) {
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P_temp.peek())).setFill(Color.BLUE);
            System.out.println("P_clone: " + P_temp.peek());
            System.out.println("P_clone second: " + get_second_peek(P_temp));
            temp.push(P_temp.pop());
        }


        System.out.println("temp.size " + temp.size());
        System.out.println("P_temp peek in REC VISIBILITY: "+ P_temp.peek());
        System.out.println("VIs_temp peek in REC VISIBILITY: "+ Vis_temp.peek());

        if (temp.size() == 0) {

            B_vis.push(intersect_v);
            B_vis.push(Vis_temp.pop());
        } else {
            temp.push(Vis_temp.pop());
            temp.push(B_vis.peek());
            temp.push(intersect_v);

            Collections.rotate(temp,1);
            Collections.reverse(temp);

            algorithm_default(temp, B_vis, B_vis.peek());

            System.out.println("P_temp peek in REC VISIBILITY: "+ P_temp.peek());
            System.out.println("VIs_temp peek in REC VISIBILITY: "+ Vis_temp.peek());


            printStackordertoPolygon(temp);
            System.out.println("hitler");
        }
    }


    // ----------------------------------------------------------------------------------------------------------------
    // VisPolygon Overrides
    // ----------------------------------------------------------------------------------------------------------------


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

        m_x = m_x/ norm; //  / norm
        m_y = m_y/ norm ;//  / norm

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
            System.out.println("GUI.polygon.addNode("+temp.peek().getCenterX()+","+temp.pop().getCenterY()+");");
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
