import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int key;
    String title;
    TreeNode left;
    TreeNode right;

    TreeNode(int key, String title) {
        this.key = key;
        this.title = title;
    }
}

class BinarySearchTree {
    private TreeNode root;

    public String insert(int key, String title) {
        TreeNode newNode = new TreeNode(key, title);

        if (root == null) {
            root = newNode;
            return "Inserted as root node";
        }

        TreeNode current = root;
        while (true) {
            if (key == current.key) {
                current.title = title;
                return "Updated existing node";
            }

            if (key < current.key) {
                if (current.left == null) {
                    current.left = newNode;
                    return "Inserted " + key + " to the LEFT of " + current.key;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = newNode;
                    return "Inserted " + key + " to the RIGHT of " + current.key;
                }
                current = current.right;
            }
        }
    }

    public List<Integer> searchPath(int key) {
        List<Integer> path = new ArrayList<>();
        TreeNode current = root;

        while (current != null) {
            path.add(current.key);
            if (key == current.key) {
                return path;
            }
            current = key < current.key ? current.left : current.right;
        }

        return path;
    }

    public List<Integer> inorder() {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    public List<Integer> preorder() {
        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    public List<Integer> postorder() {
        List<Integer> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    public int count() {
        return count(root);
    }

    public int height() {
        return height(root);
    }

    public void clear() {
        root = null;
    }

    private void inorder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        inorder(node.left, result);
        result.add(node.key);
        inorder(node.right, result);
    }

    private void preorder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.key);
        preorder(node.left, result);
        preorder(node.right, result);
    }

    private void postorder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.key);
    }

    private int count(TreeNode node) {
        return node == null ? 0 : 1 + count(node.left) + count(node.right);
    }

    private int height(TreeNode node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }
}

public class BinarySearchTreeDemo {
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();

        tree.insert(50, "Project");
        tree.insert(25, "Review");
        tree.insert(80, "Submit");
        tree.insert(10, "Warmup");
        tree.insert(35, "Practice");
        tree.insert(65, "Debug");
        tree.insert(95, "Present");

        System.out.println("Inorder: " + tree.inorder());
        System.out.println("Preorder: " + tree.preorder());
        System.out.println("Postorder: " + tree.postorder());
        System.out.println("Search path for 65: " + tree.searchPath(65));
        System.out.println("Nodes: " + tree.count());
        System.out.println("Height: " + tree.height());
    }
}
