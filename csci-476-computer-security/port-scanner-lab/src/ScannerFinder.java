/**
 * ASSIGNMENT #4 - JORDAN POTTRUFF
 * 
 * PLEASE NOTE ON FILE OUTPUT:
 * If any address has received 0 SYN-ACKs, then any number of sent SYNs greater
 * than 0 will cause the program to recognize the address as an attacker. The
 * specifications for this situation were not clarified in the assignment text,
 * so I wanted to make this clear in my implementation. Also, the output of the
 * program is sent to standard out by default, which can then be piped to a 
 * file of the user's choice.
 */

package scannerfinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 * The purpose of the ScannerFinder class is to analyze a pcap file to determine
 * which IP addresses are potentially unleashing SYN scan attacks. The metric 
 * for determining whether a SYN scan is taking place is a simple threshold: if
 * the number of SYN packets sent is more than 3 times the number of received
 * SYN-ACKs, then the IP address is considered an attacker.
 * @author Jordan
 */
public class ScannerFinder {
    // The filename to be scanned.
    private final String filename;
    
    public ScannerFinder(String filename) {
        this.filename = filename;
    }
    
    /**
     * Conducts the scan on the file, returning a set of IP addresses that are
     * considered attackers.
     * @return 
     */
    public Set<String> scan() {
        // Open file through pcap library, displaying error information if 
        // necessary.
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Pcap.openOffline(filename, errbuf);
        if(pcap == null) {
            throw new IllegalArgumentException(errbuf.toString());
        }
        
        // Create a handler that processes the incoming packets being found in
        // the pcap file by the pcap library.
        ScannerPacketHandler handler = new ScannerPacketHandler();
        pcap.loop(Pcap.LOOP_INFINATE, handler, null);
        pcap.close();
        
        // Grab the list of attackers from the handler object once the scan is
        // complete.
        HashMap<String, SynRecord> synRecords = handler.getRecord();
        return identifySynScanAttackers(synRecords);
    }
    
    /**
     * Determines whether the record of SYN packets indicates any malicious 
     * addresses. If so, these addresses are compiled into a list to be sent
     * to the caller.
     * @param records the TCP-SYN information from the pcap file.
     * @return a set of attacker IP addresses.
     */
    private Set<String> identifySynScanAttackers(HashMap<String, SynRecord> records) {
        // Record attackers as a hash set.
        Set<String> attackers = new HashSet<>();
        
        // Iterate over all addresses, using our metric to check for malicious
        // activity.
        for(String address: records.keySet()) {
            int sentSYNs = records.get(address).getSYNs();
            int receivedACKs = records.get(address).getSYNACKs();
            
            // Consider malicious if sent more than 3 times the SYNs than
            // SYN-ACKs received.
            if(sentSYNs > 3*receivedACKs) {
                attackers.add(address);
            }
        }
        return attackers;
    }
    
    // Program is ran by calling the java file with the first argument as the 
    // filename of the pcap file, including the relative file path.
    public static void main(String[] args) throws Exception {
        // Create finder, run scan, and store attacker information.
        ScannerFinder scanner = new ScannerFinder(args[0]);
        Set<String> attackers = scanner.scan();
        
        // Display attacker addresses on each line.
        for(String attacker: attackers) {
            System.out.println(attacker);
        }
    }
    
    /**
     * When packets are being read from the pcap file, the pcap library requires
     * a handler to be used to deal with each packet ("nextPacket"). This class
     * implements the required interface and adds additional methods for 
     * processing the packets.
     */
    public static class ScannerPacketHandler implements PcapPacketHandler {
        // These are used to represent the relevant protocols.
        private final Ip4 ip = new Ip4();
        private final Tcp tcp = new Tcp();
        // This stores the relevant details of our SYN packets.
        private final HashMap<String, SynRecord> synRecords = new HashMap<>();

        // Determines whether a packet is a TCP packet.
        private boolean isTCP(PcapPacket packet) {
            return packet.hasHeader(tcp);
        }

        // Determines whether a packet is a TCP packet with the SYN flag on.
        private boolean isSYN(PcapPacket packet) {
            if(!isTCP(packet)) return false;
            Tcp tcpPacket = packet.getHeader(tcp);
            return tcpPacket.flags_SYN();
        }

        // Determines whether a packet is a TCP packet with the SYN and ACK 
        // flags on.
        private boolean isSYNACK(PcapPacket packet) {
            if(!isSYN(packet)) return false;
            Tcp tcpPacket = packet.getHeader(tcp);
            return tcpPacket.flags_ACK();
        }

        /**
         * Filters for TCP SYN packets and stores their information based on
         * whether it is a SYN or SYN-ACK.
         * @param packet the next packet to be processed.
         * @param user 
         */
        public void nextPacket(PcapPacket packet, Object user) {
            // Ignore if packet is not TCP...
            if(isTCP(packet)) {
                // Ignore if TCP packet does not have SYN flag set...
                if(isSYN(packet)) {
                    // Convert packet into IPv4 packet for additional methods.
                    Ip4 ipPacket = packet.getHeader(ip);

                    // Grab source and destination addresses.
                    String srcIP = FormatUtils.ip(ipPacket.source());
                    String dstIP = FormatUtils.ip(ipPacket.destination());

                    // Create records for each address if they don't already
                    // exist.
                    if(!synRecords.containsKey(srcIP)) {
                        synRecords.put(srcIP, new SynRecord(0,0));
                    }
                    if(!synRecords.containsKey(dstIP)) {
                        synRecords.put(dstIP, new SynRecord(0,0));
                    }

                    // Grab the record (may have just been created) for each
                    // address.
                    SynRecord srcRec = synRecords.get(srcIP);
                    SynRecord dstRec = synRecords.get(dstIP);
                    
                    // Is the packet a SYN-ACK or just a SYN?
                    if(isSYNACK(packet)) {
                        // If a SYN-ACK, add it to the destination's received
                        // count.
                        dstRec.receieveSYNACK();
                    } else {
                        // If just a SYN, add it to the source's sent count.
                        srcRec.sendSYN();
                    }

                }
            }
        }
        
        /**
         * Once the packets have been processed by this handler, this method
         * will return the relevant details as a HashMap. 
         * @return a HashMap where the key is an IP address and the value is the
         * record of SYN-ACKs received and SYNs sent for that address.
         */
        private HashMap<String, SynRecord> getRecord() {
            return synRecords;
        }
    }
    
    /**
     * Helper class that maintains information about the number of SYNs sent and
     * SYN-ACKs received, making the use of a HashMap easier.
     */
    private static class SynRecord{
        private int sentSYNs;
        private int receivedSYNACKs;
        
        public SynRecord(int initialSYNs, int initialSYNACKs) {
            sentSYNs = initialSYNs;
            receivedSYNACKs = initialSYNACKs;
        }
        
        // If the address is found to have sent a SYN to someone.
        public void sendSYN() {
            sentSYNs++;
        }
        
        // If the address is found to have received a SYN-ACK from someone.
        public void receieveSYNACK() {
            receivedSYNACKs++;
        }
        
        public int getSYNs() {
            return sentSYNs;
        }
        
        public int getSYNACKs() {
            return receivedSYNACKs; 
        }
        
        public String toString() {
            return String.format("%d, %d", sentSYNs, receivedSYNACKs);
        }
    }
}
