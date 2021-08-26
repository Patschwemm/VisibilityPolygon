package geometry;

import javafx.scene.paint.Color;
import java.util.Stack;
import java.util.Collections;

public class Beta_Visibility_Brute extends VisPolygon {

    private final Stack<Point> P = GUI.vis_q.getBetavis_P();
    private final Stack<Point> Vis = GUI.vis_q.getBetavis_Vis();
    private final Stack<Point> B_vis = new Stack<Point>();


    public Beta_Visibility_Brute(double beta) {
        //check if Vis Polygon is computed
        if (GUI.vis_q != null) {

            //resets angle counter
            reset_angle();

            // copy array so we have it for several BetaVisibility runs
            Stack<Point> P_temp = (Stack<Point>) this.P.clone();
            Stack<Point> Vis_temp = (Stack<Point>) this.Vis.clone();

            //rotate by VisPolygon calculated Stacks to have them in similar form
            Collections.rotate(Vis_temp, 1);
            Collections.rotate(P_temp, -1);

            // calculate visibility
            beta_visibility(P_temp, Vis_temp, beta);
        } else {
            System.out.println("Cannot compute Beta Visibility, check necessities. \n");
        }
    }

    private void beta_visibility(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta) {


        //cycle until Stacks are empty
        while (P_temp.size() != 0 && Vis_temp.size() != 0) {
            polygonCycle(P_temp, Vis_temp, beta, GUI.polygon.get_q());
        }

        //draws the polygon and connects points to edges
        connectEdges(B_vis);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Algorithm
    // ----------------------------------------------------------------------------------------------------------------

    private void polygonCycle(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q) {

        //synchronously on Vis_temp and P_temp Stacks finds the same points
        //if the points are not the same, a cave has been found
        while (P_temp.size() != 0 && Vis_temp.size() != 0 && P_temp.peek() == Vis_temp.peek()) {
            B_vis.push(P_temp.pop());
            Vis_temp.pop();
        }

        //right cave
        if (B_vis.peek().getIntersect() != null && B_vis.peek().isInner_turn_corner() == false && P_temp.size() != 0 && Vis_temp.size() != 0) {
            rightCave(P_temp, B_vis, Vis_temp, beta, q);
            polygonCycle(P_temp, Vis_temp, beta, q);

        //left cave as inner turn
        } else if(B_vis.peek().getIntersect() != null && B_vis.peek().isInner_turn_corner() == true && P_temp.size() != 0 && Vis_temp.size() != 0) {
            leftCave(P_temp, B_vis, Vis_temp, beta, q);
            polygonCycle(P_temp, Vis_temp, beta, q);

        //catch case if two intersections points are adjacent
        } else if (B_vis.peek().getIntersect() == null  && P_temp.size() != 0 && Vis_temp.size() != 0) {
            leftCave(P_temp, B_vis, Vis_temp, beta, q);
            polygonCycle(P_temp, Vis_temp, beta, q);
        }

        //call polygoncycle until the (recursive Stack) is empty
        while (P_temp.size() != 0 && Vis_temp.size() != 0) {
            polygonCycle(P_temp, Vis_temp, beta, q);
        }
    }

    // ----------------------------------------------------------------------------------------------------------
    // Right cave functions
    // ----------------------------------------------------------------------------------------------------------


    public void rightCave(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q) {

        //get cave beginning and calculate angle:
        Point c_outerturn = B_vis.peek();
        double alpha_init = Math.abs(checkAngle(P_temp.peek(), c_outerturn, Vis_temp.peek()));


        //recursive beta calculated from point q
        //if the q is not the original q, calculate angles for remaining beta
        if (q != GUI.polygon.get_q()) {
            Point q_parent = q.getTreeParent();
            Point prev_end = simulateRayEndPoint(q_parent, q);
            beta = beta - Math.abs(checkAngle(prev_end, q, c_outerturn));
        }

        //bugfix purpose for artefacts
        if ( alpha_init == 0.0){

        } else if (alpha_init > beta) {
            //sets corner linkages
            c_outerturn.setSuccessor(P_temp.peek());
            c_outerturn.setTreeParent(q);
            q.setTreeChild(c_outerturn);
            c_outerturn.setLocalBeta(beta);

            //calculates the intersection, cycles to that intersection
            //calculate visibility until intersection point with c_outerturn as q
            Point intersect_v = caveIntersectRotateRight(P_temp, Vis_temp, beta, q, c_outerturn, get_second_peek(Vis_temp));
            cycleToIntersectPoint(P_temp, intersect_v);
            partlyRecursivePolygon(P_temp, B_vis, Vis_temp, beta, c_outerturn, intersect_v, Vis_temp.peek());

        } else if (alpha_init <= beta) {
            //sets corner linkages
            c_outerturn.setSuccessor(P_temp.peek());
            c_outerturn.setTreeParent(q);
            q.setTreeChild(c_outerturn);
            c_outerturn.setLocalBeta(beta);

            //calculate visibility until intersection point with c_outerturn as q
            recursivePolygonVisibility(P_temp, B_vis, Vis_temp, beta, c_outerturn, Vis_temp.peek());
        }
    }




    public Point caveIntersectRotateRight(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q, Point c, Point end) {

        //clones Stack P and finds the definitive intersection
        Stack<Point> P_clone = (Stack<Point>) P_temp.clone();
        Point q_rotation;


        q_rotation = rotatePredeccPointClockwise(beta, c, q);
        Point intersect_v = null;
        Point temp_intersect;
        double min_range = 1000000000;

        //loops as long as P Stack top element is either the end
        //or the successor to the end if the end is exclusive to $Vis$
        while ((P_clone.peek() != end ) && P_clone.size() >= 2) {
            try{
                if( P_clone.peek() == end.getSuccessor()){
                    break;
                }
            }catch(Exception e){ }
            //if a valid intersection is found
            if (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true
                    && visibleAngle(P_clone.peek(), c, Vis_temp.peek())
                    && visibleAngle(P_clone.peek(), c, get_second_peek(P_clone))) {
                temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone));
                temp_intersect.setSuccessor(get_second_peek(P_clone));
                temp_intersect.setPredecessor(P_clone.peek());
                if (min_range > inRange(q_rotation, temp_intersect) && visibleAngle(temp_intersect, c, Vis_temp.peek())) {
                    min_range = inRange(q_rotation, temp_intersect);
                    intersect_v = temp_intersect;
                }
            }
            P_clone.pop();
            //checks for the last edge which does not in P_clone but the intersections as well
            if (P_clone.size()==1 && end != null && lineLineSegIntersection(q_rotation, c, P_clone.peek(), end) == true) {
                temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), end);
                temp_intersect.setSuccessor(get_second_peek(P_clone));
                temp_intersect.setPredecessor(P_clone.peek());
                if (min_range > inRange(q_rotation, temp_intersect)) {
                    min_range = inRange(q_rotation, temp_intersect);
                    intersect_v = temp_intersect;
                }
            }
        }
        return intersect_v;
    }

    //cycles through P until intersect point
    protected void cycleToIntersectPoint(Stack<Point> P_temp, Point intersect_v) {
        while (P_temp.peek() != intersect_v.getPredecessor()) {
            P_temp.pop();
        }
    }


    protected Stack<Point> getPartPolygonRight(Stack<Point> P_temp, Stack<Point> Vis_temp, Point end) {
        Stack<Point> temp = new Stack<>();

        //gets the remaining polygon until end or end succesor if end is exclusive to Vis
        while ((P_temp.size() != 0) && (Vis_temp.size() == 1 || P_temp.peek() != end )) {
            try {
                if (P_temp.peek() == end.getSuccessor()) {
                    break;
                }
            } catch (Exception e) { }
            temp.push(P_temp.pop());
        }
        return temp;
    }

    protected Stack<Point> getWholePolygonRight(Stack<Point> P_temp, Stack<Point> Vis_temp, Point end) {
        Stack<Point> temp = new Stack<>();

        //gets the whole polygon until cave ending
        while (P_temp.size() != 0 && P_temp.peek() != get_second_peek(Vis_temp)) {
            try {
                if (P_temp.peek() == end.getSuccessor()) {
                    break;
                }
            } catch (Exception e) { }
            temp.push(P_temp.pop());
        }
        return temp;
    }



    protected void partlyRecursivePolygon(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end) {

        //initialize the recursive Stacks
        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();
        P_temp.pop();

        //gets the remaining polygon construction
        temp = getPartPolygonRight(P_temp, Vis_temp, end);


        //if temp size is zero, no node has to be considered for recursive visibility calculation
        if (temp.size() == 0) {
            B_vis.push(intersect_v);
            B_vis.push(Vis_temp.pop());
        } else {

            //construct polygon for recursive visibility
            temp.push(Vis_temp.pop());
            temp.push(B_vis.peek());
            temp.push(intersect_v);

            //process the Stack orientation
            Collections.rotate(temp, 1);
            Collections.reverse(temp);

            // add initial starting points
            P_rec = (Stack<Point>) temp.clone();
            B_vis.peek().setLocalBeta(beta);
            Vis_rec.push(end);
            Vis_rec.push(B_vis.peek());

            //call vis algorithm
            algorithm_default(temp, Vis_rec, q);

            // remove point q, because they are obsolete for the polygon
            P_rec.remove(q);
            Vis_rec.remove(q);

            //process the Stack Orientations
            Collections.reverse(Vis_rec);
            Collections.rotate(Vis_rec, 1);


            //cycle polygon with new query Point q
            polygonCycle(P_rec, Vis_rec, beta, q);
        }
    }


    protected void recursivePolygonVisibility(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point end) {

        //initialize the recursive Stacks
        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();

        //construct polygon for recursive visibility
        temp = getWholePolygonRight(P_temp, Vis_temp, end);
        temp.push(end);
        temp.push(Vis_temp.pop());
        temp.push(B_vis.peek());


        //process the Stack orientation
        Collections.rotate(temp, 1);
        Collections.reverse(temp);

        // add initial starting points
        P_rec = (Stack<Point>) temp.clone();
        B_vis.peek().setLocalBeta(beta);
        Vis_rec.push(end);
        Vis_rec.push(B_vis.peek());


        //call vis algorithm
        algorithm_default(temp, Vis_rec, q);

        // remove point q and B_vis peek, because they are obsolete for the polygon, as it is already added
        P_rec.remove(q);
        Vis_rec.remove(q);
        Vis_rec.remove(B_vis.peek());

        //process the Stack Orientations
        Collections.reverse(Vis_rec);
        Collections.rotate(Vis_rec, 1);

        //cycle polygon with new query Point q
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

        //get cave beginning and calculate angle:
        double alpha = Math.abs(checkAngle(Vis_temp.peek(), c_innerturn, c_innerturn_prev));

        //recursive beta calculated from point q
        // if the q is not the original q, calculate angles for remaining beta
        if (q != GUI.polygon.get_q()) {
            Point q_parent = q.getTreeParent();
            Point prev_end = simulateRayEndPoint(q_parent, q);
            beta = beta - Math.abs(checkAngle(prev_end, q, c_innerturn));
        }

        //for artefact points
        if(alpha == 0.0){

        } else if (alpha > beta) {

            //linkage for corner c_innerturn
            Point Vis_peek = Vis_temp.pop();
            c_innerturn.setSuccessor(Vis_peek.getSuccessor());
            Vis_temp.push(Vis_peek);
            c_innerturn.setTreeParent(q);
            q.setTreeChild(c_innerturn);
            c_innerturn.setLocalBeta(beta);

            //calculate intersection point, and then calculate visibility for everything until intersection point
            Point intersect_v = caveIntersectRotateLeft(P_temp, Vis_temp, beta, q, c_innerturn, c_innerturn_prev);
            partlyRecursivePolygonLEFT(P_temp, B_vis, Vis_temp, beta, c_innerturn, intersect_v, Vis_temp.peek());

        } else if (alpha <= beta) {

            //set linkage
            c_innerturn.setSuccessor(P_temp.peek());
            c_innerturn.setTreeParent(q);
            q.setTreeChild(c_innerturn);
            c_innerturn.setLocalBeta(beta);

            //calculate visibility for the whole area
            recursivePolygonVisibilityLeft(P_temp, B_vis, Vis_temp, beta, c_innerturn, Vis_temp.peek());
        }
    }


    public Point caveIntersectRotateLeft(Stack<Point> P_temp, Stack<Point> Vis_temp, double beta, Point q, Point c, Point end) {


        //local cloned P and q rotation
        Stack<Point> P_clone = (Stack<Point>) P_temp.clone();
        Point q_rotation;

        //calculate rotation point set local variables
        q_rotation = rotatePredeccPointCounterClockwise(beta, c, q);
        P_clone.push(B_vis.peek());
        Point intersect_v = null;
        Point temp_intersect;
        double min_range = 1000000000;

        //loop until intersection is found, test range for definitive true intersection point
        while (P_clone.peek() != end && P_clone.size() >= 2) {
            if (lineLineSegIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone)) == true
//                        && (visibleAngle(Vis_temp.peek(), c, get_second_peek(P_clone)))
                    && visibleAngle(P_clone.peek(), c, get_second_peek(P_clone))) {
                temp_intersect = lineLineIntersection(q_rotation, c, P_clone.peek(), get_second_peek(P_clone));
                temp_intersect.setSuccessor(get_second_peek(P_clone));
                temp_intersect.setPredecessor(P_clone.peek());
                //check ranges for intersection
                if (min_range > inRange(q_rotation, temp_intersect) && visibleAngle(Vis_temp.peek(), c, temp_intersect)) {
                    min_range = inRange(q_rotation, temp_intersect);
                    intersect_v = temp_intersect;
                }
            }
            P_clone.pop();
        }
        return intersect_v;
    }

    protected Stack<Point> getPartPolygonLeft(Stack<Point> P_temp, Stack<Point> Vis_temp, Point intersect_v) {

        //stack for polygon construction
        Stack<Point> temp = new Stack<>();

        //gets Points until intersection Point
        while (P_temp.peek() != intersect_v.getSuccessor()) {
            temp.push(P_temp.pop());
        }
        temp.push(intersect_v);

        // pop every point until inner turn cornerpoint as ending of the left cave, as they will never be visible
        while ((P_temp.size() != 0) && (Vis_temp.size() == 1 || P_temp.peek() != get_second_peek(Vis_temp))) {
            P_temp.pop();
        }

        return temp;
    }


    protected Stack<Point> getWholePolygonLeft(Stack<Point> P_temp, Stack<Point> Vis_temp, Point c_innerturn) {

        //initialize local stacks as constructed stacks
        Stack<Point> P_rec = new Stack<>();
        Point end = Vis_temp.peek();
        P_rec.push(end);

        //loop until the inner turn corner is found, as ending of the left cave
        while (P_temp.peek() != c_innerturn) {
            P_rec.push(P_temp.pop());
        }

        return P_rec;
    }

    protected void partlyRecursivePolygonLEFT(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point intersect_v, Point end) {

        //initialize the recursive Stacks
        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();

        //gets the remaining polygon construction
        temp = getPartPolygonLeft(P_temp, Vis_temp, intersect_v);

        //set inner turn and its previous point
        Point c_innerturn = end.getCorner();
        Point c_innerturn_prev = c_innerturn.getPredecessor();

        //if temp size is zero, no node has to be considered for recursive visibility calculation
        if (temp.size() == 0) {
            B_vis.push(Vis_temp.pop());
            B_vis.push(intersect_v);
        } else {

            //construct polygon for recursive visibility
            temp.push(intersect_v);
            temp.push(c_innerturn_prev);
            temp.push(c_innerturn);

            //process the Stack orientation
            Collections.reverse(temp);

            // add initial starting points and endings
            temp.push(end);
            P_rec = (Stack<Point>) temp.clone();
            B_vis.peek().setLocalBeta(beta);
            Vis_rec.push(c_innerturn_prev);
            Vis_rec.push(c_innerturn);

            //call vis algorithm
            algorithm_default(temp, Vis_rec, q);

            //remove already contained points
            P_rec.remove(c_innerturn);
            Vis_rec.remove(c_innerturn);
            P_rec.remove(c_innerturn_prev);
            Vis_rec.remove(c_innerturn_prev);

            //process the Stack Orientations
            Collections.reverse(Vis_rec);

            //cycle polygon with new query Point q
            polygonCycle(P_rec, Vis_rec, beta, q);

            //delete Vis_temp pop as artefact, it is already included
            Vis_temp.pop();
        }
    }


    protected void recursivePolygonVisibilityLeft(Stack<Point> P_temp, Stack<Point> B_vis, Stack<Point> Vis_temp, double beta, Point q, Point end) {

        //initialize the recursive Stacks
        Stack<Point> temp = new Stack<>();
        Stack<Point> P_rec = new Stack<>();
        Stack<Point> Vis_rec = new Stack<>();


        //construct polygon for recursive visibility
        temp = getWholePolygonLeft(P_temp, Vis_temp, q);
        temp.push(get_second_peek(Vis_temp));

        //process the Stack orientation
        Collections.reverse(temp);

        P_rec = (Stack<Point>) temp.clone();

        // add initial starting points and endings
        B_vis.peek().setLocalBeta(beta);
        Vis_rec.push(get_second_peek(Vis_temp).getPredecessor());
        Vis_rec.push(get_second_peek(Vis_temp));

        //call vis algorithm
        algorithm_default(temp, Vis_rec, q);

        //remove already contained points
        P_rec.remove(get_second_peek(Vis_temp));
        Vis_rec.remove(get_second_peek(Vis_temp));
        Vis_rec.remove(get_second_peek(Vis_temp).getPredecessor());

        //process the Stack Orientations
        Collections.reverse(Vis_rec);


        //cycle polygon with new query Point q
        polygonCycle(P_rec, Vis_rec, beta, q);

        //delete Vis_temp pop as artefact, it is already included
        Vis_temp.pop();

        //push predecessor of innerturn
        //as algorithm default is set that it already included visible points as starting points
        //predecessor is one of the starting points
        B_vis.push(Vis_temp.peek().getPredecessor());
    }


    // ----------------------------------------------------------------------------------------------------------------
    // geometry helper
    // ----------------------------------------------------------------------------------------------------------------


    //rotates predecessing point clockwise with c as origin
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

    //rotates predecessing point counterclockwise with c as origin
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


    //simulates a ray from q to c as point
    private Point simulateRayEndPoint(Point q, Point c) {

        double m_x;
        double m_y;

        //y = mx +b;
        m_x = (c.getCenterX() - q.getCenterX()) * 0.5;
        m_y = (c.getCenterY() - q.getCenterY()) * 0.5;

        double x = c.getCenterX() + m_x;
        double y = c.getCenterY() + m_y;

        return createNode(x, y);
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Structure -, Variable Handling
    // ----------------------------------------------------------------------------------------------------------------


    //for bugfix purposes
    public void printStackorder(Stack<Point> Stack) {

        Stack<Point> temp = (java.util.Stack<Point>) Stack.clone();

        int size = Stack.size();
        for (int i = 0; i < size; i++) {
            System.out.println("number: " + i + temp.pop());
        }
    }

    //for bugfix purposes
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