package com.tomomoto.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MainForm extends JFrame {
    private JTable matrixTable;
    private JScrollPane scrollPane;
    private JButton readFromFileButton;
    private JButton findPathButton;
    private final Path pathToMatrixFile = Path.of("src", "main", "resources", "matrix.txt");
    private int[][] matrix = new int[6][6];

    public MainForm() {
        super("Алгоритм Дейкстры");
        setSize(500, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        initializeComponents();
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainForm();
    }

    private void initializeComponents() {
        initializeMatrixTable();
        initializeScrollPane();
        initializeReadFromFileButton();
        initializeFindPathButton();
    }

    private Map<String, List<String>> getMapOfVertexes() {
        Map<String, List<String>> mapOfVertexes = new LinkedHashMap<>();
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            String key = String.format("v%d", i + 1);
            List<String> values = new ArrayList<>();
            for (int j = 1; j < matrixTable.getColumnCount(); j++) {
                if (matrixTable.getValueAt(i, j).equals(1)) {
                    values.add(String.format("v%d", j));
                }
            }
            mapOfVertexes.put(key, values);
        }
        return mapOfVertexes;
    }

    private void initializeReadFromFileButton() {
        readFromFileButton = new JButton("Считать");
        readFromFileButton.setSize(100, 30);
        readFromFileButton.setLocation(400, 0);
        readFromFileButton.addActionListener(event -> {
            try {
                List<String> matrixLinesList = readConnectionsFromMatrixFile();
                System.out.println(matrixLinesList);
                transferMatrixValuesToArray(convertStringValuesToInt(matrixLinesList));
                fillMatrixTableWithArrayMatrix();

                convertWeightsMatrixLinesToMap(readWeightsFromMatrixFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.add(readFromFileButton);
    }

    private void initializeFindPathButton() {
        findPathButton = new JButton("Найти путь");
        findPathButton.setSize(100, 30);
        findPathButton.setLocation(200, 230);
        findPathButton.addActionListener((event) -> {
            try {
                new Graph(this, getMapOfVertexes(), convertWeightsMatrixLinesToMap(readWeightsFromMatrixFile()));
                new ShortestPath(this, findShortestPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.add(findPathButton);
    }

    private List<String> findShortestPath() throws IOException {
        Map<String, List<String>> weightsMatrixMap = convertWeightsMatrixLinesToMap(readWeightsFromMatrixFile());
        Map<String, Integer> costs = new LinkedHashMap<>();
        Map<String, String> parents = new LinkedHashMap<>();
        List<String> processed = new ArrayList<>();

        fillCostsMap(weightsMatrixMap, costs);
        fillParentsMap(weightsMatrixMap, parents);

        String node = findLowestCostNode(costs, processed);
        while (!node.equals(getLastNode(weightsMatrixMap))) {
            int cost = costs.get(node);
            List<String> neighbors = weightsMatrixMap.get(node);
            for (String neighbor : neighbors) {
                int newCost = cost + Integer.parseInt(neighbor.split("-")[1]);
                if (costs.get(neighbor.split("-")[0]) > newCost) {
                    costs.put(neighbor.split("-")[0], newCost);
                    parents.put(neighbor.split("-")[0], node);
                }
            }
            processed.add(node);
            node = findLowestCostNode(costs, processed);
        }

        List<String> resultPathReversed = new ArrayList<>();
        String vertex = parents.keySet().stream().filter(item -> item.equals(getLastNode(weightsMatrixMap))).toList().get(0);
        while (!vertex.equals("v1")) {
            resultPathReversed.add(vertex);
            vertex = parents.get(vertex);
        }
        resultPathReversed.add("v1");
        System.out.println(resultPathReversed);
        System.out.println(costs);
        return resultPathReversed;
    }

    private String getLastNode(Map<String, List<String>> weightsMatrixMap) {
        return String.format("v%d", weightsMatrixMap.size());
    }

    private String findLowestCostNode(Map<String, Integer> costs, List<String> processed) {
        AtomicReference<String> lowestCostNode = new AtomicReference<>(null);
        AtomicInteger lowestCost = new AtomicInteger(9999);
        costs.forEach((key, value) -> {
            if (value < lowestCost.get() && !processed.contains(key)) {
                lowestCost.set(value);
                lowestCostNode.set(key);
            }
        });
        return lowestCostNode.get();
    }

    private void fillCostsMap(Map<String, List<String>> weightsMatrixMap, Map<String, Integer> costs) {
        for (int i = 0; i < weightsMatrixMap.get("v1").size(); i++) {
            costs.put(weightsMatrixMap.get("v1").get(i).split("-")[0], Integer.parseInt(weightsMatrixMap.get("v1").get(i).split("-")[1]));
        }
        for (int i = weightsMatrixMap.get("v1").size() + 1; i < weightsMatrixMap.size(); i++) {
            costs.put((String) weightsMatrixMap.keySet().toArray()[i], 9999);
        }
    }

    private void fillParentsMap(Map<String, List<String>> weightsMatrixMap, Map<String, String> parents) {
        for (int i = 0; i < weightsMatrixMap.get("v1").size(); i++) {
            parents.put(weightsMatrixMap.get("v1").get(i).split("-")[0], "v1");
        }
        parents.put("v6", "null");
    }

    private void initializeMatrixTable() {
        TableModel tableModel = new DefaultTableModel(getColumnNamesVector(), matrix.length);
        matrixTable = new JTable(tableModel);
        matrixTable.setGridColor(Color.BLACK);
        fillLeftTableNumbers();
        fillTableWithZeroes();
    }

    private void fillLeftTableNumbers() {
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            matrixTable.setValueAt(String.valueOf(i + 1), i, 0);
        }
    }

    private void fillTableWithZeroes() {
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            for (int j = 1; j < matrixTable.getColumnCount(); j++) {
                matrixTable.setValueAt(0, i, j);
            }
        }
    }
    
    private void fillMatrixTableWithArrayMatrix() {
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            for (int j = 1; j < matrixTable.getColumnCount(); j++) {
                matrixTable.setValueAt(matrix[i][j - 1], i, j);
            }
        }
    }

    private Vector<String> getColumnNamesVector() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        for (int i = 0; i < matrix.length; i++) {
            columnNames.add(String.valueOf(i + 1));
        }
        return columnNames;
    }

    private void initializeScrollPane() {
        scrollPane = new JScrollPane(matrixTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setSize(400, 200);
        scrollPane.setLocation(0, 0);
        this.add(scrollPane);
    }

    private List<String> readConnectionsFromMatrixFile() throws IOException {
        return List.of(Files.readString(pathToMatrixFile).split("_")[0].trim().split("\n"));
    }

    private List<String> readWeightsFromMatrixFile() throws IOException {
        return List.of(Files.readString(pathToMatrixFile).split("_")[1].trim().split("\n"));
    }

    private Map<String, List<String>> convertWeightsMatrixLinesToMap(List<String> weightsMatrixLines) {
        Map<String, List<String>> resultMap = new LinkedHashMap<>();
        List<String> keys = new ArrayList<>();
        weightsMatrixLines.forEach(item -> keys.add(item.split(":")[0]));
        keys.forEach(key -> {
            List<String> values = new ArrayList<>();
            weightsMatrixLines.forEach(item -> {
                if (key.equals(item.split(":")[0])) {
                    values.add(item.split(":")[1]);
                }
            });
            resultMap.put(key, values);
        });
        return resultMap;
    }

    private void transferMatrixValuesToArray(List<Integer> matrixIntegerValuesList) {
        int size = getMatrixSize(matrixIntegerValuesList.size());
        matrix = new int[size][size];
        for (int i = 0, k = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = matrixIntegerValuesList.get(k);
                k++;
            }
        }
    }

    private int getMatrixSize(int matrixSize) {
        return (int) Math.sqrt(matrixSize);
    }

    private List<Integer> convertStringValuesToInt(List<String> matrixLinesList) {
        List<Integer> integers = new ArrayList<>();
        matrixLinesList.forEach(
                item -> Arrays.stream(item.split(" "))
                        .forEach(stringValue -> integers.add(Integer.valueOf(stringValue)))
        );
        return integers;
    }
}
