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


            // calculate visibility
            // ----------------------------------------
            Stack<Point> vispolygon = algorithm_default(P, Vis, GUI.polygon.get_q());
            //connects all nodes of Polygon and makes visibilityPolygon visible on Interface
            connectEdges(vispolygon);
            showPushedNodes();

        } else if(betavis == true){
            System.out.println("Not called because BetaVisibility is called");
        } else{
            System.out.println("Input of polygon and query point point q needed. \n");
            Settings.get().get_vis_q_Status().setSelected(false);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    protected Stack<Point> algorithm_default(Stack<Point> P, Stack<Point> Vis, Point q) {


        vi_prev = get_second_peek(Vis);
        //cycles through all nodes of polygon
        while (P.size() > 1) {
            boundaryCycle(P, Vis, q);
        }


        return Vis;
    }

    private void boundaryCycle(Stack<Point> P, Stack<Point> Vis, Point q) {

        while (P.size()!= 0 && !getEvent(Vis.peek(), q, P.peek(), vi_prev, P, Vis)) {
            vi_prev = Vis.peek();
            Vis.push(P.pop());
            inner_turn_before = false;

            //catch stackempty exception, Algorithm done by this point
            if (P.size() <= 1) {
                Vis.pop();
                break;
            }
        }
    }


    protected boolean getEvent(Point c, Point q, Point v2, Point prev_v1, Stack<Point> P, Stack<Point> Vis) {
        double angle = 0;
        double prev_angle = 0;

        //for recursion, the constructed polygon P_rec includes q as point
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

        // in case the same point is analyzed
        if(Math.round(angle) == 0 && Math.round(prev_angle) == 0){
            return false;
        }

        if (inner_turn_event) {
            vi_prev = Vis.peek();
            delete_covered_points(P, Vis, q, P.peek());
            return true;
        }

        if (outer_left_turn_event) {
            fastForward(P, Vis, q, c);
            return true;
        }

        if (outer_right_turn_event) {
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
        update_angle_sum(Vis.peek(), q, P.peek());
        update_angle_sum(P.peek(), q, get_second_peek(P));

        //iterates through P until intersection and angle sum is not smaller than zero
        while (lineLineSegIntersection(q, c, P.peek(), get_second_peek(P)) == false) {
            while (angle_sum_exceeded()) {
                if (P.size() == 1) {
                    break;
                }
                R.push(P.pop());
                update_angle_sum(P.peek(), q, get_second_peek(P));
            }
            if (P.size() == 0) {
                break;
            }
        }


        //line intersection of 4 points (2 node for first edge, 2 for second edge)
        intersect_v = lineLineIntersection(q, c, P.peek(), get_second_peek(P));
        //set linkages
        intersect_v.setPointLinked(c);
        c.setIntersect(intersect_v);
        intersect_v.setCorner(c);


        // in case Forward Intersect is called in polygon winding, R Stack is the leftover Stack of P
        // in the case of being called in polygon winding
        if (inRange(q, c) >= inRange(q, intersect_v)) {
            //tries to find the prior intersection before the winding of the polygon
            linked = popLinkedChain(Vis, c_prev, c_prev_prev, null);
            if (linked != null) {
                //gets the linkpoint if one has been found
                linked = linked.getPointLinked();
                //find the intersection ok the linkpoint
                if (lineLineSegIntersection(q, linked, P.peek(), get_second_peek(P))) {
                    intersect_v = lineLineIntersection(q, linked, P.peek(), get_second_peek(P));
                } else if (lineLineSegIntersection(q, linked, P.peek(), R.peek())) {
                    intersect_v = lineLineIntersection(q, linked, P.peek(), R.peek());
                    Vis.push(P.pop());
                } else {
                    while (!lineLineSegIntersection(q, linked, R.peek(), get_second_peek(R))) {
                        R.pop();
                    }
                    intersect_v = lineLineIntersection(q, linked, R.peek(), get_second_peek(R));
                }
                // pushes the intersection found on Vis
                vi_prev = P.pop();
                Vis.push(intersect_v);
            } else {
                //if no link has been found return to previous state and use delete corner,
                //because the outer right turn in a polygon winding is also an inner turn
                while (R.size() != 0 ) {
                    P.push(R.pop());
                }
                Vis.push(c);
                delete_covered_points(P,Vis,q,P.peek());
                inner_turn_before = true;
            }
        } else {
            //intersection is not in a polygon winding
            //continue to the usual case
            intersect_v.setPredecessor(P.peek());
            intersect_v.setSuccessor(get_second_peek(P));
            c.setInner_turn_corner(false);
            vi_prev = P.pop();
            Vis.push(intersect_v);
            Vis.push(P.pop());
            inner_turn_before = false;
        }
        //reset the angle counter and clear leftover Stack R
        reset_angle();
        R.clear();
    }

    private void fastForward(Stack<Point> P, Stack<Point> Vis, Point q, Point c) {


        //skips all the outer left turns until the next inner turn
        while ((visibleAngle(P.peek(), q, Vis.peek()) == false)) {
            vi_prev = P.pop();
        }
        //calls the deletecovered points for the inner turn
        inner_turn_before = true;
        delete_covered_points(P, Vis, q, P.peek());
    }

 
    protected void delete_covered_points(Stack<Point> P, Stack<Point> Vis, Point q, Point c) {
        //Point as new artificial edge node point
        Point linkedPoint = null;
        Point intersect_v = new Point();


        //deletes all vertices left to the vector qc
        while ((lineLineSegIntersection(q, c, Vis.peek(), get_second_peek(Vis)) == false)) {
            //if the popped point in Vis is not an intersection
            if (linkedPoint == null){
                linkedPoint = popLinked(Vis);
            } else {
                if ( linkedPoint.getPointLinked() == null){
                    linkedPoint = popLinked(Vis);
                    //if it is an intersection and the inner turn corner is not visible
                } else if (inRange(q, c) > inRange(q, linkedPoint.getPointLinked() )){
                    break;
                }else {
                    linkedPoint = popLinked(Vis);
                }
            }
            // if prior case of an intersection is found, so inner turn corner is not visible break out of loop
            if (linkedPoint != null
                    && linkedPoint.getPointLinked() != null
//                    && (inRange(q, c) > inRange(q, linkedPoint.getPointLinked())
            || (linkedPoint != null && visibleAngle(c,q,linkedPoint.getPointLinked()))){

            }
        }


        //check conditions for the intersection linked point that has been found in case of winding
        if (linkedPoint != null
                && linkedPoint.getPointLinked() != null
//                && (inRange(q, c) > inRange(q, linkedPoint.getPointLinked())
                ||  (linkedPoint != null && visibleAngle(c,q,linkedPoint.getPointLinked()))){
            //call the function that deletes every point until the linked point of the intersection has been found again
            previousPointCovered(P, Vis, q, c, linkedPoint.getPointLinked());

        } else {

            //else usual case of delete covered points of the inner turn
            intersect_v = lineLineIntersection(q, c, Vis.peek(), get_second_peek(Vis));
            intersect_v.setPredecessor(get_second_peek(Vis));
            intersect_v.setSuccessor(Vis.peek());
            Vis.pop();


            Vis.push(intersect_v);
            intersect_v.setCorner(c);
            c.setIntersect(intersect_v);
            c.setInner_turn_corner(true);
            Vis.push(P.pop());
            inner_turn_before = true;
        }
    }


    protected void previousPointCovered(Stack<Point> P, Stack<Point> Vis, Point q, Point c, Point linkpoint) {
        Point intersect_v;

        //search for the intersection of prior corner as linkpoint, in case an inner turn is called in polygonwinding
        //push on leftoverstack, in case of a following outer right turn
        while (lineLineSegIntersection(q, linkpoint, P.peek(), get_second_peek(P)) == false) {
            while (angle_sum_exceeded()) {
                R.push(P.pop());
                update_angle_sum(P.peek(), q, get_second_peek(P));
            }
            R.push(P.pop());
        }

        intersect_v = lineLineIntersection(q, linkpoint, P.peek(), get_second_peek(P));

        //check if the intersection holds the range condition, that the corner must be closer than the intersection
        // if it doesnt return the points to the usual state
        if( inRange(q,linkpoint) >= inRange(q,intersect_v)){
            while (R.size() != 0){
                P.push(R.pop());
            }
            inner_turn_before = false;
        } else {

            //intersection is found and set linkages, usual push to stack
            intersect_v.setPredecessor(P.peek());
            intersect_v.setSuccessor(get_second_peek(P));

            vi_prev = P.pop();
            intersect_v.setPointLinked(linkpoint);
            linkpoint.setIntersect(intersect_v);
            intersect_v.setCorner(linkpoint);
            linkpoint.setInner_turn_corner(false);
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
        } else {
            PointList = rearrangeList(PointList);
        }

        return PointList;
    }

    //find idx for Point that is a valid node of visibility Polygon
    private void pre_Processing_P(ArrayList<Point> PointList, Point q) {


        //find edge with smallest distance
        double smallest_dist = 10000;
        int idx = 0;
        for (int i = 0; i < PointList.size(); i++) {
            //smallest distance to visible edge is strating point that can be added to visiblity polygon
            if (smallest_dist >= closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), q)
                    && visibleAngle(PointList.get(i), q, PointList.get(decrementIdx(i)))) {
                smallest_dist = closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), q);
                idx = i;
            }
        }

        //push both visible Points in Vis
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


        //different signs mean there is an intersection, catch rounding errors
        boolean almost_zero = (cross_c > -0.00001 && cross_c <= 0) || (cross_d > -0.00001 && cross_d <= 0 )
                || (cross_c < 1.318767317570746E-7 && cross_c >= 0) || (cross_d < 1.318767317570746E-7  && cross_d >= 0);

        if ((Math.signum(cross_d) != Math.signum(cross_c) || cross_c == 0 || cross_d == 0  || almost_zero)
                && !(cross_c == 0 && cross_d == 0)) {
            return true;
        } else {
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

        //different signs mean there is an intersection
        if (cross_c == 0 || cross_d == 0) {
            return true;
        } else {
            return false;
        }
    }


    //test orientation of the input polygon
    private String testOrientation(ArrayList<Point> PointList) {

        //fast practical check: get smallest X-Coordinate (if two are the same smallest y Coord)
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
    }

    // orientation test addition to angle sum
    protected boolean angle_sum_exceeded() {
        if (angle_sum < 0.0) {
            return true;
        } else {
            return false;
        }
    }

    //resets the angle counter
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


    //tries to find a chain of previous linkages
    //used in the case of outer right turn in polygon winding
    protected Point popLinkedChain(Stack<Point> Stack, Point c, Point c_prev, Point link) {

        Point linked = link;

        if (Stack.size() <= 1) {
            return linked;
        }
        Point current = Stack.pop();
        Point prev = Stack.peek();

        //adjacency test so the whole polygon must not be iterated through
        if (current.getPointLinked() != null && AdjacentEdgesTest(c, c_prev, current, prev)) {
            return current;
        } else if (current.getPointLinked() == null && !AdjacentEdgesTest(c, c_prev, current, prev)) {
            return null;
        } else if (current.getPointLinked() == null && AdjacentEdgesTest(c, c_prev, current, prev)) {
            linked = popLinkedChain(Stack, c, c_prev, linked);
        }
        Stack.push(current);
        return linked;
    }

    // checks if vis(p) point is linked to a turn
    protected Point popLinked(Stack<Point> Stack) {
        Point point;
        point = Stack.pop();

        if (point.getPointLinked() != null) {
            return point;
        } else {
            point.setCorner(null);
            point.setIntersect(null);
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

    //gets the element after peek element
    protected Point get_second_peek(Stack<Point> Stack) {
        Point prev;
        Point current;

        current = Stack.pop();
        if (P.size() == 0) {
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

    //converst the Pointlist to a Stack
    private void List_to_Stack(ArrayList<Point> List, Stack<Point> P, int p_idx) {

        for (int i = 0; i < PointList.size(); i++) {
            P.push(List.get(p_idx));
            betavis_P.push(List.get(p_idx));
            if(betavis_P.size() > 1){
                get_second_peek(betavis_P).setPredecessor(betavis_P.peek());
            }
            p_idx = incrementIdx(p_idx);

        }
        betavis_P.lastElement().setPredecessor(betavis_P.firstElement());
    }

    public void showPushedNodes(){
        for (int i = 0; i < Vis.size();i++){
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(Vis.get(i))).setFill(Color.RED);
        }
    }


    // connects nodes of Vispolygon to form the visibility Polygon edges and adds that polygon to scene
    // extra to have distinct edges and a filled polygon
    protected void connectEdges(Stack<Point> Vis) {

        javafx.scene.shape.Polygon vis_polygon = new javafx.scene.shape.Polygon();


        //constructs the Stack for beta visibility while looping
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

}

