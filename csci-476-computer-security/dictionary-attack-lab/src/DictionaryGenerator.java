import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class DictionaryGenerator {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        char[] digits = {'0', '1','2','3','4','5','6','7','8','9'};
        char[] lowercase = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        char[] uppercase = new char[26];
        char[] special = {'$', '!', '@', '#', '&'};
        for(int i=0; i<lowercase.length; i++) {
            uppercase[i] = Character.toUpperCase(lowercase[i]);
        }

        char[] alphabet = digits;
//        char[] alphabet = new char[31];
//        for(int i=0; i<alphabet.length; i++) {
//            if(i < 26) {
//                alphabet[i] = lowercase[i];
//            } else {
//                alphabet[i] = special[i-26];
//            }
//        }
        int minLength = 6;
        int maxLength = 6;
        String filename = "dates.txt";

        PrintWriter writer = new PrintWriter(filename, "UTF-8");

        for(int length=minLength; length<=maxLength; length++) {
            System.out.println("starting length="+length+"...");
            permute(length, "", alphabet, writer);
        }

        writer.close();
    }

    public static void permute(int maxLength, String sofar, char[] alphabet, PrintWriter writer) {
        if(sofar.length() == maxLength) {
            writer.println(sofar);
            return;
        }

        for(char symbol: alphabet) {
            permute(maxLength, sofar+Character.toString(symbol), alphabet, writer);
        }
    }
}
