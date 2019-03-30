import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for validating whether the contents of a memory dump include unencrypted track 1 data. Process is done
 * using procedural programming.
 */
public class MagStripeScanner {

    /**
     * Will validate the .dump magstripe file and return any personal information that could be recovered.
     * @param args the first argument should be the filename of the dump file.
     */
    public static void main(String[] args) throws Exception {
        String dmp = dumpToString(new File(args[0]));
        validate(dmp);
    }

    /**
     * Converts a memory dump file to a single string.
     * @param file the memory dump file.
     * @return a string of the file.
     * @throws Exception
     */
    public static String dumpToString(File file) throws Exception {
        Scanner in = new Scanner(file);
        String dump = "";

        while(in.hasNextLine()) {
            dump += in.nextLine();
        }

        return dump;
    }

    /**
     * Validates a string input by determining whether a specific sequence (defined as a Regex) occurs
     * that represents track 1 data.
     * @param input the string to be validated.
     */
    public static void validate(String input) {
        String track_pattern = "%[A-Z]([0-9]{1,19})\\^([A-Za-z0-9/]{1,26})\\^([0-9]{4})([0-9]{3})[0-9]*?";

        Pattern p = Pattern.compile(track_pattern);

        Matcher count = p.matcher(input);
        int matches = 0;
        while(count.find()) {
            matches++;
        }
        System.out.println("Found " + matches + " track I records in the memory data");

        Matcher m = p.matcher(input);

        int i=1;
        while (m.find()) {
            System.out.println("<Information for record #" + (i++) + ">");
            String acct_num = m.group(1);
            String name = m.group(2);
            String expiration = m.group(3);
            String cvc = m.group(4);

            display_account(acct_num, name, expiration, cvc);
            System.out.println();
        }
    }

    /**
     * Utility function for printing out account details.
     * @param acct_num the account number, in track 1 format.
     * @param name the name, in track 1 format.
     * @param exp the expiration, in track 1 format.
     * @param cvc the cvc code, in track 1 format.
     */
    public static void display_account(String acct_num, String name, String exp, String cvc) {
        // Account Number
        String num_first = acct_num.substring(0,4);
        String num_second = acct_num.substring(4,8);
        String num_third = acct_num.substring(8, 12);
        String num_fourth = acct_num.substring(12, 16);

        // Expiration calculation
        String exp_year = "";
        if(Integer.parseInt(exp.substring(0,2)) < 19) {
            exp_year = "20"+exp.substring(0,2);
        } else {
            exp_year = "19"+exp.substring(0,2);
        }
        String exp_month = exp.substring(2,4);

        System.out.println("Cardholder's Name: " + name);
        System.out.println("Card Number: "+num_first+" "+num_second+" "+num_third+" "+num_fourth);
        System.out.println("Expiration Date: " + exp_month + "/" + exp_year);
        System.out.println("CVC Number: " + cvc);
    }
}
