import queue
import threading
import json
import math
import time


## wrapper class for a queue of packets
class Interface:
    ## @param maxsize - the maximum size of the queue storing packets
    def __init__(self, maxsize=0):
        self.in_queue = queue.Queue(maxsize)
        self.out_queue = queue.Queue(maxsize)

    ##get packet from the queue interface
    # @param in_or_out - use 'in' or 'out' interface
    def get(self, in_or_out):
        try:
            if in_or_out == 'in':
                pkt_S = self.in_queue.get(False)
                # if pkt_S is not None:
                #     print('getting packet from the IN queue')
                return pkt_S
            else:
                pkt_S = self.out_queue.get(False)
                # if pkt_S is not None:
                #     print('getting packet from the OUT queue')
                return pkt_S
        except queue.Empty:
            return None

    ##put the packet into the interface queue
    # @param pkt - Packet to be inserted into the queue
    # @param in_or_out - use 'in' or 'out' interface
    # @param block - if True, block until room in queue, if False may throw queue.Full exception
    def put(self, pkt, in_or_out, block=False):
        if in_or_out == 'out':
            # print('putting packet in the OUT queue')
            self.out_queue.put(pkt, block)
        else:
            # print('putting packet in the IN queue')
            self.in_queue.put(pkt, block)


## Implements a network layer packet.
class NetworkPacket:
    ## packet encoding lengths
    dst_S_length = 5
    prot_S_length = 1

    ##@param dst: address of the destination host
    # @param data_S: packet payload
    # @param prot_S: upper layer protocol for the packet (data, or control)
    def __init__(self, dst, prot_S, data_S):
        self.dst = dst
        self.data_S = data_S
        self.prot_S = prot_S

    ## called when printing the object
    def __str__(self):
        return self.to_byte_S()

    ## convert packet to a byte string for transmission over links
    def to_byte_S(self):
        byte_S = str(self.dst).zfill(self.dst_S_length)
        if self.prot_S == 'data':
            byte_S += '1'
        elif self.prot_S == 'control':
            byte_S += '2'
        else:
            raise('%s: unknown prot_S option: %s' %(self, self.prot_S))
        byte_S += self.data_S
        return byte_S

    ## extract a packet object from a byte string
    # @param byte_S: byte string representation of the packet
    @classmethod
    def from_byte_S(self, byte_S):
        dst = byte_S[0 : NetworkPacket.dst_S_length].strip('0')
        prot_S = byte_S[NetworkPacket.dst_S_length : NetworkPacket.dst_S_length + NetworkPacket.prot_S_length]
        if prot_S == '1':
            prot_S = 'data'
        elif prot_S == '2':
            prot_S = 'control'
        else:
            raise('%s: unknown prot_S field: %s' %(self, prot_S))
        data_S = byte_S[NetworkPacket.dst_S_length + NetworkPacket.prot_S_length : ]
        return self(dst, prot_S, data_S)




## Implements a network host for receiving and transmitting data
class Host:

    ##@param addr: address of this node represented as an integer
    def __init__(self, addr):
        self.addr = addr
        self.intf_L = [Interface()]
        self.stop = False #for thread termination

    ## called when printing the object
    def __str__(self):
        return self.addr

    ## create a packet and enqueue for transmission
    # @param dst: destination address for the packet
    # @param data_S: data being transmitted to the network layer
    def udt_send(self, dst, data_S):
        p = NetworkPacket(dst, 'data', data_S)
        print('%s: sending packet "%s"' % (self, p))
        self.intf_L[0].put(p.to_byte_S(), 'out') #send packets always enqueued successfully

    ## receive packet from the network layer
    def udt_receive(self):
        pkt_S = self.intf_L[0].get('in')
        if pkt_S is not None:
            print('%s: received packet "%s"' % (self, pkt_S))

    ## thread target for the host to keep receiving data
    def run(self):
        print (threading.currentThread().getName() + ': Starting')
        while True:
            #receive data arriving to the in interface
            self.udt_receive()
            #terminate
            if(self.stop):
                print (threading.currentThread().getName() + ': Ending')
                return



