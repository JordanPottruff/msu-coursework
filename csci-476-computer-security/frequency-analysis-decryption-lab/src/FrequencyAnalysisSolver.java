import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Provides functionality for conducting character frequency analysis on a monoalphabetic ciphertext message. In
 * addition to detailing frequency counts, it also has tools to make 'modification rules' that transform one character
 * to another, essentially allowing inferences to be checked in real time. Furthermore, these inferences are maintained
 * separately to the original message, so that statistics and data may still be viewed during the inference process.
 */
public class FrequencyAnalysisSolver {
    private final String original;
    private ArrayList<Character[]> rules;

    /**
     * Creates an instance that is wrapped around a specific ciphertext.
     * @param ciphertext
     * @throws IOException
     */
    public FrequencyAnalysisSolver(String ciphertext) throws IOException {
        original = ciphertext;
        rules = new ArrayList<>();
    }

    /**
     * Returns the original ciphertext message.
     * @return original ciphertext.
     */
    public String getOriginalCiphertext() {
        return original;
    }

    /**
     * Returns the new modified message, created from applying the modification rules to the ciphertext.
     * @return the modified message.
     */
    public String getModified() {
        String result = "";
        for(int i=0; i<original.length(); i++) {
            char cur = original.charAt(i);

            int ascii = (int) cur;

            if(ascii < 97 || ascii > 122) {
                result += cur;
                continue;
            }

            boolean found = false;
            for(Character[] rule: rules) {
                if(rule[0] == cur) {
                    result += rule[1];
                    found = true;
                }
            }

            if(!found) result += cur;
        }

        return result;
    }

    /**
     * Adds a new rule, where each rule is a mapping of some character in the ciphertext to a different character in
     * the modified output.
     * @param original the character to be changed from the ciphertext.
     * @param modified the character to change it to.
     */
    public void addRule(char original, char modified) {
        removeRule(original);
        rules.add(new Character[]{original, modified});
    }

    /**
     * Removes the rule that applies to the specified character in the ciphertext.
     * @param original the character to be unmapped.
     */
    public void removeRule(char original) {
        ArrayList<Character[]> newRules = new ArrayList<>();
        for(Character[] rule: rules) {
            if(rule[0] != original) {
                newRules.add(rule);
            }
        }
        rules = newRules;
    }

    /**
     * Returns a formatted string of the current rules in place.
     * @return the formatted string.
     */
    public String displayRules() {
        String output = "";
        for(Character[] rule: rules) {
            output += String.format("(%c -> %c)\n", rule[0], rule[1]);
        }
        return output;
    }

    /**
     * Returns a map of each character to its frequency in the original ciphertext.
     * @return a hashmap where the key is each character in the ciphertext and the value is an integer that represents
     * the number of occurences of that character in the ciphertext.
     */
    public HashMap<Character, Integer> getCharFrequencies() {
        HashMap<Character, Integer> counter = new HashMap<>();
        for(int i=0; i<original.length(); i++) {
            char cur = original.charAt(i);

            int ascii = (int) cur;

            if(ascii < 97 || ascii > 122) {
                continue;
            }

            if(counter.containsKey(cur)) {
                int count = counter.get(cur);
                counter.put(cur, count+1);
            } else {
                counter.put(cur, 1);
            }
        }
        return counter;
    }

    /**
     * Returns a formatted string that displays the frequencies of each character in the ciphertext as a proportion.
     * @return the formatted string.
     */
    public String displayCharFrequencies() {
        HashMap<Character, Integer> freqs = getCharFrequencies();

        int total = 0;
        for(char c: freqs.keySet()) {
            total += freqs.get(c);
        }

        String result = "";

        for(char c: freqs.keySet()) {
            int num = freqs.get(c);
            float percentage = 100 * (float) num/total;
            result += String.format("%c: %.1f%%\n", c, percentage);
        }

        return result;

    }

    /**
     * A simple program that allows a user to try out the FrequencyAnalysisSolver class through guided instructions
     * and user inputs.
     * @param args additional program arguments - disregarded.
     * @throws Exception will throw an exception if file is not found.
     */
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter filename (including relative path): ");
        String ciphertext = new String(Files.readAllBytes(Paths.get(in.nextLine())));

        FrequencyAnalysisSolver solver = new FrequencyAnalysisSolver(ciphertext);

        int selection = -1;
        do {
            System.out.println("\n-------------------------------------------");
            System.out.println("Enter the number of the desired operation: ");
            System.out.println(" 1 Display original ciphertext message");
            System.out.println(" 2 Display modified message");
            System.out.println(" 3 Display ciphertext character frequencies");
            System.out.println(" 4 Display modification rules");
            System.out.println(" 5 Add modification rule");
            System.out.println(" 6 Remove modification rule");
            System.out.println(" -1 Exit program");
            System.out.print(">");
            selection = getInt(in);

            switch(selection) {
                case -1:
                    return;
                case 1:
                    System.out.println("\nORIGINAL CIPHERTEXT MESSAGE:");
                    System.out.println(solver.getOriginalCiphertext());
                    break;
                case 2:
                    System.out.println("\nCURRENT MODIFIED MESSAGE:");
                    System.out.println(solver.getModified());
                    break;
                case 3:
                    System.out.println("\nCIPHERTEXT CHARACTER FREQUENCIES:");
                    System.out.println(solver.displayCharFrequencies());
                    break;
                case 4:
                    System.out.println("\nMODIFICATION RULES:");
                    System.out.println(solver.displayRules());
                    break;
                case 5:
                    Character[] newRule = getNewRule(in);
                    solver.addRule(newRule[0], newRule[1]);
                    break;
                case 6:
                    Character charOfRule = deleteRule(in);
                    solver.removeRule(charOfRule);
                    break;
                default:
                    System.out.println("INVALID RESPONSE");
            }

        } while(true); // Will exit on sentinel condition above.
    }

    // Handles user input for adding a new rule.
    private static Character[] getNewRule(Scanner in) {
        System.out.println("\nEnter the character you would like to change:");
        System.out.print(">");
        char original = getChar(in);
        System.out.println(String.format("Changing character %c to which character?", original));
        System.out.print(">");
        char modified = getChar(in);
        System.out.println(String.format("NEW RULE ADDED: %c will be changed to %c!", original, modified));

        return new Character[]{original, modified};
    }

    // Handles user input for deleting a rule.
    private static char deleteRule(Scanner in) {
        System.out.println("\nEnter the original character of the rule you would like to remove:");
        System.out.print(">");
        char original = getChar(in);
        System.out.println(String.format("RULE REMOVED: if any rule for %c is found, it will be removed", original));
        return original;
    }

    // Handles user input for single characters.
    private static char getChar(Scanner in) {
        char nextChar;
        String input = in.nextLine().trim();
        while(input.length() != 1) {
            System.out.println("ERROR: please enter a single character:");
            System.out.print(">");
            input = in.nextLine().trim();
        }
        return input.charAt(0);
    }

    // Handles user input for getting integers.
    private static int getInt(Scanner in) {
        String input = in.nextLine().trim();
        while(!isInteger(input)) {
            System.out.println("ERROR: please enter a single digit:");
            System.out.print(">");
            input = in.nextLine().trim();
        }
        return parseInt(input);
    }

    // Determines whether a given string can be parsed as an integer (white space WILL cause 'false').
    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
