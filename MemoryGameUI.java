import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MemoryGameUI extends JFrame {
    private JTextField[] numberFields;
    private JLabel numberLabel, scoreLabel;
    private Timer displayTimer;
    private int[] sequence;
    private int currentScore = 0;
    private JTable leaderboardTable;
    private String playerName;
    private long startTime;
    private String[][] leaderboardData = new String[1][3]; // To store the player's data
    private String[] leaderboardColumns = { "Player", "Highest Score", "Time" };
    private JComboBox<String> difficultyComboBox;
    private JButton startButton;
    private JPanel inputPanel;
    private int numFields = 6; // Default to Medium

    public MemoryGameUI() {
        setTitle("Memory Game");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left side (Game Area)
        JPanel gamePanel = new JPanel(new GridLayout(6, 1));
        add(gamePanel, BorderLayout.CENTER);

        // Display Level and Score
        JPanel levelPanel = new JPanel(new GridLayout(1, 1));
        String[] difficultyLevels = { "Easy", "Medium", "Hard" };
        difficultyComboBox = new JComboBox<>(difficultyLevels);
        difficultyComboBox.setSize(5, 5);
        ;
        levelPanel.add(difficultyComboBox);
        scoreLabel = new JLabel("Score: 0", JLabel.CENTER);
        Font font = new Font("Arial", Font.BOLD, 20);
        scoreLabel.setFont(font);
        levelPanel.add(scoreLabel);
        gamePanel.add(levelPanel);

        // Number Display
        numberLabel = new JLabel("Click 'Start' to begin the game", JLabel.CENTER);
        Font font1 = new Font("Arial", Font.BOLD, 20);
        numberLabel.setFont(font1);
        gamePanel.add(numberLabel);

        // Input Fields for user entry (adjusted dynamically)
        inputPanel = new JPanel(new GridLayout(1, numFields));
        gamePanel.add(inputPanel);

        // Start Button
        startButton = new JButton("Start");
        gamePanel.add(startButton);
        startButton.addActionListener(e -> startGame());

        // Submit Button
        JButton submitButton = new JButton("Submit");
        gamePanel.add(submitButton);

        // Submit button action
        submitButton.addActionListener(e -> checkInput());

        // Right side (Leaderboard)
        JPanel leaderboardPanel = new JPanel(new BorderLayout());
        add(leaderboardPanel, BorderLayout.EAST);

        JLabel leaderboardLabel = new JLabel("Leaderboard", JLabel.CENTER);
        leaderboardPanel.add(leaderboardLabel, BorderLayout.NORTH);

        leaderboardTable = new JTable(leaderboardData, leaderboardColumns);
        leaderboardPanel.add(new JScrollPane(leaderboardTable), BorderLayout.CENTER);

        // Ask player for their name before starting the game
        playerName = JOptionPane.showInputDialog("Enter your name: ");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player 1"; // Default name if not provided
        }

        setVisible(true);
    }

    // Start the game based on the difficulty level
    private void startGame() {
        // Disable the start button after pressing it
        startButton.setEnabled(false);

        String difficulty = (String) difficultyComboBox.getSelectedItem();

        switch (difficulty) {
            case "Easy":
                numFields = 5;
                break;
            case "Medium":
                numFields = 6;
                break;
            case "Hard":
                numFields = 7;
                break;
        }

        updateInputFields(); // Adjust the number of input fields

        int numDigits = 2; // Number of digits for each number
        int time = 5000; // Time the sequence is shown (milliseconds)

        // Track the start time when game begins
        startTime = System.currentTimeMillis();

        generateSequence(numDigits);
        displaySequence(time);
    }

    // Update input fields based on difficulty
    private void updateInputFields() {
        inputPanel.removeAll(); // Clear old input fields
        numberFields = new JTextField[numFields];
        inputPanel.setLayout(new GridLayout(1, numFields));
        for (int i = 0; i < numFields; i++) {
            numberFields[i] = new JTextField(2);
            inputPanel.add(numberFields[i]);
        }
        inputPanel.revalidate(); // Refresh panel to show new input fields
        inputPanel.repaint();
    }

    // Generate random sequence based on difficulty
    private void generateSequence(int numDigits) {
        sequence = new int[numFields]; // Adjust number of values based on difficulty
        Random rand = new Random();
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = rand.nextInt((int) Math.pow(10, numDigits)); // Random 2-digit numbers
        }

        // Display the sequence for a brief moment
        StringBuilder sb = new StringBuilder();
        for (int num : sequence) {
            sb.append(num).append(" ");
        }
        numberLabel.setText(sb.toString());
    }

    // Display the sequence for a few seconds and hide it
    private void displaySequence(int time) {
        // Show the sequence for a few seconds, then hide it
        displayTimer = new javax.swing.Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numberLabel.setText("");
            }
        });
        displayTimer.setRepeats(false); // Only repeat once
        displayTimer.start();
    }

    // Check the user's input
    private void checkInput() {
        boolean correct = true;
        for (int i = 0; i < numberFields.length; i++) {
            try {
                int enteredNumber = Integer.parseInt(numberFields[i].getText());
                if (enteredNumber != sequence[i]) {
                    correct = false;
                    break;
                }
            } catch (NumberFormatException ex) {
                correct = false;
                break;
            }
        }

        if (correct) {
            currentScore += 10; // Increase score for correct answer
            scoreLabel.setText("Score: " + currentScore);
            JOptionPane.showMessageDialog(this, "Correct Sequence! Next sequence coming up...");
            startGame(); // Start next sequence immediately
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect Sequence! Try again.");
            showLeaderboard();
            resetGame();
        }
    }

    // Show the leaderboard
    private void showLeaderboard() {
        // Calculate total time taken
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / 1000; // Time in seconds

        // Update leaderboard with player's info
        leaderboardData[0][0] = playerName;
        leaderboardData[0][1] = String.valueOf(currentScore);
        leaderboardData[0][2] = timeTaken + " sec";

        // Update leaderboard table
        leaderboardTable.setModel(new javax.swing.table.DefaultTableModel(leaderboardData, leaderboardColumns));

        JOptionPane.showMessageDialog(this, "Game Over! Check the leaderboard.");
    }

    // Reset the game for the next round
    private void resetGame() {
        currentScore = 0;
        scoreLabel.setText("Score: 0");
        startButton.setEnabled(true);
    }

    public static void main(String[] args) {
        new MemoryGameUI();
    }
}
