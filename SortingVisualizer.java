import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SortingVisualizer extends JFrame {
    private int[] array;
    private JTextArea outputArea;
    private JButton bubbleSortButton, selectionSortButton, mergeSortButton, quickSortButton, resetButton, pauseButton;
    private SortingPanel sortingPanel;
    private boolean isPaused = false;
    private int delay = 100; // Initial sorting speed delay (in milliseconds)
    private JSlider speedSlider;
    private JTextField arraySizeField;

    public SortingVisualizer() {
        // Initialize the JFrame
        setTitle("Sorting Algorithm Visualizer");
        setSize(1000, 600);  // Increased width for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize the array with 50 elements by default
        array = generateRandomArray(50);
        sortingPanel = new SortingPanel();

        // Create Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 6));
        bubbleSortButton = new JButton("Bubble Sort");
        selectionSortButton = new JButton("Selection Sort");
        mergeSortButton = new JButton("Merge Sort");
        quickSortButton = new JButton("Quick Sort");
        resetButton = new JButton("Reset");
        pauseButton = new JButton("Pause");

        buttonPanel.add(bubbleSortButton);
        buttonPanel.add(selectionSortButton);
        buttonPanel.add(mergeSortButton);
        buttonPanel.add(quickSortButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(pauseButton);

        // Output area for displaying sorting steps
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Input field for changing array size dynamically
        JPanel inputPanel = new JPanel();
        arraySizeField = new JTextField(5);
        arraySizeField.setText("50");  // Default array size
        inputPanel.add(new JLabel("Array Size:"));
        inputPanel.add(arraySizeField);

        // Speed control slider for sorting speed
        speedSlider = new JSlider(1, 1000, delay);
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        inputPanel.add(new JLabel("Speed:"));
        inputPanel.add(speedSlider);

        // Add components to the JFrame
        add(buttonPanel, BorderLayout.NORTH);
        add(sortingPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Add action listeners
        bubbleSortButton.addActionListener(e -> startSorting("bubble"));
        selectionSortButton.addActionListener(e -> startSorting("selection"));
        mergeSortButton.addActionListener(e -> startSorting("merge"));
        quickSortButton.addActionListener(e -> startSorting("quick"));
        resetButton.addActionListener(e -> resetArray());
        pauseButton.addActionListener(e -> togglePause());

        // Initially disable reset and pause buttons
        resetButton.setEnabled(false);
        pauseButton.setEnabled(false);
    }

    // Method to generate a random array
    private int[] generateRandomArray(int size) {
        int[] newArray = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            newArray[i] = rand.nextInt(100) + 1; // Random numbers between 1 and 100
        }
        return newArray;
    }

    // Method to start sorting based on the algorithm selected
    private void startSorting(String algorithm) {
        // Disable sorting buttons to prevent multiple clicks
        bubbleSortButton.setEnabled(false);
        selectionSortButton.setEnabled(false);
        mergeSortButton.setEnabled(false);
        quickSortButton.setEnabled(false);
        resetButton.setEnabled(true); // Enable reset button
        pauseButton.setEnabled(true); // Enable pause button

        // Get speed delay from the slider
        delay = speedSlider.getValue();

        // Sort based on the selected algorithm
        new Thread(() -> {
            switch (algorithm) {
                case "bubble":
                    bubbleSort(array);
                    break;
                case "selection":
                    selectionSort(array);
                    break;
                case "merge":
                    mergeSort(array, 0, array.length - 1);
                    break;
                case "quick":
                    quickSort(array, 0, array.length - 1);
                    break;
            }
            // Re-enable sorting buttons after sorting is finished
            bubbleSortButton.setEnabled(true);
            selectionSortButton.setEnabled(true);
            mergeSortButton.setEnabled(true);
            quickSortButton.setEnabled(true);
        }).start();
    }

    // Pause the sorting
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("Resume");
        } else {
            pauseButton.setText("Pause");
        }
    }

    // Reset the array to its initial state
    private void resetArray() {
        try {
            int newSize = Integer.parseInt(arraySizeField.getText());
            if (newSize < 1 || newSize > 100) {
                JOptionPane.showMessageDialog(this, "Array size must be between 1 and 100.");
                return;
            }
            array = generateRandomArray(newSize);
            outputArea.setText(""); // Clear output area
            repaint(); // Reset the visualization
            resetButton.setEnabled(false); // Disable reset button until next sorting
            pauseButton.setEnabled(false); // Disable pause button
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for array size.");
        }
    }

    // Bubble Sort
    private void bubbleSort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1 && !isPaused; i++) {
            for (int j = 0; j < n - i - 1 && !isPaused; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
                repaint();
                outputArea.append("Step: " + arrayToString(array) + "\n");
                try { Thread.sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    // Selection Sort
    private void selectionSort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1 && !isPaused; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n && !isPaused; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;

            repaint();
            outputArea.append("Step: " + arrayToString(array) + "\n");
            try { Thread.sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    // Merge Sort
    private void mergeSort(int[] array, int left, int right) {
        if (left < right && !isPaused) {
            int mid = (left + right) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    private void merge(int[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2 && !isPaused) {
            if (leftArray[i] <= rightArray[j]) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
        }

        while (i < n1 && !isPaused) {
            array[k++] = leftArray[i++];
        }

        while (j < n2 && !isPaused) {
            array[k++] = rightArray[j++];
        }

        // After merging, you can repaint the visualization and update output area
        repaint();
        outputArea.append("Merged: " + arrayToString(array) + "\n");
        try { Thread.sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // Quick Sort
    private void quickSort(int[] array, int low, int high) {
        if (low < high && !isPaused) {
            int pivotIndex = partition(array, low, high);
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high && !isPaused; j++) {
            if (array[j] < pivot) {
                i++;
                // Swap elements
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
            repaint();
            outputArea.append("Step: " + arrayToString(array) + "\n");
            try { Thread.sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        // Swap the pivot to its correct position
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }

    // Helper method to convert array to string for output display
    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int num : array) {
            sb.append(num).append(" ");
        }
        return sb.toString();
    }

    // Sorting Panel for Visualizing the Array
    private class SortingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / array.length;
            int max = Arrays.stream(array).max().orElse(100); // Max value in array to normalize heights

            // Draw each element as a vertical bar
            for (int i = 0; i < array.length; i++) {
                int barHeight = (int) ((array[i] / (double) max) * (height - 30)); // Normalize height based on panel
                g.setColor(Color.BLUE);
                g.fillRect(i * barWidth, height - barHeight - 20, barWidth - 1, barHeight);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SortingVisualizer visualizer = new SortingVisualizer();
            visualizer.setVisible(true);
        });
    }
}