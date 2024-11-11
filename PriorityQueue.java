/******************************************************************
 *
 *   YOUR NAME / SECTION NUMBER
 * Ethan Grassia / Section 001
 *
 *   Note, additional comments provided throughout this source code
 *   is for educational purposes
 *
 ********************************************************************/

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class PriorityQueue<E,P>
 *
 * The class implements a priority queue utilizing a min heap. The underlying 
 * data structure that implements the heap is an array of type 'Node'. Each 
 * element's value and priority data types are defined using a class generic 
 * interface. 
 */

class PriorityQueue<E, P> {

    private static final int DEFAULT_CAPACITY = 10; // initial queue size
  
    final Comparator<P> comparator;
    final ArrayList<Node> tree;       // The Heap is stored in an array as a tree
                                      // with the root at index 0 (not 1)

    /*
     * Constructors
     */

    public PriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public PriorityQueue(int capacity) {
        this(capacity, (a, b) -> ((Comparable<P>) a).compareTo(b));
    }

    public PriorityQueue(int capacity, Comparator<P> comparator) {
        tree = new ArrayList<>(capacity);
        this.comparator = comparator;
    }

    /*
     * Miscellaneous Methods
     */

    public int size()               { return tree.size(); }
    public boolean isEmpty()        { return tree.size() == 0; }
    public void clear()             { tree.clear(); }
    public Node offer(E e, P p)     { return add(e, p); }

    /**
     * Public Method add(E,P)
     *
     * Inserts the specified element into min heap as the right most leaf on the
     * lowest level of the tree. It then will pull up the inserted element towards
     * the root until it is in its correct location on the heap.
     *
     * @param: E e          - Element to add to queue
     * @param: P priority   - The priority for the newly added element
     * @return: Node        - Returns an object of type 'Node' representing the 
     *                        newly inserted element
     */
    public Node add(E e, P priority) {
        Node newNode = new Node(e, priority, tree.size());
        tree.add(newNode);
        pullUp(newNode.idx);
        return newNode;
    }

    /**
     * Public Method contains(E)
     *
     * Returns true if this queue contains the specified element. More formally,
     * returns true if and only if this queue contains at least one element of
     * type 'e' such that o.equals(e).
     *
     * @return: boolean - true if element in queue, else false.
     */
    public boolean contains(E e) {
        for (Node node : tree) {
            if (node.value.equals(e) && node.isValid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Public Method peek()
     *
     * Retrieves, but does not remove, the head of this queue, or returns null 
     * if this queue is empty.
     *
     * @return: Node    - An element of type Node is returned, else null 
     *                    if queue is empty.
     */
    public Node peek() {
        if (size() == 0) {
            return null;
        }
        return tree.get(0);
    }

    /**
     * Public Method remove()
     *
     * Retrieves and removes the head of this queue.
     *
     * @return: Node    - Returns an object of type 'Node' representing 
     *                    the removed element
     */
    public Node remove() {
        if (tree.size() == 0) {
            throw new IllegalStateException("PriorityQueue is empty");
        }
        return poll();
    }

    /**
     * Public Method poll()
     *
     * Retrieves and removes the head of this queue, or returns null if this 
     * queue is empty.
     *
     * @return: Node  - Returns an object of type 'Node' representing 
     *                  the removed element
     */
    public Node poll() {
        if (tree.size() == 0)
            return null;

        if (tree.size() == 1) {
            final Node removedNode = tree.remove(0);
            removedNode.markRemoved();
            return removedNode;
        } else {
            Node head = tree.get(0);
            head.markRemoved();
            final Node nodeToMoveToHead = tree.remove(tree.size() - 1);
            nodeToMoveToHead.idx = 0;
            tree.set(0, nodeToMoveToHead);
            pushDown(0);
            return head;
        }
    }

    // Additional helper and internal methods remain unchanged
    int leftChild(int i)            { return 2 * i + 1; }
    int rightChild(int i)           { return 2 * i + 2; }
    int parent(int i)               { return (i - 1) / 2; }
    private int compare(P a, P b)   { return comparator.compare(a, b); }

    void swap(int idx1, int idx2) {
        Node node1 = tree.get(idx1);
        Node node2 = tree.get(idx2);

        node1.idx = idx2;
        node2.idx = idx1;

        tree.set(idx1, node2);
        tree.set(idx2, node1);
    }

    private void pushDown(int i) {
        while (leftChild(i) < size() && compare(tree.get(leftChild(i)).priority, tree.get(i).priority) < 0 ||
                rightChild(i) < size() && compare(tree.get(rightChild(i)).priority, tree.get(i).priority) < 0) {
            int leftChildIdx = leftChild(i);
            int rightChildIdx = rightChild(i);
            if (rightChildIdx >= size() || compare(tree.get(leftChildIdx).priority, tree.get(rightChildIdx).priority) < 0) {
                swap(i, leftChildIdx);
                i = leftChildIdx;
            } else {
                swap(i, rightChildIdx);
                i = rightChildIdx;
            }
        }
    }

    private void pullUp(int i) {
        while (i != 0 && compare(tree.get(parent(i)).priority, tree.get(i).priority) > 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Class Node
     */
    public class Node {
        public Node(E value, P priority, int idx) {
            this.value = value;
            this.priority = priority;
            this.idx = idx;
        }

        E value;
        P priority;
        int idx;

        boolean removed = false;

        void markRemoved()          { removed = true; }
        public E value()            { return value; }
        public P priority()         { return priority; }
        public boolean isValid()    { return !removed; }

        public void changePriority(P newPriority) {
            checkNodeValidity();
            if (compare(newPriority, priority) < 0) {
                priority = newPriority;
                pullUp(idx);
            } else if (compare(newPriority, priority) > 0) {
                priority = newPriority;
                pushDown(idx);
            }
        }

        private void checkNodeValidity() {
            if (removed) {
                throw new IllegalStateException("node is no longer part of heap");
            }
        }

        public void remove() {
            checkNodeValidity();

            if (tree.size() == 1) {
                tree.remove(idx);
                markRemoved();
            }
            if (idx == tree.size() - 1) {
                markRemoved();
                tree.remove(idx);
            } else {
                markRemoved();
                final Node nodeToMoveToThisIdx = tree.remove(tree.size() - 1);
                nodeToMoveToThisIdx.idx = idx;
                tree.set(idx, nodeToMoveToThisIdx);
                if (compare(tree.get(parent(idx)).priority, nodeToMoveToThisIdx.priority) > 0) {
                    pullUp(idx);
                } else {
                    pushDown(idx);
                }
            }
        }
    }
}
