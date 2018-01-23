package com.thoreau.algorithm.tree.rbtree;

import java.util.LinkedList;

/**
 * 2018/1/15 下午10:58.
 *
 * @author zhaozhou
 */
public class RBTree<T extends Comparable<T>> {
    private RBTreeNode<T> root;

    RBTree(RBTreeNode<T> node) {
        this.root = node;
    }

    public RBTree() {

    }

    public RBTreeNode<T> getRoot() {
        return root;
    }

    public T addNode(T value) {
        RBTreeNode<T> t = new RBTreeNode<T>(value);
        return addNode(t);
    }

    // insert
    private T addNode(RBTreeNode<T> node) {
        node.setLeft(null);
        node.setRight(null);
        node.setRed(true); // 新插入的节点为红色

        if (root == null) {
            // 新插入节点为跟节点
            root = node;
            root.setRed(false);
            return root.getValue();
        }
        // 找到要插入的父节点
        RBTreeNode<T> parentNode = findParentNode(node);
        int result = parentNode.getValue().compareTo(node.getValue());
        if (result == 0) {
            return node.getValue();
        }
        if (result > 0) {
            parentNode.setLeft(node);
        } else {
            parentNode.setRight(node);
        }
        node.setParent(parentNode);
        if (parentNode.isRed()) {
            fixAdd(node);
        }
        return null;

    }

    /**
     * 插入修复
     *
     * @param node node
     */
    private void fixAdd(RBTreeNode<T> node) {
        // 找到parent和uncle
        RBTreeNode<T> parent = node.getParent();

        while (parent != null && parent.isRed()) {
            RBTreeNode<T> uncle = getUncle(node);
            if (uncle == null || !uncle.isRed()) {
                // 祖先
                RBTreeNode<T> ancestor = parent.getParent();

                if (ancestor == null) {
                    parent.setRed(false);
                    return;
                }
                if (ancestor.getLeft() == parent) {
                    boolean isRight = node == parent.getRight();
                    if (isRight) {
                        // 左旋，就变成一条斜线
                        rotateLeft(parent);
                    }
                    // 一条斜线，右旋
                    rotateRight(ancestor);
                    switchColor(ancestor, ancestor.getParent());
                    parent = null;
                } else {
                    boolean isLeft = node == parent.getLeft();
                    if (isLeft) {
                        rotateRight(parent);
                    }
                    rotateLeft(ancestor);
                    if (isLeft) {
                        node.setRed(false);
                        parent = null;
                    } else {
                        parent.setRed(false);
                    }
                    ancestor.setRed(true);
                }
            } else if (uncle.isRed()) {
                // 1. 叔叔不为空
                uncle.setRed(false);
                parent.setRed(false);
                parent.getParent().setRed(true);
                node = parent.getParent();
                parent = node.getParent();
            }
        }
        getRoot().setRed(false);
        getRoot().setParent(null);
    }

    /**
     * 右旋
     *
     * @param node node
     */
    private void rotateRight(RBTreeNode<T> node) {
        // 把待替换节(n)的左节点(l)替换它
        RBTreeNode<T> replaceNode = node.getLeft();
        RBTreeNode<T> parent = node.getParent();
        node.setLeft(replaceNode.getRight());
        setParent(replaceNode.getRight(), node);

        replaceNode.setRight(node);
        setParent(node, replaceNode);

        if (parent == null) {
            root = replaceNode;
            setParent(replaceNode, null);
        } else {
            if (parent.getLeft() == node) {
                parent.setLeft(replaceNode);
            } else {
                parent.setRight(replaceNode);
            }
            setParent(replaceNode, parent);
        }
    }

    /**
     * 左旋
     *
     * @param node node
     */
    private void rotateLeft(RBTreeNode<T> node) {
        // 把待替换节(n)的右节点(r)替换它
        RBTreeNode<T> replaceNode = node.getRight();
        node.setRight(replaceNode.getLeft());

        if (replaceNode.getLeft() != null) {
            replaceNode.getLeft().setParent(node);
        }
        // r.p = n.p
        replaceNode.setParent(node.getParent());
        if (node.getParent() == null) {
            // p = null, 就是根节点
            root = replaceNode;
        } else {
            // p 重置子节点
            if (node.getParent().getLeft() == node) {
                node.getParent().setLeft(replaceNode);
            } else {
                node.getParent().setRight(replaceNode);
            }
        }

        // r.l = n
        replaceNode.setLeft(node);
        // n.p = r
        node.setParent(replaceNode);
    }

