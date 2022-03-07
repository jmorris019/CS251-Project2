import java.util.*;
import java.io.*;



/**
 * RedBlackBST class
 *
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;
    Node root;     // root of the BST

    /*************************************************************************
     *  Node Class and methods - DO NOT MODIFY
     *************************************************************************/
    public class Node {
        Key key;           // key
        Value val;         // associated data
        Node left, right;  // links to left and right subtrees
        boolean color;     // color of parent link
        int N;             // subtree count

        public Node(Key key, Value val, boolean color, int N) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.N = N;
        }
    }

    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return (x.color == RED);
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) return 0;
        return x.N;
    }

    // return number of key-value pairs in this symbol table
    public int size() { return size(root); }

    // is this symbol table empty?
    public boolean isEmpty() {
        return root == null;
    }

    public RedBlackBST() {
        this.root = null;
    }


    /*************************************************************************
     *  Helper functions
     *************************************************************************/

    // helper classes created by me, James Morris

    // function to turn a Binary tree into a sorted InOrder ArrayList
    int makeInOrder(Node x, int position, ArrayList<Key> array) {
        if (x.left != null) {
            position = makeInOrder(x.left, position, array);
        }
        array.add(x.key);
        if (x.right != null) {
            position = makeInOrder(x.right, position, array);
        }
        return position;
    }
    // function to find the smallest child node associated
    // This was directly made to help node deletion from a RB tree
    Node findSmallest(Node x) {
        while (x.left != null) {
            x = x.left;
        }
        return x;
    }

    //two helper methods in order to help deletion of a right node from a RB tree
    // Takes the smallest child node and deletes and balances the resulting tree
    void deleteSmallest() {
        // make root red if black children, needed for balancing later
        if (!root.left.color && !root.right.color) {
            root.color = true;
        }
        root = deleteSmallest(root);

        if (!isEmpty()) {
            root.color = false;
        }

    }

    Node deleteSmallest(Node x) {
        // this should never occur
        if (x.left == null) {
            return null;
        }
        // check conditions to move left, do so if conditions are met
        if (!x.left.color && !x.left.left.color) {
            x = moveRedLeft(x);
        }
        x.left = deleteSmallest(x.left);
        // balance after recursion
        return balance(x);

    }
    /*************************************************************************
     *  Modification Functions
     *************************************************************************/

    // insert the key-value pair; overwrite the old value with the new value
    // if the key is already present

    public void insert(Key key, Value val) {
        // check to see if input is valid
        if ((key == null) || (val == null)) {
            System.out.println("Either key or value invalid");
            return;
        }

        root = insert(key, val, root);
        // make the root of the tree black if red
        root = balance(root);
        root.color = false;
    }

    public Node insert(Key key, Value val, Node x) {
        //compare current node to the key
        if (x == null) {
            return new Node(key, val, true, 1);
        }
        int comp = key.compareTo(x.key);
        if (comp < 0) {
            x.left = insert(key, val, x.left);
        } else if (comp > 0 ) {
            x.right = insert(key, val, x.right);
        } else  {
            x.val = val;
        }
        // balance the node after recursive call
        x = balance(x);

        return x;
    }

    // delete the key-value pair with the given key
    public void delete(Key key) {
        // check to see if given key is valid
        if (key == null) {
            System.out.println("Invalid Key!"); // key doesn't work
            return;
        }
        if (!contains(key)) {
            System.out.println("Doesn't contain key!");
            return;
        }

        root = delete(key, root);
        // make the root of the tree black if red
        if (root.color && !isEmpty()) {
            root.color = false;
        }
    }
    public Node delete(Key key, Node x) {
        int comp = x.key.compareTo(key);
        // first check to see if the key is to the left of the node, this is much easier
        if (comp > 0) {
            // check to see if any left children are red
            if (!isRed(x.left) && !isRed(x.left.left) && isRed(x)) {
                x = moveRedLeft(x);
            }
            // recursively run with the left child
            x.left = delete(key, x.left);
        }
        // Deleting a node and checking to the right of the node takes a bit more code
        else {
            // rotate if left child is left in order to run the helper function moveRedRight()
            if (isRed(x.left)) {
                x = rotateRight(x);
            }
            // if there is a match, it shouldn't have a right child because of the previous if statement
            if ((comp == 0) && (x.right == null)) {
                return null;
            }
            // moveRedRight if right child and rl child are black
            if (!isRed(x.right) && !isRed(x.right.left)) {
                x = moveRedRight(x);
            }
            // check to see if node matches desired node, if so delete
            if (comp == 0) {
                Node temp = findSmallest(x.right); // temp node so we don't lose data
                x.key = temp.key;
                x.val = temp.val;
                x.right = deleteSmallest(x.right);
            } else {
                x.right = delete(key, x.right);
            }
        }
        return balance(x);
    }

    /*************************************************************************
     *  Search Functions
     *************************************************************************/

    // value associated with the given key; null if no such key
    public Value search(Key key) {
        Node temp = root;
        while  (true) {
            int comp = temp.key.compareTo(key);
            if (comp == 0) {
                return temp.val;
            }
            if (comp > 0) {
                if (temp.left == null) {
                    return null;
                }
                temp = temp.left;
            } else {
                if (temp.right == null) {
                    return null;
                }
                temp = temp.right;
            }
        }
    }

    // is there a key-value pair with the given key?
    public boolean contains(Key key) {
        return (search(key) != null);
    }



    /*************************************************************************
     *  Utility functions
     *************************************************************************/

    // height of tree (1-node tree has height 0)
    public int height() { return height(root); }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /*************************************************************************
     *  Rank Methods
     *************************************************************************/



    // the key of rank k
    public Key getValByRank(int k) {
        if (isEmpty()) {
            return null;
        }
        if ((k < 0) || (k >= root.N)) { // check to make sure k is valid for the trees range
            return null;
        }
        ArrayList<Key> inOrder = new ArrayList<Key>();
        makeInOrder(root, 0, inOrder);
        return inOrder.get(k);
    }

    // number of keys less than key
    public int rank(Key key) {
        if (key == null || isEmpty()) { // check to make sure k isn't null;
            return -1;
        }
        ArrayList<Key> inOrder = new ArrayList<Key>();
        makeInOrder(root, 0, inOrder);
        int count = 0;
        for (int i = 0; i < inOrder.size(); i++) {
            if (key.compareTo(inOrder.get(i)) > 0) {
                count++;
            }
        }
        return count;
    }


    /***********************************************************************
     *  Range count and range search.
     ***********************************************************************/

    public List<Key> getElements(int a, int b){
        ArrayList<Key> inOrder = new ArrayList<>();
        if (isEmpty()) {
            return inOrder;
        }
        if (a > b || a < 0 || b >= root.N) {
            return inOrder;
        }

        makeInOrder(root, 0, inOrder);
        List<Key> keyChain = new ArrayList<Key>();
        while ((a) != (b+1)) {
            keyChain.add(inOrder.get(a));
            a++;
        }
        return keyChain;
    }

    /*************************************************************************
     *  red-black tree helper functions
     *************************************************************************/

    // make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
        }
        return h;
    }

    // restore red-black tree invariant
    private Node balance(Node h) {
        if (isRed(h.right))                      h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))     flipColors(h);

        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }





    /*************************************************************************
     *  The Main Function
     Use this for testing
     *************************************************************************/
    public static void main(String[] args) {

        String[] possibleInputs = {"insert.txt", "delete.txt", "getVal.txt", "rank.txt", "search.txt", "testAll.txt"};

        Scanner readerTest = null;
        try {
            int selection;
            Scanner input = new Scanner(System.in);
            while (true) {  // while loop to ensure valid input
                System.out.println("Available RedBlackBST Tests");
                System.out.println("1: insert");
                System.out.println("2: delete");
                System.out.println("3: get value");
                System.out.println("4: rank");
                System.out.println("5: search");
                System.out.println("6: test all");
                try {
                    selection = Integer.parseInt(input.nextLine());

                if (selection > 0 && selection < 7) {
                    break; // valid input
                }
                } catch (Exception e) {}
                System.out.println("Invalid Input");
            }
            String selectedFile = "src/" + possibleInputs[selection - 1];
            File f = new File(selectedFile);
            readerTest = new Scanner(f);
        } catch (IOException e) {
            System.out.println("Reading Oops");
        }

        RedBlackBST<Integer, Integer> test = new RedBlackBST<>();

        System.out.println(readerTest);
        while(readerTest.hasNextLine()){
            String[] input  =readerTest.nextLine().split(" ");

            for(String x: input){
                System.out.print(x+" ");
            }

            System.out.println();
            switch (input[0]){
                case "insert":
                    Integer key = Integer.parseInt(input[1]);
                    Integer val = Integer.parseInt(input[2]);
                    test.insert(key,val);
                    printTree(test.root);
                    System.out.println();
                    break;

                case "delete":
                    Integer key1 = Integer.parseInt(input[1]);
                    test.delete(key1);
                    printTree(test.root);
                    System.out.println();
                    break;

                case "search":
                    Integer key2 = Integer.parseInt(input[1]);
                    Integer ans2 = test.search(key2);
                    System.out.println(ans2);
                    System.out.println();
                    break;

                case "getval":
                    Integer key3 = Integer.parseInt(input[1]);
                    Integer ans21 = test.getValByRank(key3);
                    System.out.println(ans21);
                    System.out.println();
                    break;

                case "rank":
                    Integer key4 = Integer.parseInt(input[1]);
                    Object ans22 = test.rank(key4);
                    System.out.println(ans22);
                    System.out.println();
                    break;

                case "getelement":
                    Integer low = Integer.parseInt(input[1]);
                    Integer high = Integer.parseInt(input[2]);
                    List<Integer> testList = test.getElements(low,high);

                    for(Integer list : testList){
                        System.out.println(list);
                    }

                    break;

                default:
                    System.out.println("Error, Invalid test instruction! "+input[0]);
            }
        }
    }


    /*************************************************************************
     *  Prints the tree
     *************************************************************************/
    public static void printTree(RedBlackBST.Node node){

        if (node == null){
            return;
        }

        printTree(node.left);
        System.out.print(((node.color == true)? "Color: Red; ":"Color: Black; ") + "Key: " + node.key + "\n");
        printTree(node.right);

    }
}
