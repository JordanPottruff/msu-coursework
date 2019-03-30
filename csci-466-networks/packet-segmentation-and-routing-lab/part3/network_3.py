'''
Created on Oct 12, 2016

@author: mwittie
'''
import queue
import threading


## wrapper class for a queue of packets
class Interface:
    ## @param maxsize - the maximum size of the queue storing packets
    def __init__(self, maxsize=0):
        self.queue = queue.Queue(maxsize);
        self.mtu = None

    ##get packet from the queue interface
    def get(self):
        try:
            return self.queue.get(False)
        except queue.Empty:
            return None

    ##put the packet into the interface queue
    # @param pkt - Packet to be inserted into the queue
    # @param block - if True, block until room in queue, if False may throw queue.Full exception
    def put(self, pkt, block=False):
        self.queue.put(pkt, block)


## Implements a network layer packet (different from the RDT packet
# from programming assignment 2).
# NOTE: This class will need to be extended to for the packet to include
# the fields necessary for the completion of this assignment.
class NetworkPacket:
    ## packet encoding lengths
    dst_addr_S_length = 5
    pack_length_length = 4
    frag_flag_length = 1
    pack_id_length = 1
    pack_off_length = 3

    # head size for initial send
    head_size = dst_addr_S_length + pack_length_length + frag_flag_length + pack_id_length + pack_off_length

    ##@param dst_addr: address of the destination host
    # @param data_S: packet payload
    def __init__(self, dst_addr, data_S):
        self.dst_addr = dst_addr
        self.data_S = data_S

        # frag capabilities
        self.pack_length = self.head_size + len(data_S)
        self.pack_id = 0
        self.frag_flag = 0
        self.pack_off = 3


    ## called when printing the object
    def __str__(self):
        return self.to_byte_S()

    # set frag_val
    def frag_val(self, length, id, frag, off):
        self.frag_flag = frag
        self.pack_length = length + len(self.data_S)
        self.pack_id = id
        self.pack_off = off

    ## convert packet to a byte string for transmission over links
    def to_byte_S(self):
        byte_S = str(self.pack_length).zfill(self.pack_length_length)
        byte_S += str(self.pack_id).zfill(self.pack_id_length)
        byte_S += str(self.frag_flag).zfill(self.frag_flag_length)
        byte_S += str(self.pack_off).zfill(self.pack_off_length)
        byte_S += str(self.dst_addr).zfill(self.dst_addr_S_length)
        byte_S += self.data_S
        return byte_S

    ## extract a packet object from a byte string
    # @param byte_S: byte string representation of the packet
    @classmethod
    def from_byte_S(self, byte_S):
        pointer = 0
        pack_length = int(byte_S[pointer : pointer + NetworkPacket.pack_length_length])

        pointer += NetworkPacket.pack_length_length
        p_id = int(byte_S[pointer : pointer + NetworkPacket.pack_id_length])

        pointer += NetworkPacket.pack_id_length
        frag_flag = int(byte_S[pointer : pointer + NetworkPacket.frag_flag_length])

        pointer += NetworkPacket.frag_flag_length
        off = int(byte_S[pointer : pointer + NetworkPacket.pack_off_length])

        pointer += NetworkPacket.pack_off_length
        dst_addr = int(byte_S[pointer : pointer + NetworkPacket.dst_addr_S_length])

        pointer += NetworkPacket.dst_addr_S_length
        data_S = byte_S[pointer : ]

        # create packet from data
        p = self(dst_addr, data_S)

        p.frag_val(pack_length, p_id, frag_flag, off)

        return p




