package median.treeVisualizer;

import base.input.Input;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.MouseButton;
import panAndZoom.PanAndZoom;

public abstract class TreeVisualizer<T> {

    private TreeVisualizerNode<T> root;

    private TreeVisualizerNode.Drawable<T> drawable;

    // Fields to select the nodes

    private TreeVisualizerNode<T> selectedNode = null;

    private final float minDist = 10f;

    // Intrinsic fields for draw the tree

    private final Vec2df treeOffset = new Vec2df(100, 150);

    // Constructor

    public TreeVisualizer(Vec2df ori, T root, TreeVisualizerNode.Drawable<T> drawable) {
        this.drawable = drawable;
        this.root = generateTree(ori, root);
    }

    // Methods

    private TreeVisualizerNode<T> generateTree(Vec2df pos, T root) {
        int numChildren = getNumChildren(root);

        TreeVisualizerNode<T> node = new TreeVisualizerNode<>(pos, root);
        node.setDrawMethod(drawable);

        if (numChildren != 0) {
            Vec2df childPos = new Vec2df(pos);
            childPos.addToY(treeOffset.getY() / 2f);
            for (int i = 0; i < numChildren; i++) {
                T child = getChild(root, i);
                if (child != null) {
                    TreeVisualizerNode<T> childNode = generateTree(childPos, child);
                    node.getChildren().add(childNode);
                    childPos.addToX(treeOffset.getX());
                    childPos.addToY(treeOffset.getY() / 2f);
                }
            }
        }

        return node;
    }

    public void recalculate(Vec2df ori, T root) {
        this.root = generateTree(ori, root);
    }

    public void manageUserInput(Input input, MouseButton ms, Vec2df mouse) {
        if (input.isButtonDown(ms)) {
            if (selectedNode == null) {
                selectedNode = root.getNode(mouse, minDist);
                selectedNode.getPos().set(mouse);
            }
        }

        if (input.isButtonHeld(ms)) {
            if (selectedNode != null) {
                selectedNode.getPos().set(mouse);
            }
        }

        if (input.isButtonUp(ms)) {
            if (selectedNode != null) {
                selectedNode = null;
            }
        }
    }

    public void draw(TreeVisualizerNode<T> node, PanAndZoom pz) {

        Vec2df ori = node.getPos();
        pz.strokeOval(ori, new Vec2df(minDist));
        node.draw(pz);

        if (node.hasChildren()) {
            for (var child : node.getChildren()) {
                Vec2df end = child.getPos();

                pz.strokeLine(ori, end);

                draw(child, pz);
            }
        }
    }

    public void draw(PanAndZoom pz) {
        draw(root, pz);
    }

    // Abstract methods

    public abstract int getNumChildren(T node);

    public abstract T getChild(T node, int childIndex);

    // Getters & Setters

    public TreeVisualizerNode.Drawable<T> getDrawable() {
        return drawable;
    }

    public void setDrawable(TreeVisualizerNode.Drawable<T> drawable) {
        this.drawable = drawable;
    }

}