## Implements a multi-interface router
class Router:

    ##@param name: friendly router name for debugging
    # @param cost_D: cost table to neighbors {neighbor: {interface: cost}}
    # @param max_queue_size: max queue length (passed to Interface)
    def __init__(self, name, cost_D, max_queue_size):
        self.stop = False #for thread termination
        self.name = name
        #create a list of interfaces
        self.intf_L = [Interface(max_queue_size) for _ in range(len(cost_D))]
        #save neighbors and interfeces on which we connect to them
        self.cost_D = cost_D    # {neighbor: {interface: cost}}
        #TODO: set up the routing table for connected hosts
        self.rt_tbl_D = {}      # {destination: {router: cost}}

        # (JORDAN) Here we initialize the routing table by establishing our
        # initial distance vector based on the costs of going directly to each
        # neighbor.
        for neighbor in self.cost_D:
            for interface in self.cost_D[neighbor]:
                row = {}
                row[self.name] = self.cost_D[neighbor][interface]
                self.rt_tbl_D[neighbor] = row
                break

        # (JORDAN) We also need to include the cost to get to ourselves from
        # ourselves, which is always 0.
        self.rt_tbl_D[self.name] = {}
        self.rt_tbl_D[self.name][self.name] = 0

        print('%s: Initialized routing table' % self)
        self.print_routes()


    ## called when printing the object
    def __str__(self):
        return self.name


    ## look through the content of incoming interfaces and
    # process data and control packets
    def process_queues(self):
        for i in range(len(self.intf_L)):
            pkt_S = None
            #get packet from interface i
            pkt_S = self.intf_L[i].get('in')
            #if packet exists make a forwarding decision
            if pkt_S is not None:
                p = NetworkPacket.from_byte_S(pkt_S) #parse a packet out
                if p.prot_S == 'data':
                    self.forward_packet(p,i)
                elif p.prot_S == 'control':
                    self.update_routes(p, i)
                else:
                    raise Exception('%s: Unknown packet type in packet %s' % (self, p))


    ## forward the packet according to the routing table
    #  @param p Packet to forward
    #  @param i Incoming interface number for packet p
    def forward_packet(self, p, i):
        try:
            # TODO: Here you will need to implement a lookup into the
            # forwarding table to find the appropriate outgoing interface
            # for now we assume the outgoing interface is 1

            # (JORDAN) To determine the interface to forward the packet onto,
            # we need to figure out which one has the least cost. So here I
            # am keeping track of the min one found so far, as well as its
            # cost.
            minIntf = None
            minCost = math.inf

            # (JORDAN) Here I am iterating over all of the routers we can route
            # through in our routing table, excluding ourselves.
            for rtr in self.rt_tbl_D[p.dst]:
                if rtr == self.name:
                    # (JORDAN) This 'continue' excludes rtr when it is equal to
                    # the current router thats forwarding the packet, since we
                    # don't want/can't forward to ourselves.
                    continue

                # (JORDAN) This for loop is annoying because in reality, there
                # is only one interface per each neighbor router. But the cost_D
                # dictionary is given as a 2D dictionary, which theoretically
                # allows for multiple interfaces that connect to the same router.
                # So really we are always iterating over a single interface in
                # any topology, but this has to be done due to how dicts work.
                for intf in self.cost_D[rtr]:
                    # (JORDAN) The cost of getting to our destination from a
                    # router is equal to:
                    #   (1) the cost of getting to the dst from rtr
                    #   (2) plus the cost of getting to the rtr from this router.
                    cost = self.rt_tbl_D[p.dst][rtr]
                    cost += self.rt_tbl_D[rtr][self.name]

                    # (JORDAN) Here we are just updating minCost/minIntf if the
                    # current interface/router is now the cheapest way to get
                    # to our destination.
                    if cost < minCost:
                        minCost = cost
                        minIntf = intf
                    continue

            # (JORDAN) HOWEVER, if one of our neighbors is the destination, we
            # need to evaluate with whether its less expensive to send directly
            # to it.
            # While it might seem obvious that we would just route straight to
            # the destination if its a neighbor, its possible (but rare) that
            # the path straight to the destination is more expensive than a
            # path through a different router.
            for neighbor in self.cost_D:
                for intf in self.cost_D[neighbor]:
                    if neighbor == p.dst:
                        cost = self.rt_tbl_D[neighbor][self.name]

                        if cost < minCost:
                            minIntf = intf

            # (JORDAN) Finally, we send the packet on the minIntf. The rest
            # below is given code otherwise.
            self.intf_L[minIntf].put(p.to_byte_S(), 'out', True)
            print('%s: forwarding packet "%s" from interface %d to %d' % \
                (self, p, i, 1))
        except queue.Full:
            print('%s: packet "%s" lost on interface %d' % (self, p, i))
            pass


    ## send out route update
    # @param i Interface number on which to send out a routing update
    def send_routes(self, i):
        # TODO: Send out a routing table update
        #create a routing table update packet

        # (JORDAN) WE DO NOT SEND THE ENTIRE ROUTING TABLE. Instead, we send
        # just our distance vector within the routing table. This is basically
        # a 2D dict where the first key is the destination and the second key
        # is the router to go through, which is always our self.name.
        distVec = {}
        for dst in self.rt_tbl_D:
            for rtr in self.rt_tbl_D[dst]:
                if(rtr == self.name):
                    distVec[dst] = {}
                    distVec[dst][rtr] = self.rt_tbl_D[dst][rtr]

        # (JORDAN) We put this distance vector into a JSON-format to send as a
        # message.
        msg = json.dumps(distVec)
        p = NetworkPacket(0, 'control', msg)

        print("name: %s, msg: %s" % (self.name, msg))
        try:
            print('%s: sending routing update "%s" from interface %d' % (self, p, i))
            self.intf_L[i].put(p.to_byte_S(), 'out', True)
        except queue.Full:
            print('%s: packet "%s" lost on interface %d' % (self, p, i))
            pass


    ## forward the packet according to the routing table
    #  @param p Packet containing routing information
    def update_routes(self, p, i):
        #TODO: add logic to update the routing tables and
        # possibly send out routing updates

        # (JORDAN) This is the distance vector we receieved from a neighboring
        # router.
        distVec = json.loads(p.data_S)
        # distVec is {destination: {router: cost}}

        # (JORDAN) We now update the row in our routing table that is for the
        # distance vector of the neighbor who sent us their updated one.
        for dst in distVec:
            for rtr in distVec[dst]:
                if dst not in self.rt_tbl_D:
                    self.rt_tbl_D[dst] = {}
                self.rt_tbl_D[dst][rtr] = distVec[dst][rtr]

        # (JORDAN) Now that we have incorporated our neighbors new distance
        # vector, we need to use it to decide if ours can be updated. Basically,
        # we want to know if we have any shorter ways of getting to certain
        # destinations.

        # (JORDAN) We need to know if any of our distance vector values get
        # updated, because if so we will need to resend ours.
        updated = False

        # (JORDAN) For each destination in our routing table...
        for dst in self.rt_tbl_D:
            # (JORDAN) That is not a destination to ourselves...
            if dst == self.name:
                continue

            # (JORDAN) The cost to it is infinity...
            minCost = math.inf
            if self.name in self.rt_tbl_D[dst]:
                # (JORDAN) Unless we already have a cost to that destination,
                # then it is that cost...
                minCost = self.rt_tbl_D[dst][self.name]
            # (JORDAN) So we iterate over all the routers we can go through...
            for rtr in self.rt_tbl_D[dst]:
                cost = 0
                if rtr == self.name:
                    # (JORDAN) If the router is ourselves, we just compute the
                    # cost of getting to the destination from us...
                    cost += self.rt_tbl_D[dst][rtr]
                else:
                    # (JORDAN) But, if the router is a neighbor, we need to
                    # also include the cost of getting to it...
                    cost += self.rt_tbl_D[rtr][self.name] + self.rt_tbl_D[dst][rtr]

                # (JORDAN) If the cost is less than the one we had previously,
                # update it...
                if cost < minCost:
                    # (JORDAN) Which means our distance vector has changed...
                    updated = True
                    minCost = cost

            # (JORDAN) Changing the distance vector here...
            self.rt_tbl_D[dst][self.name] = minCost

        # (JORDAN) And if the distance vector changed, we need to send out
        # our distance vector to all of our neighbors (interfaces).
        if updated:
            for i in range(0, len(self.intf_L)):
                self.send_routes(i)

        print('%s: Received routing update %s from interface %d' % (self, p, i))


    ## Print routing table
    def print_routes(self):

        # (JORDAN) Keep track of all destinations and routers we can go through.
        dsts = []
        rtrs = []

        # (JORDAN) Find these destinations and routers.
        for dst in self.rt_tbl_D:
            dsts.append(dst)
            for rtr in self.rt_tbl_D[dst]:
                if rtr not in rtrs:
                    rtrs.append(rtr)

        # (JORDAN) Create a table large enough to store all combinations of these
        # destinations and the possible routers to get to them, plus room for
        # headers.
        table = [[None for i in range(len(rtrs)+1)] for j in range(len(dsts)+1)]

        # (JORDAN) The top left corner is the name of the current router who
        # owns the routing table.
        table[0][0] = self.name
        # (JORDAN) Add the destination names to the top row.
        for i in range(0, len(dsts)):
            table[i+1][0] = dsts[i]
        # (JORDAN) Add the "route from" names to the left column.
        for i in range(0, len(rtrs)):
            table[0][i+1] = rtrs[i]

        # (JORDAN) Now update the costs between each destination through each
        # router.
        for row in range(1, len(dsts)+1):
            for col in range(1, len(rtrs)+1):
                dst = dsts[row-1]
                rtr = rtrs[col-1]
                table[row][col] = self.rt_tbl_D[dst][rtr]

        # (JORDAN) With the table now complete, we just need to iterate over its
        # items in a way that allows us to print a fancy table format.
        for col in range(0, len(rtrs)+1):
            print("+----"*(len(dsts)+1) + "+")
            line = "|"
            for row in range(0, len(dsts)+1):
                val = str(table[row][col]).ljust(2)
                line += (" %s |" % val)
            print(line)
        print("+----"*(len(dsts)+1) + "+")



    ## thread target for the host to keep forwarding data
    def run(self):
        print (threading.currentThread().getName() + ': Starting')
        while True:
            self.process_queues()
            if self.stop:
                print (threading.currentThread().getName() + ': Ending')
                return
