import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class BrutePointST<Value> implements PointST<Value> {
    private RedBlackBST<Point2D, Value> bst; // the symbol table represented 
                                             // as a red-black BST.
    // Constructs an empty symbol table of points.
    public BrutePointST() {
        bst = new RedBlackBST<Point2D, Value>();
    }

    // Returns true if the symbol table is empty, and false otherwise.
    public boolean isEmpty() { 
        return this.size() == 0;
    }

    // Returns the number points in the symbol table.
    public int size() {
        return this.bst.size();
    }

    // Associates the value val with point p.
    public void put(Point2D p, Value val) {
        if (p.equals(null) || val == null) 
            throw new NullPointerException("put");
        this.bst.put(p, val);
    }

    // Returns the value associated with point p.
    public Value get(Point2D p) {
        if (p.equals(null)) throw new NullPointerException("get");
        return this.bst.get(p);
    }

    // Returns true if the symbol table contains the point p, and false 
    // otherwise.
    public boolean contains(Point2D p) {
        if (p.equals(null)) throw new NullPointerException("contains");
        return this.get(p) != null;
    }

    // Returns all points in the symbol table.
    public Iterable<Point2D> points() {
        return this.bst.keys(bst.min(), bst.max());
    }

    // Returns all points in the symbol table that are inside the rectangle 
    // rect.
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> queue = new Queue<Point2D>();
        for (Point2D p : this.points()) {
            if (p.x() > rect.xmin() && p.x() < rect.xmax() 
            && p.y() > rect.ymin() && p.y() < rect.ymax())
                queue.enqueue(p);
        }
        return queue;  
    }

    // Returns a nearest neighbor to point p; null if the symbol table is empty.
    public Point2D nearest(Point2D p) {
       if (p.equals(null)) throw new NullPointerException("...");    
       Point2D result = null;
       for (Point2D p2 : this.nearest(p, 1))    
            result = p2;  
       return result;
    }

    // Returns k poiqnts that are closest to point p.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p.equals(null)) throw new NullPointerException("...");
        MinPQ<Point2D> minpq = new MinPQ<Point2D>(p.distanceToOrder());
        Queue<Point2D> queue = new Queue<Point2D>();
        int queueSize = 0;
        for (Point2D p2 : this.points()) 
            minpq.insert(p2);
        while (queueSize < k) {
            Point2D temp =  minpq.delMin();
            if (!p.equals(temp)) {          //to avoid adding p to return
                queue.enqueue(temp);
                ++queueSize;
            }
        }
        return queue;
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        BrutePointST<Integer> st = new BrutePointST<Integer>();
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

