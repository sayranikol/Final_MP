import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class GuiTreeNode {
    int key;
    String title;
    GuiTreeNode left;
    GuiTreeNode right;

    GuiTreeNode(int key, String title) {
        this.key = key;
        this.title = title;
    }
}

class GuiBinarySearchTree {
    GuiTreeNode root;

    String insert(int key, String title) {
        GuiTreeNode node = new GuiTreeNode(key, title);
        if (root == null) {
            root = node;
            return "Inserted " + key + " as the root node.";
        }

        GuiTreeNode current = root;
        while (true) {
            if (key == current.key) {
                current.title = title;
                return "Updated existing node " + key + ".";
            }

            if (key < current.key) {
                if (current.left == null) {
                    current.left = node;
                    return "Inserted " + key + " to the LEFT of " + current.key + ".";
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = node;
                    return "Inserted " + key + " to the RIGHT of " + current.key + ".";
                }
                current = current.right;
            }
        }
    }

    List<Integer> searchPath(int key) {
        List<Integer> path = new ArrayList<>();
        GuiTreeNode current = root;
        while (current != null) {
            path.add(current.key);
            if (key == current.key) return path;
            current = key < current.key ? current.left : current.right;
        }
        return path;
    }

    List<Integer> traverse(String type) {
        List<Integer> result = new ArrayList<>();
        if ("Preorder".equals(type)) preorder(root, result);
        else if ("Postorder".equals(type)) postorder(root, result);
        else inorder(root, result);
        return result;
    }

    int count() {
        return count(root);
    }

    int height() {
        return height(root);
    }

    void clear() {
        root = null;
    }

    private void inorder(GuiTreeNode node, List<Integer> result) {
        if (node == null) return;
        inorder(node.left, result);
        result.add(node.key);
        inorder(node.right, result);
    }

    private void preorder(GuiTreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.key);
        preorder(node.left, result);
        preorder(node.right, result);
    }

    private void postorder(GuiTreeNode node, List<Integer> result) {
        if (node == null) return;
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.key);
    }

    private int count(GuiTreeNode node) {
        return node == null ? 0 : 1 + count(node.left) + count(node.right);
    }

    private int height(GuiTreeNode node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }
}

class TreeCanvas extends JPanel {
    private final GuiBinarySearchTree tree;
    private final Set<Integer> highlights = new HashSet<>();
    private final Map<GuiTreeNode, PointData> positions = new HashMap<>();
    private String activity = "Default";

    TreeCanvas(GuiBinarySearchTree tree) {
        this.tree = tree;
        setBackground(new Color(11, 11, 27));
        setPreferredSize(new Dimension(1050, 620));
    }

    void setActivity(String activity) {
        this.activity = activity;
    }

    void setHighlights(List<Integer> keys) {
        highlights.clear();
        highlights.addAll(keys);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintBackground(g2);
        positions.clear();

        if (tree.root == null) {
            g2.setColor(new Color(190, 184, 220));
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            drawCentered(g2, "Binary tree is empty. Insert a key to create a new root.", getWidth() / 2, getHeight() / 2);
            return;
        }

        assignPositions(tree.root, 0, getWidth() / 2, Math.max(86, getWidth() / 4));
        drawEdges(g2, tree.root);
        drawNodes(g2, tree.root, 0);
    }

    private void paintBackground(Graphics2D g2) {
        GradientPaint bg = new GradientPaint(0, 0, new Color(20, 17, 48), getWidth(), getHeight(), new Color(5, 5, 14));
        g2.setPaint(bg);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void assignPositions(GuiTreeNode node, int depth, int x, int gap) {
        if (node == null) return;
        int y = 80 + depth * 112;
        positions.put(node, new PointData(x, y));
        assignPositions(node.left, depth + 1, x - gap, Math.max(70, gap / 2));
        assignPositions(node.right, depth + 1, x + gap, Math.max(70, gap / 2));
    }

    private void drawEdges(Graphics2D g2, GuiTreeNode node) {
        if (node == null) return;
        PointData parent = positions.get(node);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(130, 104, 255));
        if (node.left != null) {
            PointData child = positions.get(node.left);
            g2.drawLine(parent.x, parent.y, child.x, child.y);
        }
        if (node.right != null) {
            PointData child = positions.get(node.right);
            g2.drawLine(parent.x, parent.y, child.x, child.y);
        }
        drawEdges(g2, node.left);
        drawEdges(g2, node.right);
    }

