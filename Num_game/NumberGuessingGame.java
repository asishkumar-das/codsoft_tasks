import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NumberGuessingGame extends JFrame {

    // Game variables
    private int randomNumber;
    private int attempts;
    private int score;
    private int roundsPlayed;
    private int roundsWon;

    private final int MAX_ATTEMPTS = 7;

    // GUI components
    private JLabel attemptLabel;
    private JLabel scoreLabel;
    private JLabel roundLabel;
    private JLabel resultLabel;
    private JTextField guessField;
    private JButton guessButton;
    private JButton newRoundButton;

    private Random random = new Random();

    public NumberGuessingGame() {

        setTitle("Number Guessing Game");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createGUI();
        startNewRound();

        setVisible(true);
    }

    // ================= CREATE GUI =================

    private void createGUI() {

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // ================= HEADER =================

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(37, 99, 235));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel("NUMBER GUESSING GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Guess the number between 1 and 100");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================= CENTER =================

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));

        attemptLabel = new JLabel("Attempt: 0 / 7");
        attemptLabel.setFont(new Font("Arial", Font.BOLD, 17));
        attemptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(attemptLabel);
        centerPanel.add(Box.createVerticalStrut(25));

        JLabel instructionLabel = new JLabel("Enter your guess");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(instructionLabel);
        centerPanel.add(Box.createVerticalStrut(8));

        // Guess field
        guessField = new JTextField();
        guessField.setFont(new Font("Arial", Font.BOLD, 22));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setMaximumSize(new Dimension(250, 50));

        centerPanel.add(guessField);
        centerPanel.add(Box.createVerticalStrut(15));

        // Guess button
        guessButton = new JButton("GUESS");
        guessButton.setFont(new Font("Arial", Font.BOLD, 15));
        guessButton.setForeground(Color.WHITE);
        guessButton.setBackground(new Color(37, 99, 235));
        guessButton.setFocusPainted(false);
        guessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessButton.setPreferredSize(new Dimension(150, 45));
        guessButton.setMaximumSize(new Dimension(150, 45));

        centerPanel.add(guessButton);
        centerPanel.add(Box.createVerticalStrut(20));

        // Result
        resultLabel = new JLabel("Make your first guess!");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(resultLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ================= BOTTOM =================

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(245, 247, 250));

        // Statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createLineBorder(
                new Color(220, 224, 230)));

        roundLabel = new JLabel("Round: 0", SwingConstants.CENTER);
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);

        JLabel wonLabel = new JLabel("Won: 0", SwingConstants.CENTER);

        roundLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        wonLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(roundLabel);
        statsPanel.add(scoreLabel);
        statsPanel.add(wonLabel);

        bottomPanel.add(statsPanel, BorderLayout.NORTH);

        // New Round button
        newRoundButton = new JButton("NEW ROUND");
        newRoundButton.setFont(new Font("Arial", Font.BOLD, 14));
        newRoundButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(newRoundButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ================= BUTTON ACTIONS =================

        guessButton.addActionListener(e -> checkGuess());

        newRoundButton.addActionListener(e -> startNewRound());

        // Press Enter to guess
        guessField.addActionListener(e -> checkGuess());
    }

    // ================= START NEW ROUND =================

    private void startNewRound() {

        randomNumber = random.nextInt(100) + 1;

        attempts = 0;

        roundsPlayed++;

        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);

        attemptLabel.setText("Attempt: 0 / " + MAX_ATTEMPTS);

        resultLabel.setText("Make your first guess!");
        resultLabel.setForeground(Color.DARK_GRAY);

        roundLabel.setText("Round: " + roundsPlayed);

        guessField.requestFocus();
    }

    // ================= CHECK GUESS =================

    private void checkGuess() {

        String input = guessField.getText().trim();

        if (input.isEmpty()) {

            resultLabel.setText("Please enter a number!");
            resultLabel.setForeground(Color.RED);
            return;
        }

        int guess;

        try {

            guess = Integer.parseInt(input);

        } catch (NumberFormatException e) {

            resultLabel.setText("Enter a valid number!");
            resultLabel.setForeground(Color.RED);
            return;
        }

        // Range validation
        if (guess < 1 || guess > 100) {

            resultLabel.setText("Enter a number between 1 and 100!");
            resultLabel.setForeground(Color.RED);
            return;
        }

        attempts++;

        attemptLabel.setText(
                "Attempt: " + attempts + " / " + MAX_ATTEMPTS
        );

        // ================= CORRECT =================

        if (guess == randomNumber) {

            roundsWon++;

            score = calculateScore(attempts);

            resultLabel.setText(
                    "Correct! You won +" + score + " points!"
            );

            resultLabel.setForeground(new Color(22, 163, 74));

            scoreLabel.setText("Score: " + score);

            guessField.setEnabled(false);
            guessButton.setEnabled(false);

            showWinMessage();

        }

        // ================= TOO LOW =================

        else if (guess < randomNumber) {

            resultLabel.setText("Too Low! Try a higher number.");
            resultLabel.setForeground(new Color(234, 88, 12));

        }

        // ================= TOO HIGH =================

        else {

            resultLabel.setText("Too High! Try a lower number.");
            resultLabel.setForeground(new Color(220, 38, 38));
        }

        // ================= GAME OVER =================

        if (attempts >= MAX_ATTEMPTS && guess != randomNumber) {

            resultLabel.setText(
                    "Game Over! Number was " + randomNumber
            );

            resultLabel.setForeground(Color.RED);

            guessField.setEnabled(false);
            guessButton.setEnabled(false);

            JOptionPane.showMessageDialog(
                    this,
                    "You used all 7 attempts.\n"
                            + "The correct number was: "
                            + randomNumber,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        guessField.setText("");
        guessField.requestFocus();
    }

    // ================= CALCULATE SCORE =================

    private int calculateScore(int attempts) {

        switch (attempts) {

            case 1:
                return 100;

            case 2:
                return 90;

            case 3:
                return 80;

            case 4:
                return 70;

            case 5:
                return 60;

            case 6:
                return 50;

            case 7:
                return 40;

            default:
                return 0;
        }
    }

    // ================= WIN MESSAGE =================

    private void showWinMessage() {

        JOptionPane.showMessageDialog(
                this,
                "Congratulations!\n\n"
                        + "You guessed the correct number.\n"
                        + "Attempts: " + attempts + "\n"
                        + "Points Earned: " + score,
                "You Won!",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new NumberGuessingGame();
        });
    }
}