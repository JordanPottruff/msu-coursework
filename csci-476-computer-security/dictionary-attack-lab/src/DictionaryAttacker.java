import java.io.File;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * AUTHOR: Jordan Pottruff
 * DESCRIPTION: this class is capable of breaking password hashes using any dictionary list stored in a text file or
 * other format capable of being used by a Scanner.
 */

public class DictionaryAttacker {
    /**
     * This is a private class that acts as a value class in order to store relevant data about the results of our
     * dictionary attack in object form.
     */
    private class AttackResults {
        private final String hash;
        private final String pass;
        private final double time;

        public AttackResults(String hash, String pass, Duration time) {
            this.hash = hash;
            this.pass = pass;
            this.time = time.toMillis()/1000.0f;
        }

        public String toString() {
            return String.format("The password for hash value %s is %s, it takes the program %.3f sec to recover this password", hash, pass, time);
        }
    }

    private final Scanner hashIn;
    private final Scanner dictIn;
    private final MessageDigest md;

    /**
     * Creates an attacker from the scanner for the hash and dictionary files, along with a MessageDigest object that
     * implements the relevant hashing method (in our case, MD5).
     * @param hashIn scanner for hash values.
     * @param dictIn scanner for dictionary passwords to attempt.
     * @param md the message digest class that will hash our passwords.
     */
    public DictionaryAttacker(Scanner hashIn, Scanner dictIn, MessageDigest md) {
        this.hashIn = hashIn;
        this.dictIn = dictIn;
        this.md = md;
    }

    /**
     * Runs the dictionary attack using the dependencies passed to the constructor. Returns a list of results where
     * each index corresonds to the results of one of the hashes.
     *
     * @return the results, encapsulated in a list of AttackResult objects. Hashes that could not be broken will not
     * be included in the list.
     */
    public ArrayList<AttackResults> attack() {

        // Scan in the list of hashes into an ArrayList. These will be removed as their corresponding passwords are
        // discovered.
        ArrayList<String> hashList = new ArrayList<>();
        while(hashIn.hasNextLine()) {
            hashList.add(hashIn.nextLine());
        }

        // Prepare an array list to store the results as they are found for each hash.
        ArrayList<AttackResults> results = new ArrayList<>();

        // Track the starting point so that we can include the duration required to find the results.
        Instant start = Instant.now();

        // Loop through the items in the password dictionary...
        while(dictIn.hasNextLine() && !hashList.isEmpty()) {
            // Save each password in both original and hashed form.
            String pass = dictIn.nextLine();
            String passHashed = hash(pass);

            // Now iterate over the (remaining) hashes in the hashList...
            for(String hash: hashList) {
                // If the hash of the password equals the hash we are looking for, save that result...
                if(hash.equals(passHashed)) {
                    System.out.println(String.format("Discovered hash for %s, %d remaining...", hash, hashList.size()));
                    results.add(new AttackResults(hash, pass, Duration.between(start, Instant.now())));
                    hashList.remove(hash);
                    break;
                }
            }
        }
        // Return the list of results.
        return results;
    }

    // Hashes the given value using md5.
    private String hash(String password) {
        md.reset();
        md.update(password.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append("0123456789abcdef".charAt((b & 0xF0) >> 4));
            sb.append("0123456789abcdef".charAt((b & 0x0F)));
        }
        return sb.toString();
    }

    /**
     * The DictionaryAttacker can be ran by configuring the arguments passed to the following main method. The first
     * argument is the filename (including the relative path) of the file that lists the md5hashes that you wish to
     * break, where each hash is on a new line. The second argument is the filename (again, including the relative path)
     * of the password dictionary, where each password is listed on a new line.
     *
     * @param args the first two arguments are used to pass the filenames as detailed above.
     * @throws Exception if MD5 is not found or if the files are not found.
     */
    public static void main(String[] args) throws Exception {
        // Gather the filenames
        String md5HashesFilename = args[0];
        String dictionaryFilename = args[1];

        // Create the dependencies: scanned hash file, scanned dictionary, and MessageDigest for hashing.
        Scanner md5Hashes = new Scanner(new File(md5HashesFilename));
        Scanner dictionary = new Scanner(new File(dictionaryFilename));
        MessageDigest digest = MessageDigest.getInstance("MD5");

        // Create the dictionary attacker.
        DictionaryAttacker attacker = new DictionaryAttacker(md5Hashes, dictionary, digest);

        // Run the attack, printing the results.
        for(AttackResults result: attacker.attack()) {
            System.out.println(result);
        }
    }
}
