package geometry;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VisPolygon extends Polygon {

    // List for iteration through points for algorithm
    private ArrayList<Circle> PointList = (ArrayList<Circle>) GUI.polygon.getPointList().clone();
    // List with Elements that belong to visibility polygon
    private ArrayList<Circle> VisPointList = new ArrayList<>(PointList.size());
    private ArrayList<Line> VisEdgeList = new ArrayList<>(PointList.size());
    private double angle_sum = 0;
    private int p_idx;


    public VisPolygon() {
        super();

        //check if Polygon is given and p point for visibility is given
        if (GUI.polygon.getPolygonDrawn() && GUI.polygon.is_p_set()) {

            // preprocessing
            // ----------------------------------------
            PointList = pre_Processing_Points(PointList);
            pre_Processing_P(PointList, GUI.polygon.get_p());

            // calculate visibility
            // ----------------------------------------

            algorithm_default(PointList, GUI.polygon.get_p());

        } else {
            System.out.println("Input of Polygon and visibility Point p needed. \n");
            Settings.get().get_vis_p_Status().setSelected(false);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    private void algorithm_default(ArrayList<Circle> List, Circle p) {

        // calculate visible starting point (done in Pre-Processing)
        // ArrayList with visible point of polygon edge
        // Edge Point p_idx

        int start = decrementIdx(p_idx);

        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(VisPointList.get(0))).setFill(Color.RED);
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(VisPointList.get(1))).setFill(Color.RED);


        while (p_idx != start) {
            boundaryCycle(List, p);
        }

//        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(p_idx))).setFill(Color.ORANGE);
        connectEdges(VisPointList);
        addToScene(GUI.polygonscene, VisEdgeList);
    }

    private void boundaryCycle(ArrayList<Circle> List, Circle p) {


        //if angle is correct add to vispoints
        p_idx = incrementIdx(p_idx);
        //if signe is true means positive for the angle requirement (inverted or not) so visible


        if (getEvent(List.get(decrementIdx(p_idx)), p, List.get(p_idx), List.get(decrementIdx(decrementIdx(p_idx))), List)) {

        } else {
            // delete points that are hidden by this non visible edge

            inner_turn(List, p);
//                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(VisPointList.get(VisPointList.size() - 1))).setFill(Color.YELLOW);
//                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(p_idx))).setFill(Color.WHITE);
//                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(decrementIdx(p_idx)))).setFill(Color.YELLOW);

        }

    }


    private boolean getEvent(Circle c, Circle p, Circle v2, Circle prev_v1, ArrayList<Circle> List) {
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
        //turn inner: angle smaller than prev_angle
        //turn outer right: angle bigger than prev_angle
        //turn outer left: angle bigger than prev_angle and previous vertex not visible


        System.out.println("angle: " + angle + " prev_angle" + prev_angle);

        boolean outer_right_turn_event = prev_angle < angle && prev_angle > 0 && !visibleAngle(c, p, v2);
        boolean outer_left_turn_event = prev_angle > angle && prev_angle < 0 && angle < 0;

        if (outer_right_turn_event) {
            outer_right_turn(List, p);
            System.out.println("!!!");
            return true;
        }

        if (outer_left_turn_event) {
            outer_left_turn(List, p);
            System.out.println("case reached");
            return false;
        }

        if (visibleAngle(c, p, v2)) {
            VisPointList.add(List.get(p_idx));
            System.out.println("added " + p_idx);
            return true;
        }


        return false;
    }

    private void outer_right_turn(ArrayList<Circle> List, Circle p) {

        Circle c = VisPointList.get(VisPointList.size() - 1);
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(p_idx))).setFill(Color.GREEN);
        GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(c)).setFill(Color.GREENYELLOW);

        update_angle_sum(p,p_idx);
        p_idx = incrementIdx(p_idx);
//        System.out.println("p_idx " + p_idx);



