package geometry;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Stack;

public class VisPolygon extends Polygon {

    // List for iteration through points for algorithm
    private ArrayList<Circle> PointList = (ArrayList<Circle>) GUI.polygon.getPointList().clone();
    private Stack<Circle> P = new Stack<Circle>();
    private Stack<Circle> S = new Stack<Circle>();
    // List with Elements that belong to visibility polygon
    private ArrayList<Circle> VisPointList = new ArrayList<>(PointList.size());
    private ArrayList<Line> VisEdgeList = new ArrayList<>(PointList.size());
    private double angle_sum = 0;
    private String angle_orient = "left";
    private int p_idx;
    private Circle vi_prev;
    private boolean inner_turn_before = false;
    private boolean dot_collinear = false;



    public VisPolygon() {
        super();
        //check if Polygon is given and p point for visibility is given
        if (GUI.polygon.getPolygonDrawn() && GUI.polygon.is_p_set()) {

            // preprocessing
            // ----------------------------------------
            PointList = pre_Processing_Points(PointList);
            pre_Processing_P(PointList, GUI.polygon.get_p());
            List_to_Stack(PointList, P, p_idx);

            // calculate visibility
            // ----------------------------------------
            algorithm_default(P, S, GUI.polygon.get_p());

        } else {
            System.out.println("Input of Polygon and visibility Point p needed. \n");
            Settings.get().get_vis_p_Status().setSelected(false);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    private void algorithm_default(Stack<Circle> P, Stack<Circle> S, Circle p) {

        // calculate visible starting point (done in Pre-Processing)
        // ArrayList with visible point of polygon edge

        Circle start = S.lastElement();

        vi_prev = get_second_peek(S);
        //cycles through all nodes of polygon
        while (!(P.size()<=1 )) {
            boundaryCycle(P, S, p, start);
            System.out.println("P size: " + P.size());
            System.out.println("S size: " + S.size());
        }

        //connects all nodes of Polygon and makes visibilityPolygon visible on Interface
        connectEdges(S);
        addToScene(GUI.polygonscene, VisEdgeList);
    }

    private void boundaryCycle(Stack<Circle> P, Stack<Circle> S, Circle p, Circle start) {
        //finds the turn event for current cycle

//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(vi_prev)).setFill(Color.YELLOW);

        while (!getEvent(S.peek(), p, P.peek(), vi_prev, P, S)){

            System.out.println("boundary cycling");
            System.out.println("P size: " + P.size());
            System.out.println("S size: " + S.size());
            System.out.println("visible angle: "+ visibleAngle(S.peek(),p,P.peek()));

            if (angle_orient == "left") {
                vi_prev = S.peek();
                S.push(P.pop());
            } else if (angle_orient == "right") {
                System.out.println("delete covered points entered");
                delete_covered_points(P, S, p, S.peek());
            }

            //catch stackempt exception, Algorithm done by this point
            if (P.size() <= 1 ){
                S.pop();
                break;
            }
            System.out.println("Event: " + !getEvent(S.peek(), p, P.peek(), get_second_peek(S), P, S));
        }
    }


    private boolean getEvent(Circle c, Circle p, Circle v2, Circle prev_v1, Stack<Circle> P, Stack<Circle> S) {
        double angle = 0;
        double prev_angle = 0;



        // x y coords of points v1
        double x_p = p.getCenterX() - c.getCenterX();
        double y_p = p.getCenterY() - c.getCenterY();

        // x y coords of points v2
        double x_v2 = v2.getCenterX() - c.getCenterX();
        double y_v2 = v2.getCenterY() - c.getCenterY();

        // x y coords of prev_v1
        double x_prev = prev_v1.getCenterX() - c.getCenterX();
        double y_prev = prev_v1.getCenterY() - c.getCenterY();

        // angle between line of p and current vertex
        angle = Math.atan2(x_v2 * y_p - y_v2 * x_p, x_v2 * x_p + y_v2 * y_p) * 180 / Math.PI;
        // angle between line of p and prev vertex
        prev_angle = Math.atan2(x_prev * y_p - y_prev * x_p, x_prev * x_p + y_prev * y_p) * 180 / Math.PI;

        //cases:
        //turn inner: depending on sign. prev_angle has always a bigger angle than angle
        //turn outer right: angle bigger than prev_angle
        //turn outer left: angle bigger than prev_angle and previous vertex not visible

        boolean collinear =  Math.round(angle) == 180.0 || Math.round(angle) == -180.0 || Math.round(prev_angle) == 0;
        boolean inner_turn_event = ((prev_angle > angle && angle > 0 && prev_angle > 0)
                || (prev_angle < angle && angle < 0 && prev_angle < 0)
                || ( prev_angle < 0 && angle > 0)) &&  visibleAngle(c,p,v2) == false && !collinear;
        boolean outer_right_turn_event = (prev_angle < angle && prev_angle >= 0
                && !visibleAngle(c, p, v2)  && !collinear)
                || (Math.round(angle) == 90 && Math.round(prev_angle) == 0);
        boolean outer_left_turn_event = (prev_angle > angle && angle < 0 && prev_angle < 0)
                && !visibleAngle(prev_v1,p,c) && visibleAngle(c,p,v2)
                && inner_turn_before == true  && !collinear;

        System.out.println("angle: "+ angle+ " prev_angle: "+ prev_angle);
        if (inner_turn_event) {
            System.out.println("inner turn entered");
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(S.peek())).setFill(Color.YELLOW);
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.YELLOW);
//            angle_orient_invert();
            vi_prev = S.peek();
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(vi_prev)).setFill(Color.YELLOW);
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(c)).setFill(Color.BLUE);
            delete_covered_points(P, S, p, P.peek());
            return true;
        }


        if (outer_right_turn_event ) {
            System.out.println("outer right turn entered");
            vi_prev = S.peek();
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(vi_prev)).setFill(Color.YELLOW);

            FastForwardIntersect(P, S, p, c);
            return true;
        }

        if (outer_left_turn_event)  {
            System.out.println("outer left turn entered");
            FastForward(P, S, p, c);
            return true;
        }
        return false;
    }

    private void FastForwardIntersect(Stack<Circle> P, Stack<Circle> S, Circle p, Circle c) {
        Circle intersect_v = new Circle();
        Circle p_prev = new Circle();
        System.out.println("check angle:"+ checkAngle(S.peek(), p,P.peek()));
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(S.peek())).setFill(Color.BLUE);




        while ((lineLineSegIntersection(p, c, P.peek(), get_second_peek(P)) == false  || angle_sum_exceeded()) ) {
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.GREEN);
            System.out.println("in Fast ForwardIntersect");
            System.out.println("check angle:"+ checkAngle( P.peek(),p, get_second_peek(P)));
