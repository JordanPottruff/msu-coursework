'''
Created on Oct 12, 2016

@author: mwittie
'''
import network_4 as network
import link_4 as link
import threading
from time import sleep

##configuration parameters
router_queue_size = 0 #0 means unlimited
simulation_time = 35 #give the network sufficient time to transfer all packets before quitting

if __name__ == '__main__':
    object_L = [] #keeps track of objects, so we can kill their threads

    # part 3 routing tables
    router_a_table = {1:2, 2:3, 3:0, 4:1};
    router_b_table = {1:1, 2:1, 3:0, 4:0};
    router_c_table = {1:1, 2:1, 3:0, 4:0};
    router_d_table = {1:2, 2:3, 3:0, 4:1};

    # part 3 network nodes
    host_1 = network.Host(1);
    host_2 = network.Host(2);
    host_3 = network.Host(3);
    host_4 = network.Host(4);
    router_a = network.Router(name='A', intf_count=4, table=router_a_table, max_queue_size=router_queue_size);
    router_b = network.Router(name='B', intf_count=2, table=router_b_table, max_queue_size=router_queue_size);
    router_c = network.Router(name='C', intf_count=2, table=router_c_table, max_queue_size=router_queue_size);
    router_d = network.Router(name='D', intf_count=4, table=router_d_table, max_queue_size=router_queue_size);
    object_L.extend([host_1, host_2, host_3, host_4, router_a, router_b, router_c, router_d]);

    #create a Link Layer to keep track of links between network nodes
    link_layer = link.LinkLayer()
    object_L.append(link_layer)

    # part 3 links
    # host to router links
    link_layer.add_link(link.Link(host_1, 0, router_a, 0, 70))
    link_layer.add_link(link.Link(host_2, 0, router_a, 1, 70))
    link_layer.add_link(link.Link(router_a, 2, host_1, 0, 70))
    link_layer.add_link(link.Link(router_a, 3, host_2, 0, 70))
    link_layer.add_link(link.Link(router_d, 0, host_3, 0, 70))
    link_layer.add_link(link.Link(router_d, 1, host_4, 0, 70))
    link_layer.add_link(link.Link(host_3, 0, router_d, 2, 70))
    link_layer.add_link(link.Link(host_4, 0, router_d, 3, 70))
    # router to router links
    link_layer.add_link(link.Link(router_a, 0, router_b, 0, 70))
    link_layer.add_link(link.Link(router_b, 1, router_a, 2, 70))
    link_layer.add_link(link.Link(router_a, 1, router_c, 0, 70))
    link_layer.add_link(link.Link(router_c, 1, router_a, 3, 70))
    link_layer.add_link(link.Link(router_b, 0, router_d, 0, 70))
    link_layer.add_link(link.Link(router_d, 2, router_b, 1, 70))
    link_layer.add_link(link.Link(router_c, 0, router_d, 1, 70))
    link_layer.add_link(link.Link(router_d, 3, router_c, 1, 70))

    # part 3 start objects
    thread_L = []
    thread_L.append(threading.Thread(name=host_1.__str__(), target=host_1.run))
    thread_L.append(threading.Thread(name=host_2.__str__(), target=host_2.run))
    thread_L.append(threading.Thread(name=host_3.__str__(), target=host_3.run))
    thread_L.append(threading.Thread(name=host_4.__str__(), target=host_4.run))
    thread_L.append(threading.Thread(name=router_a.__str__(), target=router_a.run))
    thread_L.append(threading.Thread(name=router_b.__str__(), target=router_b.run))
    thread_L.append(threading.Thread(name=router_c.__str__(), target=router_c.run))
    thread_L.append(threading.Thread(name=router_d.__str__(), target=router_d.run))

    thread_L.append(threading.Thread(name="Network", target=link_layer.run))

    for t in thread_L:
        t.start()


    #create some send events
    host_2.udt_send(3, "Message from host 2, intended to be received by host 3")

    host_1.udt_send(4, "Message from host 1, intended to be received by host 4")

    host_3.udt_send(1, "Message from host 3, intended to be received by host 1")

    host_4.udt_send(2, "Message from host 4, intended to be received by host 2")


    # for i in range(3):
    #     client.udt_send(2, 'Sample data %d' % i)


    #give the network sufficient time to transfer all packets before quitting
    sleep(simulation_time)

    #join all threads
    for o in object_L:
        o.stop = True
    for t in thread_L:
        t.join()

    print("All simulation threads joined")



# writes to host periodically