//         add for special cases but erases simple cases cant be used normally checkAngle(c, p, List.get(p_idx)) > 90
        while (visibleAngle(c, p, List.get(p_idx)) == false
                || visibleAngle(List.get(decrementIdx(p_idx)), p, List.get(p_idx)) == false ||  angle_sum_exceeded() ) {
//            System.out.println("angle: " + checkAngle(c, p, List.get(p_idx)));
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(p_idx))).setFill(Color.GREEN);
//            System.out.println("p_idx: " + p_idx);
            update_angle_sum(p,p_idx);
            System.out.println("Bools: "+ (checkAngle(c, p, List.get(p_idx))) +" "+ (visibleAngle(List.get(decrementIdx(p_idx)), p, List.get(p_idx)) == false)  +" "+  angle_sum_exceeded());
            p_idx = incrementIdx(p_idx);
        }

        System.out.println("angle to c and p_idx: " +checkAngle(c, p, List.get(p_idx)));

        //create point of intersection
        Circle s_v =  new Circle();

        s_v = lineLineIntersection(c, p, List.get(decrementIdx(p_idx)), List.get(p_idx));


        if (s_v == null) {
            System.out.println("unexplainable error");
        }

        VisPointList.add(s_v);
        VisPointList.add(List.get(p_idx));


    }

    private void outer_left_turn(ArrayList<Circle> List, Circle p) {

        int current_corner = p_idx;

        while (visibleAngle(List.get(current_corner), p, List.get(incrementIdx(p_idx))) == true) {
            System.out.println("angle: " + checkAngle(List.get(current_corner), p, List.get(p_idx)));
            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(p_idx))).setFill(Color.BROWN);
            System.out.println("p_idx: " + p_idx);
            p_idx = incrementIdx(p_idx);
        }
        //outer_left_turn chain skipped
        p_idx = incrementIdx(p_idx);
    }


    private void inner_turn(ArrayList<Circle> List, Circle p) {


        Circle p_top = new Circle();


        //deletes all vertices left to the vector pc
        while (visibleAngle(List.get(p_idx), p, VisPointList.get(VisPointList.size() - 1)) == true) {
//            GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(List.get(p_idx))).setFill(Color.BLUE);
            try {
                GUI.polygon.getPointList().get(GUI.polygon.getPointList().indexOf(VisPointList.get(VisPointList.size() - 1))).setFill(Color.BLUE);
            } catch (Exception e) {
                System.out.println("invalid Starting Points found");
            }
            ;
            p_top = VisPointList.get(VisPointList.size() - 1);
            VisPointList.remove(VisPointList.size() - 1);
        }

        p_top = lineLineIntersection(p, List.get(p_idx), p_top, VisPointList.get(VisPointList.size() - 1));


        if (p_top == null) {
        }


        System.out.println("????? \n ");

        VisPointList.add(p_top);
        VisPointList.add(List.get(p_idx));
        GUI.polygon.addNode(p_top.getCenterX(), p_top.getCenterY());

    }


    // ----------------------------------------------------------------------------------------------------------------
    // Pre-Processing
    // ----------------------------------------------------------------------------------------------------------------


    // Sort Array Clockwise or Counterclockwise
    private ArrayList<Circle> pre_Processing_Points(ArrayList<Circle> PointList) {

        //test Orientation of the given Polygon
        if (testOrientation(PointList) == "clockwise") {
            System.out.println("Clockwise input of Poygon, rearranging Pointlist");
            PointList = rearrangeCounterClockwise(PointList);
        } else {

            System.out.println("Counterclockwise Input of Polygon");
        }

        return PointList;
    }

    //find idx for Point that is a valid node of visibility Polygon
    private void pre_Processing_P(ArrayList<Circle> PointList, Circle p) {

        if (p == null) {
            System.out.println("p is null");
        }

        //find point with smallest distance
        double smallest_dist = 10000;
        int idx = 0;
        for (int i = 0; i < PointList.size(); i++) {

            //check and save idx
            System.out.println("distance : " + closest_distance_to_linesegment(PointList.get(i), PointList.get(incrementIdx(i)), p));
            if (smallest_dist >= closest_distance_to_linesegment(PointList.get(i), PointList.get(incrementIdx(i)), p)
                    && visibleAngle(PointList.get(i),p, PointList.get(incrementIdx(i)))) {
                smallest_dist = closest_distance_to_linesegment(PointList.get(i), PointList.get(incrementIdx(i)), p);
                idx = i;
            }
        }

        System.out.println("smallest_dist: " + smallest_dist);
        // idx is 0 so connecting node is at end of List, or idx is at end so connectin node is at beginning
        // check both adjacent nodes one must be visible ( with positive angle)

        VisPointList.add(PointList.get(idx));
        VisPointList.add(PointList.get(incrementIdx(idx)));
        p_idx = incrementIdx(idx);


        System.out.println("Vispoint  x y : " + VisPointList.get(0).getCenterX() + " " + VisPointList.get(0).getCenterY() + " " + VisPointList.get(1).getCenterX() + " " + VisPointList.get(1).getCenterY());
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Geometry Helpers
    // ----------------------------------------------------------------------------------------------------------------


    private boolean visibleAngle(Circle v1, Circle p, Circle v2) {
        return checkAngle(v1, p, v2) > 0;
    }

    // v1 first node, v2 second node, p point of visibility
    private double checkAngle(Circle v1, Circle p, Circle v2) {

        double angle = 0;
        double x1 = v1.getCenterX() - p.getCenterX();
        double y1 = v1.getCenterY() - p.getCenterY();
        double x2 = v2.getCenterX() - p.getCenterX();
        double y2 = v2.getCenterY() - p.getCenterY();

        //determinant and dot product
        angle = Math.atan2(x2 * y1 - y2 * x1, x2 * x1 + y2 * y1) * 180 / Math.PI;

        return angle;
    }


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
            System.out.println(" NO INTERSECTION FOUND IN COVER DELETION");
            return null;
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return createNode(x, y);
        }
    }


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