    private void drawNodes(Graphics2D g2, GuiTreeNode node, int depth) {
        if (node == null) return;
        PointData point = positions.get(node);
        boolean highlighted = highlights.contains(node.key);
        if ("Playing Cards".equals(activity)) drawCard(g2, node, point, highlighted);
        else if ("Sort People".equals(activity)) drawPerson(g2, node, point, highlighted);
        else if ("Task Priority".equals(activity)) drawTask(g2, node, point, highlighted);
        else if ("Item Prices".equals(activity)) drawPriceTag(g2, node, point, highlighted);
        else drawBasicNode(g2, node, point, depth, highlighted);

        drawNodes(g2, node.left, depth + 1);
        drawNodes(g2, node.right, depth + 1);
    }

    private void drawBasicNode(Graphics2D g2, GuiTreeNode node, PointData p, int depth, boolean highlighted) {
        Color fill = highlighted ? new Color(39, 215, 184) : depth == 0 ? new Color(235, 229, 255) : new Color(255, 250, 224);
        g2.setColor(fill);
        g2.fillRoundRect(p.x - 52, p.y - 28, 104, 56, 14, 14);
        g2.setColor(depth == 0 ? new Color(142, 97, 255) : new Color(244, 201, 93));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(p.x - 52, p.y - 28, 104, 56, 14, 14);
        g2.setColor(new Color(46, 30, 112));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        drawCentered(g2, String.valueOf(node.key), p.x, p.y - 4);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        drawCentered(g2, node.title, p.x, p.y + 14);
    }

    private void drawCard(Graphics2D g2, GuiTreeNode node, PointData p, boolean highlighted) {
        boolean red = node.title.contains("Hearts") || node.title.contains("Diamonds");
        String suit = node.title.contains("Hearts") ? "H" : node.title.contains("Diamonds") ? "D" : node.title.contains("Clubs") ? "C" : "S";
        g2.setColor(highlighted ? new Color(214, 255, 248) : Color.WHITE);
        g2.fillRoundRect(p.x - 34, p.y - 48, 68, 96, 10, 10);
        g2.setColor(red ? new Color(201, 52, 95) : new Color(32, 34, 51));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(p.x - 34, p.y - 48, 68, 96, 10, 10);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(cardRank(node.key), p.x - 25, p.y - 29);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        drawCentered(g2, suit, p.x, p.y + 8);
    }

    private String cardRank(int key) {
        if (key == 14) return "A";
        if (key == 13) return "K";
        if (key == 12) return "Q";
        if (key == 11) return "J";
        return String.valueOf(key);
    }

