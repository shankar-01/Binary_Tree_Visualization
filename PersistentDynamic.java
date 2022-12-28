import javax.swing.*;
import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @param <E>
 * Persistent dynamic set class
 * maintains old versions of bst after insertion and deletion
 */
public class PersistentDynamic <E extends Comparable> implements VisitEventListener{
    private BinarySearchTree<E> tree = null; //current bst
    private ArrayList<BinarySearchTree<E>> trees = null; //list of versions of bst
    private BinarySearchTree.Node lastNode = null; //copy last node that is fired
    private JTabbedPane tabbedPane = new JTabbedPane(); //tabbed pane that display all version of bst sequence wise

    /**
     *
     * @param tree
     * construct that take bst tree already created
     */
    public PersistentDynamic(BinarySearchTree tree){
        this.tree = tree;
        trees = new ArrayList<BinarySearchTree<E>>();
        tree.addVisitListener(this);
    }

    /**
     * default constructor with empty bst
     *
     */
    public PersistentDynamic() {
        tree = new BinarySearchTree<E>();
        trees = new ArrayList<BinarySearchTree<E>>();
        tree.addVisitListener(this);
    }

    /**
     *
     * @param data
     *
     * add data in bst
     */
    public void add(E data){
        lastNode = null; //make last node null, because new is going to be created
        tree.add(data); //add data in bst
        if (trees.size()>=1){ //if any bst is created add it into tabbed pane
            BinarySearchTree t = trees.get(trees.size()-1);
            tabbedPane.addTab("Add " +data , t.getTreePanel());
        }
        lastNode = null; //make last null
    }

    /**
     *
     * @param data
     * remove data from bst
     */
    public void remove(E data){
        lastNode = null; //make last node null, because new is going to be created
        tree.remove(data); //remove data from bst
        if (trees.size()>=1){ //if any bst is created add it into tabbed pane
            //BinarySearchTree t = trees.get(trees.size()-1);
            tabbedPane.addTab("Remove " +data , tree.getTreePanel());
        }

        lastNode = null;//make last null
    }

    /**
     *
     * @param e
     *
     *
     */
    @Override
    public void Visited(EventObject e) {
        BinarySearchTree.Node actualNode = ((BinarySearchTree.Node) e.getSource()); //actual visited node
        BinarySearchTree.Node cloneNode = null;
        try {
            cloneNode = (BinarySearchTree.Node)actualNode.clone(); // make clone of actual node
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            cloneNotSupportedException.printStackTrace();
        }
        if(lastNode == null) { //if last node is null create new bst version and update last node
            lastNode = cloneNode;
            BinarySearchTree<E> tree1 = new BinarySearchTree<E>(lastNode);
            trees.add(tree1);
        }
        else{ //else place clone of actual node at its correct position
            //if actual node is left child of last node then attach its clone to left of last node and update last node
            if(lastNode.left == actualNode || (lastNode.left == null && (lastNode.data).compareTo(actualNode.data) > 0)){
                lastNode.left = cloneNode;
                lastNode = cloneNode;
            }
            //if actual node is right child of last node then attach its clone to right of last node and update last node
            else if(lastNode.right == actualNode || (lastNode.right == null && (lastNode.data).compareTo(actualNode.data) < 0)){
                lastNode.right = cloneNode;
                lastNode = cloneNode;
            }
        }
    }

    /**
     * create frame with trees and display it
     */
    public void display(){
        JFrame frame = new JFrame("Persistence Dynamic");
        frame.add(tabbedPane); //add tabbed pane that contains trees visualization
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
