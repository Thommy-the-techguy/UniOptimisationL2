package com.tomomoto.gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ShortestPath extends JFrame {
    private final List<String> pathReversed;
    private JLabel pathLabel;
    public ShortestPath(JFrame parentWindow, List<String> pathReversed) {
        super("Путь");
        setSize(250, 100);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });
        setResizable(false);
        setLayout(null);
        this.pathReversed = pathReversed;
        initializeComponents();
        setVisible(true);

    }

    private void initializeComponents() {
        initializePathLabel();
    }

    private void initializePathLabel() {
        pathLabel = new JLabel(convertPathToString(reversePath()));
        pathLabel.setSize(200, 30);
        pathLabel.setLocation(0, 0);
        this.add(pathLabel);
    }

    private String convertPathToString(List<String> path) {
        StringBuilder stringBuilder = new StringBuilder();
        path.forEach(node -> {
            stringBuilder.append(node).append(" -> ");
        });
        stringBuilder.replace(stringBuilder.lastIndexOf(" -> "), stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    private List<String> reversePath() {
        List<String> path = new ArrayList<>();
        for (int i = pathReversed.size() - 1; i >= 0; i--) {
            path.add(pathReversed.get(i));
        }
        return path;
    }
}