    /**
     * 寻找父节点:
     * 跟二叉查找树一样，比对节点值大小，找到要插入的父节点
     *
     * @param node node
     * @return RBTreeNode node
     */
    public RBTreeNode<T> findParentNode(RBTreeNode<T> node) {
        RBTreeNode<T> parent = root;
        RBTreeNode<T> child = root;
        while (child != null) {
            int result = child.getValue().compareTo(node.getValue());
            if (result == 0) {
                // 和其中一只节点相同
                return child;
            }
            parent = child;
            if (result > 0) {
                child = child.getLeft();
            } else {
                child = child.getRight();
            }
        }
        return parent;
    }

    /**
     * 找到叔叔节点
     *
     * @param node node
     * @return uncle
     */
    public RBTreeNode<T> getUncle(RBTreeNode<T> node) {
        RBTreeNode<T> parent = node.getParent();
        RBTreeNode<T> grandfather = parent.getParent();
        if (grandfather == null) {
            return null;
        }
        if (parent == grandfather.getLeft()) {
            return grandfather.getRight();
        } else {
            return grandfather.getLeft();
        }
    }

    /**
     * set parent
     *
     * @param node   node
     * @param parent parent
     */
    private void setParent(RBTreeNode<T> node, RBTreeNode<T> parent) {
        if (node != null) {
            node.setParent(parent);
            if (parent == root) {
                node.setParent(null);
            }
        }
    }

    public void remove(T date) {
        RBTreeNode<T> t = new RBTreeNode<>(date);
        remove(t);
    }

    /**
     * 删除节点
     *
     * @param node
     */
    private void remove(RBTreeNode<T> node) {
        RBTreeNode<T> dataRoot = getRoot();
        RBTreeNode<T> parent = root;
        while (dataRoot != null) {
            int cmp = dataRoot.getValue().compareTo(node.getValue());
            if (cmp < 0) {
                // 遍历右子树
                parent = dataRoot;
                dataRoot = dataRoot.getRight();
            } else if (cmp > 0) {
                // 遍历左子树
                parent = dataRoot;
                dataRoot = dataRoot.getLeft();
            } else {
                // 找到对应节点
                if (dataRoot.getRight() != null && dataRoot.getLeft() != null) {
                    // 被删除节点的"左右孩子都不为空"的情况
                    RBTreeNode<T> successor = findMin(dataRoot.getRight());
                    dataRoot.setValue(successor.getValue());
                    node = successor;
                    dataRoot = dataRoot.getRight();
                } else {
                    // 最多只有一个孩子
                    if (dataRoot.isRed()) {
                        // 两个节点不是非空且是红色，那么孩子一定都是叶子节点
                        if (parent.getLeft() == dataRoot) {
                            parent.setLeft(null);
                        } else {
                            parent.setRight(null);
                        }
                        dataRoot = dataRoot.getLeft() == null ? dataRoot.getRight() : dataRoot.getLeft();
                    } else {
                        // 最多只有一个孩子的黑色节点：1. 孩子是红色，2. 没有孩子(叶子节点)
                        RBTreeNode<T> onlyChild = findOneChild(dataRoot);
                        if (isRed(onlyChild)) {
                            //1. 孩子是红色
                            node.setValue(onlyChild.getValue());
                            if (node.getLeft() == onlyChild) {
                                node.setLeft(null);
                            } else {
                                node.setRight(null);
                            }
                            return;
                        }
                        // 2. 没有孩子(都是叶子节点)
                        fixRemove(dataRoot);
                        if (dataRoot.getParent().getLeft() == dataRoot) {
                            dataRoot.getParent().setLeft(null);
                        } else {
                            dataRoot.getParent().setRight(null);
                        }
                        dataRoot = null;
                    }
                }
            }
        }
    }

