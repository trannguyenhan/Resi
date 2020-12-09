# RESI
A reduction simulation of data packet transmission in Data Center Network, using fat-tree topology 

<hr>

## TABLE OF CONTENTS
- [DESCRIPTION](#description)
- [BACKGROUND](#why-the-master-templates)
- [INSTRUCTIONS FOR USE](#instructions-for-use)
- [CONTRIBUTORS](#contributors)
- [LICENCE](#licence)

<hr>

### DESCRIPTION

Today's data centers may contain tens of thousands of computers with high speed and large capacity. In order for these servers to function properly, people must set up the topology, routing algorithms, and flow control algorithms... One of the most popular topologies that have been used is Fat-Tree.

ReSi stands for Reduction Simulation:

* In this project, we simulate the process of data packet transmission in a data center network:
* In this simulator, we DO NOT take communication protocols such as HTTP, UDP, TCP ... into consideration.
* Resi's purpose: to allow developers to install (i) source and destination pairing; (ii) install routing algorithms; (iii) implement flow control algorithms.
* When running a simulation program (file ThrougphputExperiment.java), it will calculate the throughput value of the transmission process in a certain simulation period. 

Here is the example of a fat-tree topology (k = 4):

![4-port fattree](fat-tree-topology.png)

We generate the communicating pairs according to the following strategies, with the added restriction that any host receives traffic from exactly one host:
- Stride(i): A host with index x will send to the host with index (x+1) mod 16.
- Inter-pod Incoming: Multiple pods send to different hosts in the same pod, and all happen to choose the same core switch.
- Same-ID Outgoing: Hosts in the same subnet send to different hosts elsewhere in the network such that the destination hosts have the same host IDs.

<hr>

### BACKGROUND

The following two research papers explain the ideas behind this tool:
* [A Reduction Model For Simulating Large-Scale Interconnection Network], Nguyen Tien Thanh, Nguyen Khanh Van, Bui Manh Cuong.

* [A scalable, commodity data center network architecture](http://ccr.sigcomm.org/online/files/p63-alfares.pdf), Mohammad Al-Fares, Alexander Loukissas, Amin Vahdat.
  - In this paper, the authors adopt a fat-tree topology to interconnect commodity Ethernet switches, with two-level routing table and some algorithms of flow control
  in order to improve the throughput of data center network.
  - They conduct experiments with a 4-port fat-tree, using 10 physical machines to set up this virtual network. On each machine, there is a Click to perform packet
  routing tasks.
  - Each host generates packet with speed 96 Mbps. The uplinks from the pod switches to the core switch are bandwwidth-limited to 106,67 Mbps and all other links are limited to 96 Mbps.
  - They run five times on an experiemt and the simulation period is 1 minute.
 
<hr>

### INSTRUCTIONS FOR USE

1. All the files are in markdown format. While it is good to learn markdown. It is always great to have the [Markdown CheatSheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet) bookmarked somewhere in your browser.
If you can learn it, awesome! It will save you time...But it really is not important: **the more you use markdown, the better you get at it O.o**.

2. There are different Markdown templates for each type of project as below: 
    * [Android](https://github.com/tamzi/ReadMe-MasterTemplates/tree/master/android)- For android focused ReadMe
    * [Website](https://github.com/tamzi/ReadMe-MasterTemplates/tree/master/website)- For websites with HTML, CSS and JS
    * IOS ...coming soon - FOr IOS projects
    * Scripts ....coming soon - For Python, JS type projects
    * brief ...coming soon - TLo edit, this is a minimal version

3. Copy the respective files depending on the type of project. Paste it and edit the file.

4. As a general rule, provide link to download the app if its published on the playstore /website if it is hosted at the description section above.See image below
:point_down: :point_down:

![Edit the Website link](https://raw.githubusercontent.com/tamzi/ReadMe-MasterTemplates/master/website/art/web.png)

5. Want to give a suggestion? Feel free: it's open source. you can [raise issues here](https://github.com/vuminhhieu1311/Resi/issues)
The project is still very incomplete but under development. We would love to have your help in making our project better.

<hr>

### CONTRIBUTORS

- Vu Minh Hieu (hieu.vm183917@sis.hust.edu.vn)
- Nguyen Tien Thanh (nguyenthanh@soict.hust.edu.vn)
- Nguyen Khanh Van (vannk@soict.hust.edu.vn
- Le Vinh Nhon (nhon.lv176841@sis.hust.edu.vn)
- Nguyen Chi Hieu

### LICENCE


[![license](https://img.shields.io/github/license/mashape/apistatus.svg?style=for-the-badge)](#)

The javatuples is one of the simplest java libraries ever made, which is not our work. Its aim is to provide a set of java classes that allow you to work with tuples. All other code / scripts / materials are original contributions of the above contributors, and are released under the MIT LICENSE (see "./LICENSE"). 



[![Open Source Love](https://badges.frapsoft.com/os/v2/open-source-200x33.png?v=103)](#)

We would appreciate you citing this code and the most relevant of our associated research publications below.


