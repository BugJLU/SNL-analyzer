package org.bugjlu.snlcompiler.syntax;

import java.util.ArrayList;

public class TreeNode {

    Object data;

    ArrayList<TreeNode> sonNodes;

//    int matchTime = 0;

    public TreeNode() {
        sonNodes = new ArrayList<>();
//        matchTime = 0;
    }

    void addSon(TreeNode node) {
        sonNodes.add(node);
//        matchTime += node.matchTime;
    }

    public ArrayList<TreeNode> getSonNodes() {
        return sonNodes;
    }
}
