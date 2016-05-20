mport edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class KdTreePointST<Value> implements PointST<Value> {
    private Node root; // root of the KdTree
    private int N;     // number of nodes in the KdTree

    // 2d-tree (generalization of a BST in 2d) representation.
    private class Node {
        private Point2D p;   // the point
        private Value val;   // the symbol table maps the point to this value
        private RectHV rect; // the axis-aligned rectangle corresponding to 
                             // this node
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        // Constructs a node given the point, the associated value, and the 
        // axis-aligned rectangle corresponding to the node.
        Node(Point2D p, Value val, RectHV rect) {
            this.p = p;
            this.val = val;
            this.rect = rect;
        }
    }

    // Constructs an empty symbol table of points.
    public KdTreePointST() {
    }

    // Returns true if the symbol table is empty, and false otherwise.
    public boolean isEmpty() { 
        return this.N == 0;
    }

    // Returns the number points in the symbol table.
    public int size() {
        return this.N;
    }

    // Associates the value val with point p.
    public void put(Point2D p, Value val) {
        if (p.equals(null) || val == null) 
            throw new NullPointerException("...");
        double pInf = Double.POSITIVE_INFINITY;
        double nInf = -1 * pInf;
        if (this.isEmpty()) {
            root = new Node(p, val, new RectHV(nInf, nInf, pInf, pInf));
            N++;
            return;
        }
        put(root, p, val, new RectHV(nInf, nInf, pInf, pInf), true);    
    }

    // Helper for put(Point2D p, Value val).
    private Node put(Node x, Point2D p, Value val, RectHV rect, boolean lr) {
        if (x == null) {
            N++;
            return new Node(p, val, rect);
        }
        if (lr) {
            double cmp = p.x() - x.p.x();
            if      (cmp < 0.0) x.lb = put(x.lb, p, val, 
                                           new RectHV(rect.xmin(), rect.ymin(), 
                                           x.p.x(), rect.ymax()), false);
            else if (cmp > 0.0) x.rt = put(x.rt, p, val, 
                                           new RectHV(x.p.x(), rect.ymin(), 
                                           rect.xmax(), rect.ymax()), false);
            else                x.val = val;
            
        }   else {            
            double cmp = p.y() - x.p.y();
            if      (cmp < 0.0) x.lb = put(x.lb, p, val, 
                                           new RectHV(rect.xmin(), rect.ymin(), 
                                           rect.xmax(), x.p.y()), true);
            else if (cmp > 0.0) x.rt = put(x.rt, p, val, 
                                           new RectHV(rect.xmin(), x.p.y(), 
                                           rect.xmax(), rect.ymax()), true);
            else                x.val = val;
        }
        return x;
    }

    // Returns the value associated with point p.
    public Value get(Point2D p) {
        if (p.equals(null)) throw new NullPointerException("...");
        return get(root, p, true);
    }

    // Helper for get(Point2D p).
    private Value get(Node x, Point2D p, boolean lr) {
        if (x == null) return null;
        if (lr) {
            double cmp = p.x() - x.p.x();
            if      (cmp < 0.0) return get(x.lb, p, false);
            else if (cmp > 0.0) return get(x.rt, p, false);
            else                return x.val;
        }   else    {
            double cmp = p.y() - x.p.y();
            if      (cmp < 0.0) return get(x.lb, p, true);
            else if (cmp > 0.0) return get(x.rt, p, true);
            else                return x.val;
        } 
    }

    // Returns true if the symbol table contains the point p, and false 
    // otherwise.
    public boolean contains(Point2D p) {
        return this.get(p) != null;
    }

    // Returns all points in the symbol table, in level order.
    public Iterable<Point2D> points() {
        Queue<Point2D> keys = new Queue<Point2D>();
        Queue<Node> queue = new Queue<Node>();
        queue.enqueue(root);
            while (!queue.isEmpty()) {
                Node x = queue.dequeue();
                if (x == null) continue;
                keys.enqueue(x.p);
                queue.enqueue(x.lb);
                queue.enqueue(x.rt);
            }
        return keys;
    }

    // Returns all points in the symbol table that are inside the rectangle 
    // rect.
    public Iterable<Point2D> range(RectHV rect) {
        if (rect.equals(null)) throw new NullPointerException("...");
        Queue<Point2D> queue = new Queue<Point2D>();
        range(root, rect, queue);
        return queue;
    }

    // Helper for public range(RectHV rect).
    private void range(Node x, RectHV rect, Queue<Point2D> q) {    
        if (x == null || !x.rect.intersects(rect)) return;
        if (x.p.x() > rect.xmin() && x.p.x() < rect.xmax() 
        &&  x.p.y() > rect.ymin() && x.p.y() < rect.ymax())
            q.enqueue(x.p);
        range(x.lb, rect, q);
        range(x.rt, rect, q);
    }

    // Returns a nearest neighbor to point p; null if the symbol table is empty.
    public Point2D nearest(Point2D p) {
        if (p.equals(null)) throw new NullPointerException("...");
        return nearest(root, p, null, Double.POSITIVE_INFINITY, true);
    }
    
    // Helper for public nearest(Point2D p).
    private Point2D nearest(Node x, Point2D p, Point2D nearest, 
                            double nearestDistance, boolean lr) {
        if (x == null) return nearest;
        if (nearestDistance < x.rect.distanceSquaredTo(p)) return nearest;    
        double closestDistance = nearestDistance;
        Point2D closest = nearest;
        if (!p.equals(x.p)
            && nearestDistance > x.p.distanceSquaredTo(p)) {
            closestDistance = x.p.distanceSquaredTo(p);
            closest = x.p;
        }   
        if (lr) {
            if (p.x() > x.p.x()) 
                return nearest(x.rt, p, closest, closestDistance, false);
            else 
                return nearest(x.lb, p, closest, closestDistance, false);
        }   else {
            if (p.y() > x.p.y()) 
                return nearest(x.rt, p, closest, closestDistance, true);
            else 
                return nearest(x.lb, p, closest, closestDistance, true);
        }
    }

    // Returns k points that are closest to point p.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p.equals(null)) throw new NullPointerException("...");
        MaxPQ<Point2D> maxPQ = new MaxPQ<Point2D>(p.distanceToOrder());
        nearest(root, p, k, maxPQ, true);
        return maxPQ;
    }

    // Helper for public nearest(Point2D p, int k).
    private void nearest(Node x, Point2D p, int k, 
                         MaxPQ<Point2D> pq, boolean lr) {
        if (x == null) return;
        if (pq.size() == k
        && pq.max().distanceSquaredTo(p) < x.rect.distanceSquaredTo(p)) 
            return;
        if (!p.equals(x.p)) {
            pq.insert(x.p);
            if (pq.size() > k)
                pq.delMax();
        }
        if (lr) {
            if (p.x() > x.p.x()) {
                nearest(x.rt, p, k, pq, false);
                nearest(x.lb, p, k, pq, false);
            }   else {
                nearest(x.lb, p, k, pq, false);
                nearest(x.rt, p, k, pq, false);
            }
        }   else {
            if (p.y() > x.p.y()) {
                nearest(x.rt, p, k, pq, true);
                nearest(x.lb, p, k, pq, true);
            }   else {
                nearest(x.lb, p, k, pq, true);
                nearest(x.rt, p, k, pq, true);
            }
        }                          
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        KdTreePointST<Integer> st = new KdTreePointST<Integer>();
        Point2D query = new Point2D(0.661633, 0.287141);
        Point2D origin = new Point2D(0, 0);
        int i = 0;
        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            Point2D p = new Point2D(x, y);
            st.put(p, i++);
        }
        StdOut.println("st.empty()? " + st.isEmpty());
        StdOut.println("st.size() = " + st.size());
        StdOut.println("First five values:");
        i = 0;
        for (Point2D p : st.points()) {
            StdOut.println("  " + st.get(p));
            if (i++ == 5) {
                break;
            }
        }
        StdOut.println("st.contains(" + query + ")? " + st.contains(query));
        StdOut.println("st.contains(" + origin + ")? " + st.contains(origin)); 
        StdOut.println("st.range([0.65, 0.68]x[0.28, 0.29]):");
        for (Point2D p : st.range(new RectHV(0.65, 0.28, 0.68, 0.29))) {
            StdOut.println("  " + p);
        }
        StdOut.println("st.nearest(" + query + ") = " + st.nearest(query));
        StdOut.println("st.nearest(" + query + "):"); 
        for (Point2D p : st.nearest(query, 7)) {
            StdOut.println("  " + p);
        }
    }
}
