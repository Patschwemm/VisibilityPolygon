package geometry;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import geometry.Point;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.Stack;

public class VisPolygon extends Polygon {

    // List for iteration through points for algorithm
    private ArrayList<Point> PointList = (ArrayList<Point>) GUI.polygon.getPointList().clone();
    private Stack<Point> P = new Stack<Point>();
    private Stack<Point> Vis = new Stack<Point>();
    protected Stack<Point> R = new Stack<Point>();
    //List Copies of P and Vis for BetaVis
    private Stack<Point> betavis_P = new Stack<Point>();
    private Stack<Point> betavis_Vis = new Stack<Point>();
    protected double angle_sum = 0;
    private int p_idx;
    private Point vi_prev;
    public boolean inner_turn_before = false;
    public static boolean betavis = false;



    protected VisPolygon() {
        super();

        //check if Polygon is given and p point for visibility is given
        if (GUI.polygon.getPolygonDrawn() && GUI.polygon.is_q_set() && betavis == false) {

            // preprocessing
            // ----------------------------------------
            PointList = pre_Processing_Points(PointList);
            pre_Processing_P(PointList, GUI.polygon.get_q());
            List_to_Stack(PointList, P, p_idx);
            printStackorder(P);
            printStackorder(Vis);


            // calculate visibility
            // ----------------------------------------
            Stack<Point> vispolygon = algorithm_default(P, Vis, GUI.polygon.get_q());
            //connects all nodes of Polygon and makes visibilityPolygon visible on Interface
            connectEdges(vispolygon);
            showPushedNodes();

        } else if(betavis == true){
            System.out.println("not called because betavis is called");
        } else{
            System.out.println("Input of Polygon and visibility Point p needed. \n");
            Settings.get().get_vis_q_Status().setSelected(false);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    protected Stack<Point> algorithm_default(Stack<Point> P, Stack<Point> Vis, Point q) {

        // calculate visible starting point (done in Pre-Processing)

        System.out.println("CALLED");
        System.out.println("P size: "+ P.size());

        vi_prev = get_second_peek(Vis);
        //cycles through all nodes of polygon
        while (P.size() > 1) {
            boundaryCycle(P, Vis, q);
            System.out.println("P size: " + P.size());
            System.out.println("Vis size: " + Vis.size());
        }


        return Vis;
        //addToScene(GUI.polygonscene, VisEdgeList);
    }

    private void boundaryCycle(Stack<Point> P, Stack<Point> Vis, Point q) {
        //finds the turn event for current cycle

//

        while (P.size()!= 0 && !getEvent(Vis.peek(), q, P.peek(), vi_prev, P, Vis)) {
            System.out.println("boundary cycling");
            System.out.println("P size: " + P.size());
            System.out.println("Vis size: " + Vis.size());
            System.out.println("visible angle: " + visibleAngle(Vis.peek(), q, P.peek()));

            vi_prev = Vis.peek();
            Vis.push(P.pop());
            inner_turn_before = false;



            //catch stackempt exception, Algorithm done by this point
            if (P.size() <= 1) {
                Vis.pop();
                break;
            }
//            System.out.println("Event: " + getEvent(Vis.peek(), p, P.peek(), get_second_peek(Vis), P, Vis));
        }
    }


    protected boolean getEvent(Point c, Point q, Point v2, Point prev_v1, Stack<Point> P, Stack<Point> Vis) {
        double angle = 0;
        double prev_angle = 0;

        System.out.println("Get even of Points: ");
        System.out.println("Point c: "+ c.getCenterX()+ " "+ c.getCenterY());
        System.out.println("Point v2: "+ v2.getCenterX()+ " "+ v2.getCenterY());
        System.out.println("Point prev v: "+ prev_v1.getCenterX()+ " "+ prev_v1.getCenterY());
        System.out.println("Point q: "+ q.getCenterX()+ " "+ q.getCenterY());
        if(c==q){
            return false;
        }


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
                || (angle> 0 && Math.round(prev_angle) == 0 && inner_turn_before == true)
                || (prev_angle < 0 && angle > 0)) && visibleAngle(c, q, v2) == false && !collinear
                && !(angle > 0 && Math.round(prev_angle) ==0 && inner_turn_before == false);
//                && !(Math.ceil(angle) == 180 && prev_angle < 0);
        boolean outer_right_turn_event = ((prev_angle < angle && Math.round(prev_angle) >= 0
                && !visibleAngle(c, q, v2) && !collinear)
//                && !(angle ==  180.0 && prev_angle == 0.0)
                || (angle > 0 && Math.round(prev_angle) == 0)) && inner_turn_before == false;
        boolean outer_left_turn_event = (((prev_angle > angle && angle < 0 && prev_angle < 0)
                && !visibleAngle(prev_v1, q, c) && visibleAngle(c, q, v2)
                && !collinear)
                || (Math.round(angle) == -180 || Math.round(angle) == 180)
                || (Math.round(prev_angle) == 0 && Math.round(angle) <= -180)
                || (Math.round(angle) == -90 && Math.round(prev_angle) == 0))
                && inner_turn_before == true;

        if(Math.round(angle) == 0 && Math.round(prev_angle) == 0){
            return false;
        }

        System.out.println("angle: " + angle + " prev_angle: " + prev_angle);
        System.out.println("inner turn: "+ inner_turn_event);
        System.out.println("visibleAngle(c, q, v2) "+ visibleAngle(c, q, v2));
        if (inner_turn_event) {
            System.out.println("inner turn entered");
            vi_prev = Vis.peek();
            delete_covered_points(P, Vis, q, P.peek());
            return true;
        }

        if (outer_left_turn_event) {
            System.out.println("outer left turn entered");
            fastForward(P, Vis, q, c);
            return true;
        }

        if (outer_right_turn_event) {
            System.out.println("outer right turn entered");
            forwardIntersect(P, Vis, q, c);
            return true;
        }

        return false;
    }

    protected void forwardIntersect(Stack<Point> P, Stack<Point> Vis, Point q, Point c) {
        Point intersect_v;
        Point linked;
        Point c_prev = P.peek();
        Point c_prev_prev = get_second_peek(P);
        System.out.println("check angle1:" + checkAngle(Vis.peek(), q, P.peek()));
        update_angle_sum(Vis.peek(), q, P.peek());
        System.out.println("check angle2:" + checkAngle(P.peek(), q, get_second_peek(P)));
        update_angle_sum(P.peek(), q, get_second_peek(P));


        while (lineLineSegIntersection(q, c, P.peek(), get_second_peek(P)) == false) {
            while (angle_sum_exceeded()) {
                if (P.size() == 1) {
                    break;
                }
                System.out.println("in Fast Forward Intersect while-loop");
                System.out.println("check angle:" + checkAngle(P.peek(), q, get_second_peek(P)));
//                linkedPoint = popLinked(P);
                R.push(P.pop());
                update_angle_sum(P.peek(), q, get_second_peek(P));
            }
            if (P.size() == 0) {
                break;
            }
        }


        System.out.println(" °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°p.size:"+ P.size());
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
        c.setIntersect(intersect_v);
        intersect_v.setCorner(c);

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
                while (R.size() != 0 ) {
                    P.push(R.pop());
                    System.out.println("r pushing on P bakc again");
                }
                System.out.println("r.size: "+ R.size());
                System.out.println("Vis.ize: "+ Vis.size());
                Vis.push(c);
                delete_covered_points(P,Vis,q,P.peek());
                inner_turn_before = true;
            }
        } else {
            System.out.println("normal case reached");
            intersect_v.setPredecessor(P.peek());
            intersect_v.setSuccessor(get_second_peek(P));
            c.setInner_turn_corner(false);
            System.out.println("POPPED PEEK:"+ P.peek());
            vi_prev = P.pop();
            Vis.push(intersect_v);
            System.out.println("POPPED 2nd PEEK:"+ P.peek());
            Vis.push(P.pop());
            System.out.println("");
            inner_turn_before = false;
        }
        reset_angle();
        R.clear();
    }

    private void fastForward(Stack<Point> P, Stack<Point> Vis, Point q, Point c) {
        System.out.println("check angle:" + checkAngle(Vis.peek(), q, P.peek()));


        while ((visibleAngle(P.peek(), q, Vis.peek()) == false)) {
            System.out.println("In FAST FORWARD      checkangle: " + checkAngle(P.peek(), q, get_second_peek(Vis)));
            vi_prev = P.pop();
        }
        inner_turn_before = true;
        delete_covered_points(P, Vis, q, P.peek());

    }

 
    protected void delete_covered_points(Stack<Point> P, Stack<Point> Vis, Point q, Point c) {
        //Point as new artificial edge node point
        Point linkedPoint = null;
        Point intersect_v = new Point();


        //deletes all vertices left to the vector pc
//        while ((lineLineSegIntersection(q, c, Vis.peek(), get_second_peek(Vis)) == false)) {

        while ((lineLineSegIntersection(q, c, Vis.peek(), get_second_peek(Vis)) == false)) {
            System.out.println("in delete covered points");
            if (linkedPoint == null){
                linkedPoint = popLinked(Vis);
            } else {
                System.out.println("in range argument: "+ (inRange(q, c) > inRange(q, linkedPoint.getPointLinked()) ));
                if ( linkedPoint.getPointLinked() == null){
                    linkedPoint = popLinked(Vis);
                } else if (inRange(q, c) > inRange(q, linkedPoint.getPointLinked() )){
                    System.out.println("IN BREAK");
                    break;
                }else {
                    linkedPoint = popLinked(Vis);
                }
            }
            if (linkedPoint != null
                    && linkedPoint.getPointLinked() != null
                    && (inRange(q, c) > inRange(q, linkedPoint.getPointLinked())
            || visibleAngle(c,q,linkedPoint.getPointLinked()))){

            }
        }

        try {
            System.out.println("in range argument: "+ inRange(q, c) +" "+ inRange(q, linkedPoint.getPointLinked() ));
            System.out.println("Linkpoint "+ linkedPoint);
            System.out.println("get linked point" + linkedPoint.getPointLinked());
            System.out.println("checkangle: "+ visibleAngle(c,q,linkedPoint.getPointLinked()));
        }catch (Exception e){
            System.out.println("error in deletecovered at linkpoint argument");
        }



        if (linkedPoint != null
                && linkedPoint.getPointLinked() != null
                && (inRange(q, c) > inRange(q, linkedPoint.getPointLinked())
                || visibleAngle(c,q,linkedPoint.getPointLinked()))){
            System.out.println("checkangle: "+ visibleAngle(c,q,linkedPoint.getPointLinked()));
            System.out.println("range: c " + inRange(q, c) + " linkp: " + inRange(q, linkedPoint));
            previousPointCovered(P, Vis, q, c, linkedPoint.getPointLinked());

        } else {

            //line intersection of 4 points (2 node for first edge, 2 for second edge)
            intersect_v = lineLineIntersection(q, c, Vis.peek(), get_second_peek(Vis));
            intersect_v.setPredecessor(get_second_peek(Vis));
            intersect_v.setSuccessor(Vis.peek());
            Vis.pop();



            Vis.push(intersect_v);
            intersect_v.setCorner(c);
            c.setIntersect(intersect_v);
            c.setInner_turn_corner(true);
            Vis.push(P.pop());

            System.out.println("intersection created");
            inner_turn_before = true;
        }
    }

    protected void previousPointCovered(Stack<Point> P, Stack<Point> Vis, Point q, Point c, Point linkpoint) {
        System.out.println("entered previous point covered");
        Point intersect_v;

        while (lineLineSegIntersection(q, linkpoint, P.peek(), get_second_peek(P)) == false) {
            while (angle_sum_exceeded()) {
                System.out.println("--------- check angle:" + checkAngle(P.peek(), q, get_second_peek(P)));
                R.push(P.pop());
                update_angle_sum(P.peek(), q, get_second_peek(P));
            }
            R.push(P.pop());
            System.out.println("--------- popping until intersection");
        }



        //line intersection of 4 points (2 node for first edge, 2 for second edge)
        intersect_v = lineLineIntersection(q, linkpoint, P.peek(), get_second_peek(P));

        System.out.println("range q link: "+ inRange(q,linkpoint) + " q intersect: "+ inRange(q,intersect_v));
        System.out.println("linkpoint: "+linkpoint);
        System.out.println("intersect point:" + intersect_v);
        if( inRange(q,linkpoint) >= inRange(q,intersect_v)){
            while (R.size() != 0){
                P.push(R.pop());
            }
            inner_turn_before = false;
            System.out.println("CASTETAFUOBhdbuidgasuohpasdhioadgiohgijohagdiohasdgiohasgdhoigashogsdoha");
        } else {

            System.out.println("intersection with linked point created");

            intersect_v.setPredecessor(P.peek());
            intersect_v.setSuccessor(get_second_peek(P));

            vi_prev = P.pop();
            intersect_v.setPointLinked(linkpoint);
            linkpoint.setIntersect(intersect_v);
            intersect_v.setCorner(linkpoint);
            linkpoint.setInner_turn_corner(false);
            System.out.println("INTERSECTION PREV POINT: ");
            System.out.println("linkpoint: "+ linkpoint);
            System.out.println("intersect v: "+ intersect_v);
            Vis.push(intersect_v);
            reset_angle();
            inner_turn_before = false;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Pre-Processing
    // ----------------------------------------------------------------------------------------------------------------

    
    // Sort Array Clockwise or Counterclockwise
    private ArrayList<Point> pre_Processing_Points(ArrayList<Point> PointList) {

        //test Orientation of the given Polygon
        if (testOrientation(PointList) == "clockwise") {
            System.out.println("Clockwise input of Poygon, rearranging Pointlist");
        } else {
            PointList = rearrangeList(PointList);
            System.out.println("Counterclockwise Input of Polygon");
        }

        return PointList;
    }

    //find idx for Point that is a valid node of visibility Polygon
    private void pre_Processing_P(ArrayList<Point> PointList, Point q) {

        if (q == null) {
        }

        //find edge with smallest distance
        double smallest_dist = 10000;
        int idx = 0;
        for (int i = 0; i < PointList.size(); i++) {
            System.out.println("distance: " + closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), q));
            //smallest distance to visible edge is strating point that can be added to visiblity polygon
            if (smallest_dist >= closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), q)
                    && visibleAngle(PointList.get(i), q, PointList.get(decrementIdx(i)))) {
                smallest_dist = closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), q);
                idx = i;
                System.out.println("smallest distance: " + closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), q));

            }
        }

        Vis.push(PointList.get(idx));
        Vis.push(PointList.get(decrementIdx(idx)));
        p_idx = decrementIdx(idx);
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Geometry Helpers
    // ----------------------------------------------------------------------------------------------------------------

    //returns true for left orientation of points, false for right orientation
    protected boolean visibleAngle(Point v1, Point q, Point v2) {

        return checkAngle(v1, q, v2) >= 0;
    }


    //finds intersection of edges given bei 2 points each
    protected Point lineLineIntersection(Point A, Point B, Point C, Point D) {

        // Line AB represented as a1x + b1y = c1
        double a1 = B.getCenterY() - A.getCenterY();
        double b1 = A.getCenterX() - B.getCenterX();
        double c1 = a1 * (A.getCenterX()) + b1 * (A.getCenterY());

        // Line CD represented as a2x + b2y = c2
        double a2 = D.getCenterY() - C.getCenterY();
        double b2 = C.getCenterX() - D.getCenterX();
        double c2 = a2 * (C.getCenterX()) + b2 * (C.getCenterY());

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {

            return null;
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return createNode(x, y);
        }
    }

    //finds intersection of edges given bei 2 points each
    protected boolean lineLineSegIntersection(Point A, Point B, Point C, Point D) {

        //construct normal vector to line AB
        double n_x = A.getCenterX() - B.getCenterX();
        double n_y = A.getCenterY() - B.getCenterY();

        //vector CB
        double ca_x = A.getCenterX() - C.getCenterX();
        double ca_y = A.getCenterY() - C.getCenterY();

        //vector CB
        double da_x = A.getCenterX() - D.getCenterX();
        double da_y = A.getCenterY() - D.getCenterY();

        //cross product of both Points C and D, sign check afterwards
        double cross_c = n_x * ca_y - n_y * ca_x;
        double cross_d = n_x * da_y - n_y * da_x;

        System.out.println("cross_c: " + cross_c);
        System.out.println("cross_d: " + cross_d);

        System.out.println("in Intersection point: "+ C);




        //different signs mean there is an intersection
        boolean almost_zero = (cross_c > -0.00001 && cross_c <= 0) || (cross_d > -0.00001 && cross_d <= 0 )
                || (cross_c < 1.318767317570746E-7 && cross_c >= 0) || (cross_d < 1.318767317570746E-7  && cross_d >= 0);
        System.out.println("almost zero: "+ almost_zero);
        //cross_c == 0 || cross_d == 0
        if ((Math.signum(cross_d) != Math.signum(cross_c) || cross_c == 0 || cross_d == 0  || almost_zero)
                && !(cross_c == 0 && cross_d == 0)) {
            System.out.println("intersection of segment");
            return true;
        } else {
            System.out.println(" no  intersection of segment");
            return false;
        }
    }

    //finds intersection of edges given bei 2 points each
    private boolean AdjacentEdgesTest(Point A, Point B, Point C, Point D) {

        //construct normal vector to line AB
        double n_x = A.getCenterX() - B.getCenterX();
        double n_y = A.getCenterY() - B.getCenterY();

        //vector CB
        double ca_x = A.getCenterX() - C.getCenterX();
        double ca_y = A.getCenterY() - C.getCenterY();

        //vector CB
        double da_x = A.getCenterX() - D.getCenterX();
        double da_y = A.getCenterY() - D.getCenterY();

        //cross product of both Points C and D, sign check afterwards
        double cross_c = n_x * ca_y - n_y * ca_x;
        double cross_d = n_x * da_y - n_y * da_x;

        System.out.println("cross_c: " + cross_c);
        System.out.println("cross_d: " + cross_d);


        //different signs mean there is an intersection


        if (cross_c == 0 || cross_d == 0) {
            System.out.println("adjacent");
            return true;
        } else {
            System.out.println(" not adjacent ");
            return false;
        }
    }


    //test orientation of the input polygon
    private String testOrientation(ArrayList<Point> PointList) {

        //fast practical check: get smallest X-Coordinate ( if two are the same smallest y Coord)
        //check Orientation from three points with fast determinant calculation

        //get smallest X-Coord
        int idx = 0;

        for (int i = 1; i < PointList.size(); i++) {
            if (PointList.get(idx).getCenterX() > PointList.get(i).getCenterX()) {
                idx = i;
            }
            if (PointList.get(idx).getCenterX() == PointList.get(i).getCenterX() &&
                    PointList.get(idx).getCenterY() > PointList.get(i).getCenterY()) {
                idx = i;
            }
        }
        //idx is given and two adjacent points
        int idx_prev;
        int idx_next;

        if (idx == 0) {
            idx_prev = PointList.size() - 1;
            idx_next = idx + 1;
        } else if (idx == PointList.size() - 1) {
            idx_prev = idx - 1;
            idx_next = 0;
        } else {
            idx_prev = idx - 1;
            idx_next = idx + 1;
        }

        // A = prev || B = idx || C = next
        //det(o)=( x_idx - x_prev ) * (y_next - y_prev ) - ( x_next - x_prev ) * ( y_idx - y_prev)
        double det_orient;
        det_orient = (PointList.get(idx).getCenterX() - PointList.get(idx_prev).getCenterX())
                * (PointList.get(idx_next).getCenterY() - PointList.get(idx_prev).getCenterY())
                - (PointList.get(idx_next).getCenterX() - PointList.get(idx_prev).getCenterX())
                * (PointList.get(idx).getCenterY() - PointList.get(idx_prev).getCenterY());
        //if det(o) is positive then clockwise,negative counterclockwise, 0 if points are collinear
        if (det_orient > 0) {
            return "clockwise";
        } else {
            return "counterclockwise";
        }
    }

    //finds the closest visible edge of point p that is calculated visibility of a polygon from
    private double closest_distance_to_linesegment(Point c1, Point c2, Point q) {

        //dot product
        double dx = c2.getCenterX() - c1.getCenterX();
        double dy = c2.getCenterY() - c1.getCenterY();

        double qx = q.getCenterX();
        double qy = q.getCenterY();

        double c1x = c1.getCenterX();
        double c1y = c1.getCenterY();

        double c2x = c2.getCenterX();
        double c2y = c2.getCenterY();

        double dot = ((qx - c1x) * dx + (qy - c1y) * dy);
        double len_sq = (dx * dx + dy * dy);

        double param = dot / len_sq;

        //new point on linesegment which is closest to p
        double x;
        double y;

        if (param < 0) {
            //closest point is c1
            x = c1x;
            y = c1y;

        } else if (param > 1) {
            //closest point is c2
            x = c2x;
            y = c2y;

        } else {
            //calculate point on line as closest point
            x = c1x + param * dx;
            y = c1y + param * dy;
        }

        dx = qx - x;
        dy = qy - y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    //adds angle to catch the case for angle > 360
    protected void update_angle_sum(Point v1, Point q, Point v2) {
        angle_sum += checkAngle(v1, q, v2);
        if (angle_sum >= 0) {
            angle_sum = 0;
        }
//        System.out.println("angle sum: " + angle_sum);
    }

    // orientation test addition to angle sum
    protected boolean angle_sum_exceeded() {
        if (angle_sum < 0.0) {
            return true;
        } else {
            return false;
        }
    }

    protected void reset_angle() {
        angle_sum = 0;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // List, Point, idx handling
    // ----------------------------------------------------------------------------------------------------------------

    public Stack<Point> getBetavis_P() {
        return betavis_P;
    }

    public Stack<Point> getBetavis_Vis() {
        return betavis_Vis;
    }


    protected Point popLinkedChain(Stack<Point> Stack, Point c, Point c_prev, Point link) {

        Point linked = link;

        if (Stack.size() <= 1) {
            System.out.println("stack empty entered");
            return linked;
        }
        Point current = Stack.pop();
        Point prev = Stack.peek();

        if (current.getPointLinked() != null && AdjacentEdgesTest(c, c_prev, current, prev)) {
//            Stack.push(current);
            System.out.println("+++++ linked point found");
            return current;
        } else if (current.getPointLinked() == null && !AdjacentEdgesTest(c, c_prev, current, prev)) {
            System.out.println("+++++ linked point ");
            return null;
        } else if (current.getPointLinked() == null && AdjacentEdgesTest(c, c_prev, current, prev)) {
            System.out.println("+++++ going recursive ");
            linked = popLinkedChain(Stack, c, c_prev, linked);
        }

        System.out.println("+++++ pushing points");
        Stack.push(current);
        return linked;
    }

    // checks if vis(p) point is linked to a turn
    protected Point popLinked(Stack<Point> Stack) {
        Point point;
        point = Stack.pop();

        if (point.getPointLinked() != null) {
            System.out.println("----------- Point is linked");
            return point;
        } else {
            point.setCorner(null);
            point.setIntersect(null);
            System.out.println("----------- Point is not linked");
            return null;
        }
    }

    //increments so that if idx = last polygon node +1 -> idx = 0, first polygo node
    @Override
    public int incrementIdx(int idx) {
        idx++;
        idx = idx % PointList.size();
        return idx;
    }

    //increments so that if idx = 0, first polygon node -> idx = last polygon node
    @Override
    public int decrementIdx(int idx) {
        idx--;
        idx = idx + PointList.size();
        idx = idx % PointList.size();
        return idx;
    }

    protected Point get_second_peek(Stack<Point> Stack) {
        Point prev;
        Point current;


        current = Stack.pop();
        if (P.size() == 0) {
            System.out.println("777777777777777777777777777   last elemnt S    77777777777777777777777777777777777");
            System.out.println(Vis.lastElement());
            prev = Vis.lastElement();

        } else {
            prev = Stack.peek();
        }
        Stack.push(current);
        return prev;
    }


    //clears VisPolygon
    public void deleteVisPolygon() {
        GUI.polygonscene.getChildren().clear();
    }

    // if polygon input is clockwise order to counterclockwise
    public ArrayList<Point> rearrangeList(ArrayList<Point> List) {
        ArrayList<Point> PointListInvers = new ArrayList<>(List.size());

        for (int i = List.size() - 1; i >= 0; i--) {
            PointListInvers.add(List.get(i));
        }
        return PointListInvers;
    }

    private void List_to_Stack(ArrayList<Point> List, Stack<Point> P, int p_idx) {

        for (int i = 0; i < PointList.size(); i++) {
            P.push(List.get(p_idx));
            betavis_P.push(List.get(p_idx));
            if(betavis_P.size() > 1){
                get_second_peek(betavis_P).setPredecessor(betavis_P.peek());
            }
            System.out.println(i+ ": " + betavis_P.peek());
            p_idx = incrementIdx(p_idx);

        }
        betavis_P.lastElement().setPredecessor(betavis_P.firstElement());
        System.out.println(betavis_P);
        System.out.println(P);

    }

    public void showPushedNodes(){
        for (int i = 0; i < Vis.size();i++){
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(Vis.get(i))).setFill(Color.RED);
        }
    }


    //connects nodes of Vispolygon to form the visibility Polygon edges and adds that polygon to scene
    // extra to have distinct edges and a filled polygon
    protected void connectEdges(Stack<Point> Vis) {

        javafx.scene.shape.Polygon vis_polygon = new javafx.scene.shape.Polygon();


        int stack_size = Vis.size();
        for (int i = 0; i < stack_size; i++) {

            betavis_Vis.push(Vis.peek());
            vis_polygon.getPoints().addAll(Vis.peek().getCenterX(), Vis.pop().getCenterY());
        }

        vis_polygon.setStroke(Color.DARKRED);
        vis_polygon.setStrokeWidth(4);

        vis_polygon.setFill(Color.RED);
        vis_polygon.setOpacity(0.4);

        GUI.polygonscene.getChildren().add(vis_polygon);

    }

    // adds all edges to scene to form a "pseudo" polygon, so that the edges can be seen clearly
    private void addToScene(Group scene, ArrayList<Line> e) {
        for (int i = 0; i < e.size(); i++) {
            scene.getChildren().add(e.get(i));
        }
    }

    public void printStackorder(Stack<Point> Stack) {

        Stack<Point> temp = (java.util.Stack<Point>) Stack.clone();

        int size = Stack.size();
        for (int i = 0; i < size; i++) {
            System.out.println("number: " + i + temp.pop());
        }
    }

}

