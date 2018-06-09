package org.bugjlu.snlcompiler.ui;

import org.bugjlu.snlcompiler.syntax.TreeNode;

import java.util.ArrayList;

public class TreeWriter {
    ArrayList<Integer> leftSons;

    TreeWriter() {
        leftSons = new ArrayList<>();
        leftSons.add(-1);
    }

    void dealNode(TreeNode node) {
        for (int i = 0; i < leftSons.size()-1; i++) {
            if (leftSons.get(i) > 0) {
                System.out.print("│   ");
            } else if (leftSons.get(i) == 0){
                System.out.print("    ");
            } else {
                System.out.print(" ");
            }
        }
        if (leftSons.get(leftSons.size()-1) > 0) {
            System.out.print("├");
        } else if (leftSons.get(leftSons.size()-1) == 0){
            System.out.print("└");
        }
        System.out.println((leftSons.get(leftSons.size()-1)<0?"":"──")+node.getData().toString());
        ArrayList<TreeNode> sons = node.getSonNodes();
        leftSons.add(sons.size());
        int idx = leftSons.size()-1;

        for (TreeNode n:
                sons) {
            leftSons.set(idx, leftSons.get(idx)-1);
            dealNode(n);
        }
        leftSons.remove(idx);
    }

    public static void writeTree(TreeNode root) {
        TreeWriter treeWriter = new TreeWriter();
        treeWriter.dealNode(root);
        System.out.println();
    }
}
