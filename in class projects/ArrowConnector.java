package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

/**
 * ArrowConnector
 * ---------------
 * Draws two lists of items side by side and connects elements that are
 * common to both lists with an arrow.
 *
 * How it works:
 *   1. Two lists (List A on the left, List B on the right) are rendered
 *      as vertical stacks of labeled boxes.
 *   2. The common elements between the two lists are found.
 *   3. For every common element, an arrow is drawn from its box in
 *      List A to its (possibly multiple) matching box(es) in List B.
 *
 * This is a self-contained Swing application: compile and run with
 *   javac ArrowConnector.java
 *   java ArrowConnector
 */
public class ArrowConnector extends JPanel {

    // ---- Data ----------------------------------------------------------
    private final List<String> listA;
    private final List<String> listB;

    // ---- Layout constants -----------------------------------------------
    private static final int BOX_WIDTH   = 140;
    private static final int BOX_HEIGHT  = 40;
    private static final int VERTICAL_GAP = 20;
    private static final int TOP_MARGIN   = 60;
    private static final int LEFT_X       = 80;   // x position of list A boxes
    private static final int RIGHT_X      = 480;  // x position of list B boxes

    // Colors
    private static final Color BOX_COLOR_DEFAULT = new Color(224, 224, 224);
    private static final Color BOX_COLOR_MATCHED  = new Color(179, 216, 255);
    private static final Color BOX_BORDER         = new Color(70, 70, 70);
    private static final Color ARROW_COLOR        = new Color(200, 60, 60);
    private static final Color TEXT_COLOR         = new Color(20, 20, 20);

    public ArrowConnector(List<String> listA, List<String> listB) {
        this.listA = listA;
        this.listB = listB;
        int height = TOP_MARGIN + Math.max(listA.size(), listB.size()) * (BOX_HEIGHT + VERTICAL_GAP) + 40;
        setPreferredSize(new Dimension(700, height));
        setBackground(Color.WHITE);
    }

    /** Returns the set of elements present in both lists. */
    private Set<String> commonElements() {
        Set<String> setA = new HashSet<>(listA);
        Set<String> setB = new HashSet<>(listB);
        setA.retainAll(setB);
        return setA;
    }

    /** Y coordinate of the vertical center of the box at the given index. */
    private int centerY(int index) {
        return TOP_MARGIN + index * (BOX_HEIGHT + VERTICAL_GAP) + BOX_HEIGHT / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

        Set<String> common = commonElements();

        // Column headers
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("List A", LEFT_X, TOP_MARGIN - 25);
        g2.drawString("List B", RIGHT_X, TOP_MARGIN - 25);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Draw arrows first so boxes are painted cleanly on top of the lines
        for (int i = 0; i < listA.size(); i++) {
            String value = listA.get(i);
            if (!common.contains(value)) continue;
            for (int j = 0; j < listB.size(); j++) {
                if (listB.get(j).equals(value)) {
                    drawArrow(g2,
                            LEFT_X + BOX_WIDTH, centerY(i),
                            RIGHT_X, centerY(j));
                }
            }
        }

        // Draw the boxes for List A
        for (int i = 0; i < listA.size(); i++) {
            boolean matched = common.contains(listA.get(i));
            drawBox(g2, LEFT_X, TOP_MARGIN + i * (BOX_HEIGHT + VERTICAL_GAP), listA.get(i), matched);
        }

        // Draw the boxes for List B
        for (int j = 0; j < listB.size(); j++) {
            boolean matched = common.contains(listB.get(j));
            drawBox(g2, RIGHT_X, TOP_MARGIN + j * (BOX_HEIGHT + VERTICAL_GAP), listB.get(j), matched);
        }
    }

    /** Draws a single labeled rounded box at the given top-left position. */
    private void drawBox(Graphics2D g2, int x, int y, String label, boolean matched) {
        RoundRectangle2D box = new RoundRectangle2D.Float(x, y, BOX_WIDTH, BOX_HEIGHT, 12, 12);
        g2.setColor(matched ? BOX_COLOR_MATCHED : BOX_COLOR_DEFAULT);
        g2.fill(box);
        g2.setColor(BOX_BORDER);
        g2.draw(box);

        g2.setColor(TEXT_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int textX = x + (BOX_WIDTH - textWidth) / 2;
        int textY = y + (BOX_HEIGHT + fm.getAscent()) / 2 - 2;
        g2.drawString(label, textX, textY);
    }

    /** Draws a curved arrow from (x1, y1) to (x2, y2) with an arrowhead at the end. */
    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(ARROW_COLOR);
        g2.setStroke(new BasicStroke(2f));

        // A gentle curve looks cleaner than a straight line when many
        // arrows are drawn close together.
        int ctrlX = (x1 + x2) / 2;
        QuadCurve2D curve = new QuadCurve2D.Float(x1, y1, ctrlX, (y1 + y2) / 2, x2, y2);
        g2.draw(curve);

        // Arrowhead: compute the direction at the end of the curve using
        // the tangent between the control point and the end point.
        double angle = Math.atan2(y2 - (y1 + y2) / 2.0, x2 - ctrlX);
        int arrowLength = 10;
        double arrowAngle = Math.toRadians(25);

        int xArrow1 = (int) (x2 - arrowLength * Math.cos(angle - arrowAngle));
        int yArrow1 = (int) (y2 - arrowLength * Math.sin(angle - arrowAngle));
        int xArrow2 = (int) (x2 - arrowLength * Math.cos(angle + arrowAngle));
        int yArrow2 = (int) (y2 - arrowLength * Math.sin(angle + arrowAngle));

        Polygon head = new Polygon();
        head.addPoint(x2, y2);
        head.addPoint(xArrow1, yArrow1);
        head.addPoint(xArrow2, yArrow2);
        g2.fill(head);
    }

    // ---- Demo entry point -------------------------------------------------
    public static void main(String[] args) {
        // Example data — replace with your own lists, or read them from
        // input/a file for your assignment.
        List<String> listA = Arrays.asList("Apple", "Banana", "Cherry", "Date", "Fig", "Grape");
        List<String> listB = Arrays.asList("Kiwi", "Banana", "Mango", "Fig", "Cherry", "Lemon");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Common Elements — Arrow Connector");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ArrowConnector panel = new ArrowConnector(listA, listB);
            JScrollPane scrollPane = new JScrollPane(panel);
            frame.add(scrollPane);

            frame.setSize(760, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