//            if (dot_collinear==true && !angle_sum_exceeded()){
//                System.out.println("broke out");
//                P.push(p_prev);
//                break;
//            }
            p_prev = P.pop();
            update_angle_sum(P.peek(), p, get_second_peek(P));
        }
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.GREEN);

        //line intersection of 4 points (2 node for first edge, 2 for second edge)
        intersect_v = lineLineIntersection(p, c, P.peek(), get_second_peek(P));
        System.out.println("intersection created");

        S.push(intersect_v);
        S.push(P.pop());
        inner_turn_before = false;
        reset_angle();
    }

    private void FastForward(Stack<Circle> P, Stack<Circle> S, Circle p, Circle c) {
        System.out.println("check angle:"+ checkAngle(S.peek(), p,P.peek()));
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(S.peek())).setFill(Color.GRAY);


        while ((visibleAngle(P.peek() ,p, get_second_peek(S))==false) ) {
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.GRAY);
            System.out.println("in Fast Forward");
            vi_prev = P.pop();
        }
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(P.peek())).setFill(Color.GRAY);
        inner_turn_before = false;
    }


    private void delete_covered_points(Stack<Circle> P, Stack<Circle> S, Circle p, Circle c) {
        //circle as new artificial edge node point
        Circle intersect_v = new Circle();
        update_angle_sum(S.peek(), p, get_second_peek(S));


        //deletes all vertices left to the vector pc
        // || angle_sum_exceeded()
        while ((lineLineSegIntersection(p, c, S.peek(),get_second_peek(S) ) == false) ) {
            update_angle_sum(S.peek(), p, get_second_peek(S));
            System.out.println("in delete covered points");
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(S.peek())).setFill(Color.WHITE);
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(get_second_peek(S))).setFill(Color.WHITE);
            S.pop();
        }