    private void drawPerson(Graphics2D g2, GuiTreeNode node, PointData p, boolean highlighted) {
        int bodyHeight = Math.max(32, Math.min(74, node.key / 3));
        g2.setColor(new Color(219, 214, 238));
        g2.fillRoundRect(p.x - 46, p.y - 44, 6, 88, 6, 6);
        g2.setColor(new Color(39, 215, 184));
        g2.fillRoundRect(p.x - 46, p.y + 44 - bodyHeight, 6, bodyHeight, 6, 6);
        g2.setColor(highlighted ? new Color(39, 215, 184) : new Color(124, 92, 255));
        g2.fillOval(p.x - 17, p.y - 42, 34, 34);
        g2.fillRoundRect(p.x - 25, p.y - 11, 50, 42, 18, 18);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        drawCentered(g2, node.title.substring(0, 1), p.x, p.y - 20);
        g2.setColor(new Color(245, 242, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        drawCentered(g2, node.title + " " + node.key + "cm", p.x, p.y + 48);
    }

    private void drawTask(Graphics2D g2, GuiTreeNode node, PointData p, boolean highlighted) {
        g2.setColor(highlighted ? new Color(218, 255, 248) : new Color(243, 239, 255));
        g2.fillRoundRect(p.x - 54, p.y - 35, 108, 70, 12, 12);
        g2.setColor(new Color(244, 201, 93));
        g2.fillOval(p.x - 7, p.y - 31, 14, 14);
        g2.setColor(new Color(67, 37, 173));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        drawCentered(g2, String.valueOf(node.key), p.x, p.y + 2);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        drawCentered(g2, node.title, p.x, p.y + 20);
    }

    private void drawPriceTag(Graphics2D g2, GuiTreeNode node, PointData p, boolean highlighted) {
        g2.setColor(highlighted ? new Color(218, 255, 248) : new Color(255, 249, 232));
        g2.fillRoundRect(p.x - 56, p.y - 32, 112, 64, 22, 10);
        g2.setColor(new Color(17, 17, 38));
        g2.fillOval(p.x - 45, p.y - 5, 10, 10);
        g2.setColor(new Color(6, 20, 17));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        drawCentered(g2, "$" + node.key, p.x, p.y - 2);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        drawCentered(g2, node.title, p.x, p.y + 18);
    }

    private void drawCentered(Graphics2D g2, String text, int x, int y) {
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, x - metrics.stringWidth(text) / 2, y);
    }

    private static class PointData {
        int x;
        int y;

        PointData(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

public class BinaryTreeSkillQuestApp extends JFrame {
    private final GuiBinarySearchTree tree = new GuiBinarySearchTree();
    private final TreeCanvas canvas = new TreeCanvas(tree);
    private final JLabel stats = new JLabel("Nodes: 0 | Height: 0");
    private final JTextArea output = new JTextArea("Choose an activity or insert a node.");
    private final JTextField keyInput = new JTextField("50");
    private final JTextField titleInput = new JTextField("Master Java Basics");
    private final JTextField searchInput = new JTextField("65");
    private final JComboBox<String> activityBox = new JComboBox<>(new String[] {
        "Default", "Playing Cards", "Sort People", "Task Priority", "Item Prices"
    });

    public BinaryTreeSkillQuestApp() {
        super("SkillQuest Binary Tree Java GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(8, 8, 19));

        add(buildControls(), BorderLayout.WEST);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        add(buildOutput(), BorderLayout.SOUTH);

        setSize(1280, 780);
        setLocationRelativeTo(null);
        loadDefaultTree();
    }

    private JPanel buildControls() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 700));
        panel.setBackground(new Color(18, 19, 36));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setLayout(new java.awt.GridLayout(0, 1, 8, 8));

        JLabel title = new JLabel("SkillQuest Java GUI");
        title.setForeground(new Color(244, 241, 255));
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton insert = new JButton("Insert Node");
        JButton search = new JButton("Search");
        JButton reset = new JButton("Reset");
        JButton inorder = new JButton("Inorder");
        JButton preorder = new JButton("Preorder");
        JButton postorder = new JButton("Postorder");
        JButton loadActivity = new JButton("Load Activity");

        insert.addActionListener(this::insertNode);
        search.addActionListener(this::searchNode);
        reset.addActionListener(event -> {
            tree.clear();
            canvas.setActivity("Default");
            activityBox.setSelectedItem("Default");
            output.setText("Tree cleared. Insert a new root to start again.");
            refresh(new ArrayList<>());
        });
        inorder.addActionListener(event -> traverse("Inorder"));
        preorder.addActionListener(event -> traverse("Preorder"));
        postorder.addActionListener(event -> traverse("Postorder"));
        loadActivity.addActionListener(event -> loadActivity((String) activityBox.getSelectedItem()));

        panel.add(title);
        panel.add(label("Activity"));
        panel.add(activityBox);
        panel.add(loadActivity);
        panel.add(label("Key"));
        panel.add(keyInput);
        panel.add(label("Title"));
        panel.add(titleInput);
        panel.add(insert);
        panel.add(label("Search Key"));
        panel.add(searchInput);
        panel.add(search);
        panel.add(inorder);
        panel.add(preorder);
        panel.add(postorder);
        panel.add(reset);
        panel.add(stats);

        return panel;
    }

    private JScrollPane buildOutput() {
        output.setEditable(false);
        output.setRows(3);
        output.setBackground(new Color(5, 5, 13));
        output.setForeground(new Color(200, 255, 244));
        output.setFont(new Font("Consolas", Font.PLAIN, 14));
        return new JScrollPane(output);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(190, 184, 220));
        return label;
    }

    private void insertNode(ActionEvent event) {
        int key = Integer.parseInt(keyInput.getText().trim());
        String title = titleInput.getText().trim().isEmpty() ? "Untitled" : titleInput.getText().trim();
        output.setText(tree.insert(key, title));
        refresh(List.of(key));
    }

    private void searchNode(ActionEvent event) {
        int key = Integer.parseInt(searchInput.getText().trim());
        List<Integer> path = tree.searchPath(key);
        output.setText("Search path for " + key + ": " + path);
        refresh(path);
    }

    private void traverse(String type) {
        List<Integer> result = tree.traverse(type);
        output.setText(type + ": " + result);
        refresh(result);
    }

    private void loadDefaultTree() {
        tree.clear();
        canvas.setActivity("Default");
        int[][] data = {{50, 0}, {30, 0}, {70, 0}, {20, 0}, {40, 0}, {65, 0}, {85, 0}};
        String[] titles = {"Master Java", "HTML UI", "Database", "Variables", "Login Page", "Debug", "Present"};
        for (int i = 0; i < data.length; i++) tree.insert(data[i][0], titles[i]);
        refresh(new ArrayList<>());
    }

    private void loadActivity(String activity) {
        tree.clear();
        canvas.setActivity(activity);

        if ("Playing Cards".equals(activity)) {
            addAll(new int[] {8, 4, 12, 2, 6, 10, 14}, new String[] {
                "8 of Hearts", "4 of Clubs", "Queen of Diamonds", "2 of Spades",
                "6 of Diamonds", "10 of Hearts", "Ace of Spades"
            });
        } else if ("Sort People".equals(activity)) {
            addAll(new int[] {170, 158, 184, 151, 164, 176, 190}, new String[] {
                "Mika", "Ana", "Leo", "Sam", "Kai", "Nia", "Ben"
            });
        } else if ("Task Priority".equals(activity)) {
            addAll(new int[] {50, 25, 80, 10, 35, 65, 95}, new String[] {
                "Project", "Review", "Submit", "Warmup", "Practice", "Debug", "Present"
            });
        } else if ("Item Prices".equals(activity)) {
            addAll(new int[] {45, 20, 120, 12, 35, 90, 180}, new String[] {
                "Notebook", "Pen Set", "Keyboard", "Eraser", "Planner", "Mouse", "Monitor"
            });
        } else {
            loadDefaultTree();
            return;
        }

        output.setText(activity + " loaded as a binary search tree.");
        refresh(tree.traverse("Inorder"));
    }

    private void addAll(int[] keys, String[] titles) {
        for (int i = 0; i < keys.length; i++) tree.insert(keys[i], titles[i]);
    }

    private void refresh(List<Integer> highlights) {
        stats.setForeground(new Color(244, 241, 255));
        stats.setText("Nodes: " + tree.count() + " | Height: " + tree.height());
        canvas.setHighlights(highlights);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BinaryTreeSkillQuestApp().setVisible(true));
    }
}
