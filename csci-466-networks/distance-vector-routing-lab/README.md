# Distance Vector Routing Lab
* **Author(s)**: Jordan Pottruff, Taylor Koth
* **Language(s)**: Python
* **Description**: Implementation of a distance vector routing protocol in a network simulation. Distance vector protocols are advantageous because it doesn't require that each router know the entire network to determine the shortest path to a destination. Instead, it relies on information from its neighbors, which in turn relies on information from its neighbors, and so on. Our protocol relies on each router holding its own routing table, transmitting these tables to neighbors when they are updating, and updating their own tables when they receieve a neighbors table.
## Files
* **images/\***: image files for the instructions.
* **part1/\***: files for part 1, includes only the mechanics of transmitting and receiving routing tables.
* **part2/\***: files for part 2, includes code from part 1 and adds packet forwarding based on the routing tables.
* **part3/\***: files for part 3, includes code from part 1 and 2 but now uses a more complex network topology.
* **INSTRUCTIONS.md**: instructions for the assignment, detailing the components of each part.  