    private void fixRemove(RBTreeNode<T> node) {
        if (node == null || node.isRed()) {
            throw new IllegalArgumentException("Node err ... ");
        }
        RBTreeNode<T> onlyChild = findOneChild(node);
        if (isRed(onlyChild)) {
            throw new IllegalArgumentException("Node err ... ");
        } else {
            // 删除节点是黑色，儿子也是黑色(其实应该说孩子都是叶子节点)
            RBTreeNode<T> parent = node.getParent();
            if (parent == null) {
                // case1: 被删除的是根节点，替换后无需处理。
                root = null;
                return;
            }
            boolean isLeft = parent.getLeft() == node;
            RBTreeNode<T> sibling = getSibling(node);
            if (isRed(sibling)) {
                // case2:  S节点为红色
                if (isLeft) {
                    rotateLeft(parent);
                } else {
                    rotateRight(parent);
                }
                parent.setRed(true);
                parent.getParent().setRed(false);
            }
            if (!parent.isRed() && !isRed(sibling) && !isRed(sibling.getLeft()) && !isRed(sibling.getRight())) {
                // case3: P,S,S1,S2都是黑色
                sibling.setRed(true);
                fixRemove(parent);
            }
            if (isRed(parent) && !isRed(sibling.getLeft()) && !isRed(sibling.getRight())) {
                //case4： P为红色，S（一定黑）和儿子都不是红色
                switchColor(parent, sibling);
                return;
            }
            if (isLeft && !isRed(sibling) && isRed(sibling.getLeft()) && !isRed(sibling.getRight())) {
                //case5： S是黑色，S的一侧(N同侧)儿子黑色，另一侧儿子为红色
                rotateRight(sibling);
                switchColor(sibling, sibling.getParent());
            } else if (!isLeft && !isRed(sibling) && isRed(sibling.getRight()) && !isRed(sibling.getLeft())) {
                rotateLeft(sibling);
                switchColor(sibling, sibling.getParent());
            }
            if (isLeft && !isRed(sibling) && !isRed(sibling.getLeft()) && isRed(sibling.getRight())) {
                rotateLeft(parent);
                switchColor(parent, sibling);
            } else if (!isLeft && !isRed(sibling) && isRed(sibling.getLeft()) && !isRed(sibling.getRight())) {
                rotateRight(parent);
                switchColor(parent, sibling);
            }
        }
    }

    private void switchColor(RBTreeNode<T> parent, RBTreeNode<T> sibling) {
        boolean red = isRed(parent);
        parent.setRed(isRed(sibling));
        sibling.setRed(red);
    }

    private RBTreeNode<T> getSibling(RBTreeNode<T> node) {
        if (node == null || node.getParent() == null) {
            return null;
        }
        if (node.getParent().getLeft() == node) {
            return node.getParent().getRight();
        }
        return node.getParent().getLeft();
    }

    private boolean isRed(RBTreeNode<T> node) {
        return node != null && node.isRed();
    }

    private RBTreeNode<T> findOneChild(RBTreeNode<T> node) {
        if (node == null) {
            throw new IllegalArgumentException("Node err ... ");
        }
        return node.getLeft() == null ? node.getRight() : node.getLeft();
    }

    private RBTreeNode<T> findMin(RBTreeNode<T> node) {
        if (node == null) {
            return null;
        }
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    public void printTree(RBTreeNode<T> root) {
        LinkedList<RBTreeNode<T>> queue1 = new java.util.LinkedList<>();
        LinkedList<RBTreeNode<T>> queue2 = new LinkedList<RBTreeNode<T>>();
        if (root == null) {
            return;
        }
        queue1.add(root);
        boolean firstQueue = true;
        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            LinkedList<RBTreeNode<T>> q = firstQueue ? queue1 : queue2;
            RBTreeNode<T> n = q.poll();
            if (n != null) {
                String pos = n.getParent() == null ? "" : (n == n.getParent().getLeft() ? "/left" : "/right");
                String pstr = n.getParent() == null ? "" : n.getParent().toString();
                String cstr = n.isRed() ? "R" : "B";
                cstr = n.getParent() == null ? cstr : cstr + " ";
                System.out.print(n + "(" + (cstr) + pstr + (pos) + ")" + "\t");
                if (n.getLeft() != null) {
                    (firstQueue ? queue2 : queue1).add(n.getLeft());
                }
                if (n.getRight() != null) {
                    (firstQueue ? queue2 : queue1).add(n.getRight());
                }
            } else {
                System.out.println();
                firstQueue = !firstQueue;
            }
        }
    }
}