// ----------------------------------------------------------------------------------------------------------------
// List, Point, idx handling
// ----------------------------------------------------------------------------------------------------------------

    private void update_angle_sum( Circle p ,int p_idx) {
        angle_sum += checkAngle(PointList.get(decrementIdx(p_idx)),p,PointList.get(p_idx));
        if (angle_sum >= 0) {
            angle_sum = 0;
        }
    }

    private boolean angle_sum_exceeded (){
        if (angle_sum < -180){
            System.out.println("angle sum exceeded: "+ angle_sum);
            return true;
        } else {
            System.out.println("angle sum not exceeded: "+ angle_sum);
            return false;
        }
    }

    private int incrementIdx(int idx) {
        idx++;
        idx = idx % PointList.size();

        return idx;
    }

    private int decrementIdx(int idx) {
        idx--;
        idx = idx + PointList.size();
        idx = idx % PointList.size();
        return idx;
    }

    public void deleteVisPolygon() {
        VisPointList.clear();
        GUI.polygonscene.getChildren().clear();
    }

    public ArrayList<Circle> rearrangeCounterClockwise(ArrayList<Circle> List) {
        ArrayList<Circle> PointListInvers = new ArrayList<>(List.size());

        for (int i = List.size() - 1; i >= 0; i--) {
            PointListInvers.add(List.get(i));
        }
        return PointListInvers;
    }


    private void connectEdges(ArrayList<Circle> List) {
        double[] x = new double[List.size()];
        double[] y = new double[List.size()];

        javafx.scene.shape.Polygon vis_polygon = new javafx.scene.shape.Polygon();

        for (int i = 0; i < List.size() - 1; i++) {
            vis_polygon.getPoints().addAll(List.get(i).getCenterX(), y[i] = List.get(i).getCenterY());
        }

        vis_polygon.setStroke(Color.DARKRED);
        vis_polygon.setStrokeWidth(4);

        vis_polygon.setFill(Color.RED);
        vis_polygon.setOpacity(0.4);

        GUI.polygonscene.getChildren().add(vis_polygon);


//        for (int i = 0; i< List.size()-1;i++){
//            VisEdgeList.add(createEdge(List.get(i).getCenterX(),List.get(i).getCenterY(),
//                    List.get(i+1).getCenterX(),List.get(i+1).getCenterY()));
//        }
//        VisEdgeList.add(createEdge(List.get(List.size()-1).getCenterX(),List.get(List.size()-1).getCenterY(),
//                List.get(0).getCenterX(),List.get(0).getCenterY()));

    }

    private void addToScene(Group scene, ArrayList<Line> e) {
        for (int i = 0; i < e.size(); i++) {
            scene.getChildren().add(e.get(i));
        }
    }
}

