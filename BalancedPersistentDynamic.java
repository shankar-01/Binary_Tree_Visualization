import javax.swing.*;
import java.util.*;

/**
 * BalancedPersistentDynamic it is subclass of bst (basically it is red black tree)
 */
public class BalancedPersistentDynamic extends BinarySearchTree implements VisitEventListener{
    private JTabbedPane jTabbedPane = new JTabbedPane();//tabbed pane that display all version of trees sequence wise
    private HashMap<Node, Integer> nodeColors = new HashMap<Node, Integer>(); //maintain node colors
    private Node lastNode = null; //copy of last visited node
    private ArrayList<BinarySearchTree> trees = new ArrayList<BinarySearchTree>(); //old version of tree
    private Vector<Node> visited = null; //list of actual visited nodes it will be helpful in insertion and deletion

    /**
     * default constructor that add listener
     */
    public BalancedPersistentDynamic() {
        addVisitListener(this);

    }

    /**
     *
     * @param data
     *
     * add node in tree and perform rotation and recoloring
     */
    public void add(Comparable data){
        visited = new Vector<Node>(); //refresh list of visited nodes
        super.add(data); //add data in tree just like bst
        nodeColors.put(visited.get(visited.size()-1), 1); //last node in visited list will be new added node so make its color red

        jTabbedPane.add("Add " + data, trees.get(trees.size()-1).getTreePanel()); // add version tree (without balancing) in tabbed pane

        balance(visited.get(visited.size()-1), visited.size()-1); //balance tree
        visited = null;//make visited null
        lastNode = null; //make last node null
        jTabbedPane.add("balance", getTreePanel()); //add tree (with balance) in tabbed pane

    }

    /**
     *
     * @param data
     * remove node from tree and perform rotation and recoloring (remove double black)
     */
    public void remove(Comparable data){
        visited = new Vector<>();//refresh list of visited nodes
        super.remove(data); //remove data from tree just like bst
        jTabbedPane.addTab("Remove " + data , getTreePanel());// add version tree (without balancing) in tabbed pane
        Node del = visited.get(visited.size()-1); //deleted node
        Node rep = null; // replacer node
        //find rep node
        if(del.left == null) // if del left is null mean it is replaced by right child
            rep = del.right;
        else //else it is replaced by its left child
            rep = del.left;

        if(rep != null && nodeColors.get(del) != nodeColors.get(rep)){ // if atleast one is red either rep or del
            nodeColors.put(rep, 0); //make replacer black node
        }
        else if((rep == null || nodeColors.get(rep) == 0) && nodeColors.get(del) == 0){ //else if both are black
            //rep is double black
            removeDoubleBlack(rep, visited.size()-1);//remove double black because it is violating red black tree property
        }
        jTabbedPane.addTab("Balance ", getTreePanel()); //add tree (with balance) in tabbed pane
        visited = null; //make visited null
        lastNode = null;//make last node null
    }
    private void removeDoubleBlack(Node db, int index){
        //if db is not root then solve problem else return
        if (db != getRoot()) { //db is not root
            Node parent = visited.get(index-1); //parent node
            Node sibling = getSibling(parent, db); //sibling of rep node
            if(sibling != null && nodeColors.get(sibling) == 0) { // if sibling is black
                boolean isLeftRed = (sibling.left != null && nodeColors.get(sibling.left) == 1);
                boolean isRightRed = (sibling.right != null && nodeColors.get(sibling.right) == 1);
                if (isLeftRed || isRightRed) {//if any child of sibling is red
                    Node redChild = null;
                    //find red child of sibling
                    if (isLeftRed) redChild = sibling.left;
                    else redChild = sibling.right;
                    Node n = null;
                    //perform rotation and recolor it
                    if(parent.right == sibling){
                        if(sibling.left == redChild) { //R-L rotation
                            swapColor(sibling, redChild);
                            parent.right = rotateRight(sibling);
                            swapColor(parent, parent.right);
                            n = rotateLeft(parent);
                            nodeColors.put(sibling, 0);
                        }
                        else { //R-R rotation
                            swapColor(parent, sibling);
                            n = rotateLeft(parent);
                            nodeColors.put(redChild, 0);

                        }
                    }
                    else{
                        if(sibling.right == redChild) { //L-R rotation
                            swapColor(sibling, redChild);
                            parent.left = rotateLeft(sibling);
                            n = rotateRight(parent);
                            nodeColors.put(sibling, 0);
                        }
                        else { //L-L rotation
                            swapColor(parent, sibling);
                            n = rotateRight(parent);
                            nodeColors.put(redChild, 0);
                        }
                    }
                    if(index-2 <0){ //if we get new root after rotation so update root
                        setRoot(n);
                    }
                    else{ //else update hierarchy
                        Node grandParent = visited.get(index-2);
                        if (grandParent.left == parent){
                            grandParent.left = n;
                        }
                        else{
                            grandParent.right = n;
                        }
                    }
                }
                else{ //if both child of sibling is black
                    nodeColors.put(sibling, 1); //color sibling red
                    //color parent black
                    if(nodeColors.get(parent) == 1)
                        nodeColors.put(parent, 0);
                    else
                        removeDoubleBlack(parent, index-1); //if parent was alreday black
                }
            }
            else if(sibling != null && nodeColors.get(sibling) == 1){ // if sibling is red
                swapColor(parent, sibling); //swap parent and sibling color
                Node n = null;
                //perform suitable rotation on parent
                if(parent.left == sibling){
                    n = rotateRight(parent);
                }
                else{
                    n = rotateLeft(parent);
                }
                //if we get new root update it
                if (index-2 <0){
                    setRoot(n);
                }
                else { //else update tree hierarchy
                    Node grandParent = visited.get(index-2);
                    if (grandParent.left == parent)
                        grandParent.left = n;
                    else
                        grandParent.right = n;
                }
                visited.add(index-1, n); //add new parent in visited list
                removeDoubleBlack(db, index+1); //and again apply cases on double black node
            }
        }
    }

