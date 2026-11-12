import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/*
 * Word Guessing Game (Console based)
 * Subject : Object Oriented Programming through Java
 * Concepts: Classes & Objects, Constructors, Encapsulation, Inheritance,
 *           Abstract Class, Interface, Polymorphism, Exception Handling,
 *           Strings, Arrays, Collections (ArrayList, HashSet), Scanner
 */

// ---------- Interface ----------
interface Scoring {
    int calculateScore(int attemptsLeft, int wordLength);
}

// ---------- User defined exception ----------
class InvalidGuessException extends Exception {
    public InvalidGuessException(String message) {
        super(message);
    }
}

// ---------- Class representing one word with its hint ----------
class Word {
    private final String text;
    private final String hint;

    public Word(String text, String hint) {
        this.text = text.toLowerCase();
        this.hint = hint;
    }

    public String getText() { return text; }
    public String getHint() { return hint; }
    public int length()     { return text.length(); }
}

// ---------- Stores all words ----------
class WordBank {
    private final List<Word> words = new ArrayList<>();
    private final Random random = new Random();

    public WordBank() {
        words.add(new Word("java", "A popular programming language"));
        words.add(new Word("class", "Blueprint of an object"));
        words.add(new Word("object", "Instance of a class"));
        words.add(new Word("inheritance", "Acquiring properties of a parent class"));
        words.add(new Word("interface", "Contains abstract methods, implemented by classes"));
        words.add(new Word("polymorphism", "One name, many forms"));
        words.add(new Word("encapsulation", "Wrapping data and methods together"));
        words.add(new Word("constructor", "Special method that initializes an object"));
        words.add(new Word("exception", "An unwanted event during program execution"));
        words.add(new Word("package", "Group of related classes"));
    }

    public Word getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }
}

// ---------- Abstract class ----------
abstract class Player {
    protected String name;
    protected int totalScore;

    public Player(String name) {
        this.name = name;
        this.totalScore = 0;
    }

    public abstract String getTitle();   // implemented by subclasses

    public void addScore(int points) { totalScore += points; }
    public String getName()          { return name; }
    public int getTotalScore()       { return totalScore; }
}

// ---------- Inheritance ----------
class HumanPlayer extends Player {
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public String getTitle() {
        return "Player";
    }
}

// ---------- One round of the game ----------
class GameRound implements Scoring {
    private static final int MAX_ATTEMPTS = 6;

    private final Word word;
    private final char[] display;
    private final Set<Character> guessedLetters = new HashSet<>();
    private int attemptsLeft = MAX_ATTEMPTS;

    public GameRound(Word word) {
        this.word = word;
        this.display = new char[word.length()];
        for (int i = 0; i < display.length; i++) {
            display[i] = '_';
        }
    }

    // Validates input; throws a user defined exception for bad input
    public void processGuess(String input) throws InvalidGuessException {
        input = input.trim().toLowerCase();

        if (input.isEmpty()) {
            throw new InvalidGuessException("Input cannot be empty.");
        }
        if (!input.matches("[a-z]+")) {
            throw new InvalidGuessException("Use alphabets only.");
        }

        if (input.length() == 1) {
            guessLetter(input.charAt(0));
        } else if (input.length() == word.length()) {
            guessWholeWord(input);
        } else {
            throw new InvalidGuessException(
                "Enter one letter or the full word (" + word.length() + " letters).");
        }
    }

    private void guessLetter(char ch) throws InvalidGuessException {
        if (guessedLetters.contains(ch)) {
            throw new InvalidGuessException("You already tried '" + ch + "'.");
        }
        guessedLetters.add(ch);

        boolean found = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.getText().charAt(i) == ch) {
                display[i] = ch;
                found = true;
            }
        }

        if (found) {
            System.out.println("Good guess!");
        } else {
            attemptsLeft--;
            System.out.println("Wrong guess!");
        }
    }

    private void guessWholeWord(String guess) {
        if (guess.equals(word.getText())) {
            for (int i = 0; i < display.length; i++) {
                display[i] = word.getText().charAt(i);
            }
        } else {
            attemptsLeft--;
            System.out.println("That is not the word!");
        }
    }

    public boolean isWon()  { return new String(display).equals(word.getText()); }
    public boolean isLost() { return attemptsLeft <= 0; }
    public boolean isOver() { return isWon() || isLost(); }

    public void showStatus() {
        StringBuilder sb = new StringBuilder();
        for (char c : display) {
            sb.append(c).append(' ');
        }
        System.out.println("\nWord     : " + sb.toString().trim());
        System.out.println("Hint     : " + word.getHint());
        System.out.println("Tried    : " + guessedLetters);
        System.out.println("Attempts : " + attemptsLeft);
    }

    public Word getWord()         { return word; }
    public int getAttemptsLeft()  { return attemptsLeft; }

    // Interface method implementation
    @Override
    public int calculateScore(int attemptsLeft, int wordLength) {
        return (attemptsLeft * 10) + wordLength;
    }
}

// ---------- Main class ----------
public class WordGuessingGame {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        WordBank bank = new WordBank();

        System.out.println("=================================");
        System.out.println("      WORD GUESSING GAME");
        System.out.println("=================================");
        System.out.print("Enter your name: ");
        Player player = new HumanPlayer(sc.nextLine().trim());
        System.out.println("Welcome, " + player.getName() + "! You have 6 attempts per word.");
        System.out.println("Guess one letter at a time, or the full word.");

        String again;
        do {
            GameRound round = new GameRound(bank.getRandomWord());

            while (!round.isOver()) {
                round.showStatus();
                System.out.print("Your guess: ");
                try {
                    round.processGuess(sc.nextLine());
                } catch (InvalidGuessException e) {
                    System.out.println("Invalid: " + e.getMessage());
                }
            }

            if (round.isWon()) {
                int points = round.calculateScore(round.getAttemptsLeft(),
                                                  round.getWord().length());
                player.addScore(points);
                System.out.println("\nCorrect! The word is '" + round.getWord().getText() + "'.");
                System.out.println("You earned " + points + " points.");
            } else {
                System.out.println("\nGame over! The word was '" + round.getWord().getText() + "'.");
            }

            System.out.println(player.getTitle() + " " + player.getName()
                    + " | Total score: " + player.getTotalScore());
            System.out.print("\nPlay again? (y/n): ");
            again = sc.nextLine().trim().toLowerCase();
        } while (again.equals("y"));

        System.out.println("\nThanks for playing, " + player.getName() + "!");
        System.out.println("Final score: " + player.getTotalScore());
        sc.close();
    }
}
