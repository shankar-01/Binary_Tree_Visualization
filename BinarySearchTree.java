/**
 * Binary search tree, and node class
 * BST data structure
 *
 */

import org.w3c.dom.events.EventListener;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class BinarySearchTree <E extends Comparable>{
    //node of bst
    class Node{
        E data; //data of a node
        Node right = null; //right child pointer
        Node left = null; //left child pointer

        //constructor which takes data
        Node(E data){
            this.data = data;
        }

        //toString method for node which return data as String
        @Override
        public String toString() {
            return data.toString();
        }

        /**
         *
         * @return
         * @throws CloneNotSupportedException
         *
         * clone method return clone of node (it will return new node with same right, and left child)
         * it will be very helpful in Persistent Dynamic set and Balanced Persistent Dynamic set
         *
         */

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Node node = new Node(data); // create new node with same data
            node.right = this.right; //assign same right child
            node.left = this.left;   //assign same left child
            return node;
        }
    }
    private Node root = null; //root node of bst
    private ArrayList<VisitEventListener> eventListeners = new ArrayList<VisitEventListener>(); //list of listeners

    //getter for root node
    public Node getRoot(){
        return root;
    }
    //default contructor bst which create empty tree (root = null)
    public BinarySearchTree() {
        root = null;
    }


    /**
     * constructor which takes node and it will make it root
     * it will be helpful in Persistent Dynamic set and Balanced Persistent Dynamic set
     */
    public BinarySearchTree(Node root) {
        this.root = root;
    }

    /**
     * setter for root node
     * it will be helpful in Persistent Dynamic set and Balanced Persistent Dynamic set
     * we will need this in Balanced Persistent Dynamic class during rotaion
     *
     * @param root
     */
    public void setRoot(Node root){
        this.root = root;
    }

    /**
     * add method it will insert node in bst at its correct place
     *
     * @param data
     */
    public void add(E data){
        root = add(data, root); //call to helper method
    }

    /**
     * @param data
     * @param root
     * @return
     *
     * helper method for adding node at its corrent place
     * it will throw event with a node which is being affected while insertion (call hook method)
     */
    private Node add(E data, Node root){
        if(root == null){ //if root is null create new node, return that node and also call hook method
            root = new Node(data);

            fireVisitedListener(new VisitEvent(root));
            return root;
        }
        fireVisitedListener(new VisitEvent(root)); //call hook method
        if((root.data).compareTo(data) > 0){ //if data is less than root go left side

            root.left = add(data, root.left);

        }
        else{ //if data is greater or equal to right side

            root.right = add(data, root.right);

        }

        return root;
    }

    /**
     *
     * @param data
     *
     * it will remove node with given data.
     */
    public void remove(E data){
        root = remove(data, root); //call helper method
    }

    /**
     *
     * @param data
     * @param root
     * @return
     * helper method for removing node
     * it will remove correct node and it will call hook method
     */
    private Node remove(E data, Node root){
        if(root == null){ //if root is null do nothing
            return null;
        }
        fireVisitedListener(new VisitEvent(root)); //call hook method
        if((root.data).compareTo(data) > 0){ //if data is less than root go to left side
            root.left = remove(data, root.left);
        }
        else if((root.data).compareTo(data) < 0){//if data is greater than root go to right side
            root.right = remove(data, root.right);
        }
        else { //if data is equal to root find replacement and remove that root
            if(root.left == null){ //if root has only right child, replace root with right
                return root.right;
            }
            else if(root.right == null){//if root has only left child, replace root with left
                return root.left;
            }

            root.data = min(root.right); //if root has two child find inorder successor and replace root with that. (inorder successor = minimum node in right subtree)
            root.right = remove(root.data, root.right); //remove that inorder successor
        }
        return root;
    }

    /**
     *
     * @param node
     * @return
     * it will return minimum node in subtree
     *
     */
    private E min(Node node){
        fireVisitedListener(new VisitEvent(node));
        if(node.left == null){ //if it is minimum means it did not have any left child, return node data
            return node.data;
        }
        return min(node.left); //if it has minimum element means it has left child, then find it and return it
    }

    /**
     *
     * @param data
     * @return
     * search node with given that, if you find that return true else return fasle
     */
    public boolean contains(E data){
        if (root == null) //if root is null return false
            return false;
        Node temp = root;
        while (temp != null){ //traverse through tree
            if(temp.data.compareTo(data) > 0){ //if current node is greater than data move to left
                temp = temp.left;
            }
            else if(temp.data.compareTo(data) <0){ //if current node is is less than data move to right
                temp = temp.right;
            }
            else //if current node and data is equal return true
                return true;
        }
        //if do not find data in tree return false
        return false;
    }

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //full screen size it will be used in tree visulization
    Dimension box = new Dimension(50, 20); //node (in gui) size

    /**
     * create frame and display tree (gui)
     */
    public void display(){
        JFrame frame = new JFrame("Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(dim);
        frame.add(getTreePanel()); //add panel of frame which contains tree picture
        frame.setVisible(true);
    }

    /**
     *
     * @return
     *
     * create panel with tree picture and return that panel
     */
    public JPanel getTreePanel(){
        JPanel panel = new JPanel();
        panel.setSize(dim);
        BufferedImage image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB); //create empty image
        Graphics graphics = image.getGraphics(); //get graphic of image
        JLabel label = new JLabel(new ImageIcon(image)); //add image to label
        panel.add(label);
        graphics.setColor(Color.BLACK);
        int x = (dim.width-box.width)/2;
        int y = 0;
        drawTree(root, graphics, x, y, dim.width, 0); //draw tree on image
        graphics.dispose();
        return panel;
    }

    /**
     *
     * @param node
     * @param g
     * @param x
     * @param y
     * @param rl
     * @param ll
     *
     * it will traverse (preorder) through tree and draw tree on image (whose graphics is given)
     */
    private void drawTree(Node node, Graphics g, int x, int y, int rl, int ll){
        g.drawRect(x, y, box.width, box.height); //draw rectangle at x and y position
        g.drawString(node.toString(), x+2, y+12); //draw string
        if(node.left != null){//if it has left child
            g.drawLine(x, y+box.height, (x + ll)/2, y+box.height+20); //draw line
            drawTree(node.left, g, (ll+x-box.width)/2, y+box.height+20, x,ll); //draw left child
        }
        if(node.right != null){//if it has right child
            g.drawLine(x+box.width, y+box.height, (rl + x + box.width)/2, y+box.height+20); //draw line
            drawTree(node.right, g, (rl + x)/2, y+box.height+20, rl, x+box.width); //draw right child
        }
    }
    //inorder traversal of bst
    public void inOrder(){
        inOrder(root);
        System.out.println();
    }
    //inorder traversal helper
    private void inOrder(Node node){
        if(node.left != null)
            inOrder(node.left);
        System.out.print(node.data+ "  ");
        if (node.right != null)
            inOrder(node.right);
    }
    //add listener
    public void addVisitListener(VisitEventListener listener){
        eventListeners.add(listener);
    }
    //remove listener
    public void removeVisitListener(VisitEventListener listener){
        eventListeners.remove(listener);
    }
    //fire event
    public void fireVisitedListener(VisitEvent e){
        Iterator<VisitEventListener> it = eventListeners.listIterator(); //iterate through list of listeners and fire event
        while (it.hasNext()){
            it.next().Visited(e);
        }
    }
}