//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(S.peek())).setFill(Color.DARKCYAN);
//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(S_prev)).setFill(Color.DARKCYAN);
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(c)).setFill(Color.DARKCYAN);
        //line intersection of 4 points (2 node for first edge, 2 for second edge)
        intersect_v = lineLineIntersection(p, c, S.peek(),get_second_peek(S));


        S.push(intersect_v);
        S.push(P.pop());
        System.out.println("intersection created");
        inner_turn_before = true;
        reset_angle();

    }


    // ----------------------------------------------------------------------------------------------------------------
    // Pre-Processing
    // ----------------------------------------------------------------------------------------------------------------


    // Sort Array Clockwise or Counterclockwise
    private ArrayList<Circle> pre_Processing_Points(ArrayList<Circle> PointList) {

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
    private void pre_Processing_P(ArrayList<Circle> PointList, Circle p) {

        if (p == null) {
        }

        //find edge with smallest distance
        double smallest_dist = 10000;
        int idx = 0;
        for (int i = 0; i < PointList.size(); i++) {
            System.out.println("distance: "+ closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), p));
            //smallest distance to visible edge is strating point that can be added to visiblity polygon
            if (smallest_dist >= closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), p)
                    && visibleAngle(PointList.get(i), p, PointList.get(decrementIdx(i)))) {
                smallest_dist = closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), p);
                idx = i;
                System.out.println("smallest distance: "+ closest_distance_to_linesegment(PointList.get(i), PointList.get(decrementIdx(i)), p));

            }
        }

        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(PointList.get(idx))).setFill(Color.RED);
        S.push(PointList.get(idx));
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(PointList.get(decrementIdx(idx)))).setFill(Color.RED);
        S.push(PointList.get(decrementIdx(idx)));
        p_idx = decrementIdx(idx);
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Geometry Helpers
    // ----------------------------------------------------------------------------------------------------------------

    //returns true for left orientation of points, false for right orientation
    private boolean visibleAngle(Circle v1, Circle p, Circle v2) {

        return checkAngle(v1, p, v2) > 0;
    }



    //finds intersection of edges given bei 2 points each
    private Circle lineLineIntersection(Circle A, Circle B, Circle C, Circle D) {

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
    private boolean lineLineSegIntersection(Circle A, Circle B, Circle C, Circle D) {

        dot_collinear=false;
        //construct normal vector to line AB
        double n_x =  A.getCenterX() - B.getCenterX();
        double n_y = A.getCenterY() - B.getCenterY();

        //vector CB
        double ca_x = A.getCenterX()-C.getCenterX();
        double ca_y = A.getCenterY()-C.getCenterY();

        //vector CB
        double da_x = A.getCenterX()-D.getCenterX();
        double da_y = A.getCenterY()-D.getCenterY();

        //dot product of both Points C and D, sign check afterwards
        double dot_c = n_x * ca_y - n_y * ca_x;
        double dot_d = n_x * da_y - n_y * da_x;

        System.out.println("dot_c: "+ dot_c);
        System.out.println("dot_d: "+ dot_d);


        //different signs mean there is an intersection



        if ( Math.signum(dot_d) != Math.signum(dot_c) || dot_c == 0 || dot_d == 0){
            System.out.println("intersection of segment");
            if ( dot_c == 0 || dot_d == 0){
                dot_collinear = true;
            }
            return true;
        } else {
            System.out.println(" no  intersection of segment");
            return false;
        }
    }



    //test orientation of the input polygon
    private String testOrientation(ArrayList<Circle> PointList) {

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
    private double closest_distance_to_linesegment(Circle c1, Circle c2, Circle p) {

        //dot product
        double dx = c2.getCenterX() - c1.getCenterX();
        double dy = c2.getCenterY() - c1.getCenterY();

        double px = p.getCenterX();
        double py = p.getCenterY();

        double c1x = c1.getCenterX();
        double c1y = c1.getCenterY();

        double c2x = c2.getCenterX();
        double c2y = c2.getCenterY();

        double dot = ((px - c1x) * dx + (py - c1y) * dy);
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

        dx = px - x;
        dy = py - y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    //adds angle to catch the case for angle > 360
    private void update_angle_sum(Circle v1, Circle p, Circle v2) {
        angle_sum += checkAngle(v1, p, v2);
        if (angle_sum >= 0) {
            angle_sum = 0;
        }
        System.out.println("angle sum: "+ angle_sum);
    }

    // orientation test addition to angle sum
    private boolean angle_sum_exceeded() {
        if (Math.round(angle_sum) < 0.0) {
            return true;
        } else {
            return false;
        }
    }

    private void reset_angle(){
        angle_sum = 0;
    }

    private void angle_orient_invert() {
        if (angle_orient == "left") {
            angle_orient = "right";
        } else if (angle_orient == "right") {
            angle_orient = "left";
        }
    }

// ----------------------------------------------------------------------------------------------------------------
// List, Point, idx handling
// ----------------------------------------------------------------------------------------------------------------


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

    private Circle get_second_peek(Stack<Circle> Stack) {
        Circle prev;
        Circle current;
        current = Stack.pop();
        if(P.size() <= 1){
            prev = S.lastElement();
        }else {
            prev = Stack.peek();
        }
        Stack.push(current);
        return prev;
    }



    //clears VisPolygon
    public void deleteVisPolygon() {
        VisPointList.clear();
        VisEdgeList.clear();
        GUI.polygonscene.getChildren().clear();
    }

    // if polygon input is clockwise order to counterclockwise
    public ArrayList<Circle> rearrangeList(ArrayList<Circle> List) {
        ArrayList<Circle> PointListInvers = new ArrayList<>(List.size());

        for (int i = List.size() - 1; i >= 0; i--) {
            PointListInvers.add(List.get(i));
        }
        return PointListInvers;
    }

    private void List_to_Stack(ArrayList<Circle> List, Stack<Circle> P, int p_idx) {

        for (int i = 0; i < PointList.size(); i++) {
            P.push(List.get(p_idx));
            p_idx = incrementIdx(p_idx);
        }
    }


    //connects nodes of Vispolygon to form the visibility Polygon edges and adds that polygon to scene
    // extra to have distinct edges and a filled polygon
    private void connectEdges(Stack<Circle> S) {

        javafx.scene.shape.Polygon vis_polygon = new javafx.scene.shape.Polygon();


        int stack_size = S.size();
        for (int i = 0; i < stack_size ; i++) {

            vis_polygon.getPoints().addAll(S.peek().getCenterX(), S.pop().getCenterY());
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
}