    /**
     *
     * @param node1
     * @param node2
     *
     * swap colors of two nodes
     */
    private void swapColor(Node node1, Node node2){
        int color = nodeColors.get(node1);
        nodeColors.put(node1, nodeColors.get(node2));
        nodeColors.put(node2, color);
    }

    /**
     *
     * @param node
     * @param index
     *
     * balance tree, perform rotation and recoloring
     */
    private void balance(Node node, int index){
        if(node == getRoot() || index == 0){ //if node is root
            nodeColors.put(node, 0); //make it black
            return;
        }

        Node parent = visited.get(index-1); //parent of node

        if(nodeColors.get(parent) != 0){ //if parent is red

            Node grandParent = visited.get(index-2);
            Node uncle = getSibling(grandParent, parent);

            if(uncle != null && nodeColors.get(uncle) == 1){ //if uncle node is red
                nodeColors.put(parent, 0); //color parent black
                nodeColors.put(uncle, 0); //color uncle black
                nodeColors.put(grandParent, 1); //color grand parent red
                balance(grandParent, index-2); //rebalance grand parent
            }
            else{ //if uncle is black
                Node n = null;
                //perform suitable rotation
                if(grandParent.right == parent){
                    if(parent.left == node) { //R-L
                        grandParent.right = rotateRight(parent);
                        n = rotateLeft(grandParent);
                        nodeColors.put(n, 0);
                        nodeColors.put(n.left, 1);
                    }
                    else { //R-R
                        n = rotateLeft(grandParent);
                        nodeColors.put(n, 0);
                        nodeColors.put(n.left, 1);
                    }
                }
                else{
                    if(parent.right == node) { //L-R
                        grandParent.left = rotateLeft(parent);
                        n = rotateRight(grandParent);
                        nodeColors.put(n, 0);
                        nodeColors.put(n.right, 1);
                    }
                    else { //L-L
                        n = rotateRight(grandParent);
                        nodeColors.put(n, 0);
                        nodeColors.put(n.right, 1);
                    }
                }
                if(index-3 < 0) //after rotation if we get new root update it
                    setRoot(n);
                else { //else update hierarchy of tree
                    Node m = visited.get(index - 3);
                    if(m.right == grandParent)
                        m.right = n;
                    else if(m.left == grandParent)
                        m.left = n;
                }
            }
        }
    }

    /**
     *
     * @param node
     * @return
     *
     * rotate left
     */
    private Node rotateLeft(Node node){
        Node newNode = node.right;
        node.right = newNode.left;
        newNode.left = node;
        return newNode;
    }

    /**
     *
     * @param node
     * @return
     *
     * rotate right
     */
    private Node rotateRight(Node node){
        Node newNode = node.left;
        node.left = newNode.right;
        newNode.right = node;
        return newNode;
    }

    /**
     *
     * @param parent
     * @param node
     * @return
     *
     * get sibling of node
     */
    private Node getSibling(Node parent, Node node){
        if(parent.right == node){
            return parent.left;
        }
        return parent.right;
    }

    /**
     * create frame and add tabbed pane
     */
    @Override
    public void display() {
        JFrame frame = new JFrame("Balanced Persistent");
        frame.add(jTabbedPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     *
     * @param e
     * maintains visited nodes and old version of tree
     */
    @Override
    public void Visited(EventObject e) {
        Node actualNode = ((Node) e.getSource()); //actual node
        visited.add(actualNode); //add actual node to visited list

        Node cloneNode = null;
        try {
            cloneNode = (Node)actualNode.clone(); //clone of actual node
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            cloneNotSupportedException.printStackTrace();
        }
        if(lastNode == null) { //if last node is null make new tree version and update last node
            lastNode = cloneNode;
            BinarySearchTree tree1 = new BinarySearchTree(lastNode);
            trees.add(tree1);
        }
        else{
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
}