## Implements a network host for receiving and transmitting data
class Host:

    ##@param addr: address of this node represented as an integer
    def __init__(self, addr):
        self.addr = addr
        self.in_intf_L = [Interface()]
        self.out_intf_L = [Interface()]
        self.stop = False #for thread termination
        self.received_buf = [] #for tracking all packets received

    ## called when printing the object
    def __str__(self):
        return 'Host_%s' % (self.addr)

    ## create a packet and enqueue for transmission
    # @param dst_addr: destination address for the packet
    # @param data_S: data being transmitted to the network layer
    def udt_send(self, dst_addr, data_S):
        if len(data_S) + NetworkPacket.head_size > self.out_intf_L[0].mtu:
            p = NetworkPacket(dst_addr, data_S[0:self.out_intf_L[0].mtu - NetworkPacket.head_size])
            self.out_intf_L[0].put(p.to_byte_S()) #send packets always enqueued successfully
            print('%s: sending packet "%s" on the out interface with mtu=%d' % (self, p, self.out_intf_L[0].mtu))
            self.udt_send(dst_addr,data_S[self.out_intf_L[0].mtu - NetworkPacket.head_size:])
        else:
            p = NetworkPacket(dst_addr, data_S)
            self.out_intf_L[0].put(p.to_byte_S())  # send packets always enqueued successfully
            print('%s: sending packet "%s" on the out interface with mtu=%d' % (self, p, self.out_intf_L[0].mtu))

    ## receive packet from the network layer
    def udt_receive(self):
        pkt_S = self.in_intf_L[0].get()
        if pkt_S is not None:
            p = NetworkPacket.from_byte_S(pkt_S)

            self.received_buf.append(p)
            # if the packet was fragmented
            if p.frag_flag:
                done = False
                while not done:
                    # get more information
                    pkt_S_next = self.in_intf_L[0].get()
                    # keep trying until more information is received
                    if pkt_S_next is None:
                        continue

                    # at this point there is information so make a packet
                    p_next = NetworkPacket.from_byte_S(pkt_S_next)

                    # if the id's match use the data
                    if p_next.pack_id == p.pack_id:
                        pkt_S += p_next.data_S
                        # if there are more fragments keep looking
                        if p_next.frag_flag:
                            continue
                        # otherwise use the data and exit
                        else:
                            print('%s: received packet "%s" on the in interface' % (self, pkt_S))
                            done = True
            # otherwise use the data
            else:
                print('%s: received packet "%s" on the in interface' % (self, pkt_S))

    ## thread target for the host to keep receiving data
    def run(self):
        print (threading.currentThread().getName() + ': Starting')
        while True:
            #receive data arriving to the in interface
            self.udt_receive()
            #terminate
            if(self.stop):
                msgs = ""
                for packet in self.received_buf:
                    msgs = msgs + packet.data_S
                if len(msgs) > 0:
                    print (threading.currentThread().getName() + ': Ending, received the following message(s):\n%s' % msgs)
                else:
                    print (threading.currentThread().getName() + ': Ending, no received messages.')
                return



## Implements a multi-interface router described in class
class Router:
    id_options = [1, 2, 3, 4, 5, 6, 7, 8, 9]
    id_pointer = 0

    ##@param name: friendly router name for debugging
    # @param intf_count: the number of input and output interfaces
    # @param max_queue_size: max queue length (passed to Interface)
    def __init__(self, name, intf_count, table, max_queue_size):
        self.stop = False #for thread termination
        self.name = name
        self.table = table
        #create a list of interfaces
        self.in_intf_L = [Interface(max_queue_size) for _ in range(intf_count)]
        self.out_intf_L = [Interface(max_queue_size) for _ in range(intf_count)]


    ## called when printing the object
    def __str__(self):
        return 'Router_%s' % (self.name)

    ## look through the content of incoming interfaces and forward to
    # appropriate outgoing interfaces
    def forward(self):
        for i in range(len(self.in_intf_L)):
            pkt_S = None
            try:
                #get packet from interface i
                pkt_S = self.in_intf_L[i].get()
                #if packet exists make a forwarding decision
                if pkt_S is not None:
                    p = NetworkPacket.from_byte_S(pkt_S) #parse a packet out

                    # if there is to much data
                    if len(pkt_S) > self.out_intf_L[i].mtu:
                        # available send space
                        space = self.out_intf_L[i].mtu - NetworkPacket.head_size

                        # while there is data left
                        while len(pkt_S) > 0:
                            # make a sub packet to the send address with all info from old packet
                            # this includes the old packets header as raw data
                            p_next = NetworkPacket(p.dst_addr, pkt_S[0:space])
                            pkt_S = pkt_S[space:]
                            if len(pkt_S) > 0:
                                frag = 1
                            else:
                                frag = 0
                            p_next.frag_val(space, self.id_options[self.id_pointer], frag, int(space/8))
                            self.forwardPacket(p_next)
                        if self.id_pointer >= len(self.id_options)-1:
                            self.id_pointer = 0
                        else:
                            self.id_pointer += 1

                    else:
                        self.forwardPacket(p)
            except queue.Full:
                print('%s: packet "%s" lost on interface %d' % (self, p, i))
                pass

    # forwards packet to correct out interface using the routing table (self.table)
    def forwardPacket(self, packet):
        interfaceNum = self.table[packet.dst_addr]
        self.out_intf_L[interfaceNum].put(packet.to_byte_S(), True)
        print('%s: forwarding packet "%s" from interface %d to %d with mtu %d' \
            % (self, packet, interfaceNum, interfaceNum, self.out_intf_L[interfaceNum].mtu))

    ## thread target for the host to keep forwarding data
    def run(self):
        print (threading.currentThread().getName() + ': Starting')
        while True:
            self.forward()
            if self.stop:
                print (threading.currentThread().getName() + ': Ending')
                return
