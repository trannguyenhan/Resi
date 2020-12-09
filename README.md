# RESI
A reduction simulation of data packet transmission in Data Center Network, using fat-tree topology 

<hr>

## TABLE OF CONTENTS
- [DESCRIPTION](#description)
- [BACKGROUND](#why-the-master-templates)
- [INSTRUCTIONS FOR USE](#how-to-use-them)
- [Roadmap](#roadmap)
- [CONTRIBUTORS](#contributors)
- [LICENCE](#licence)

<hr>

### DESCRIPTION

ReSi stands for Reduction Simulation

In this project, we simulate the process of data packet transmission in a data center network:
 - Today's data centers many contain tens of thousands of computers with high speed and large capacity. In order for these servers to function properly, people must set the topology, routing algorithms, and flow control algorithms...
 - One of the most popular topologies that has been used is Fat-Tree


<hr>

### BACKGROUND

The following two research papers explain the ideas behind this tool:
* [A Reduction Model For Simulating Large-Scale Interconnection Network], Nguyen Tien Thanh, Nguyen Khanh Van, Bui Manh Cuong.

* [A scalable, commodity data center network architecture](http://ccr.sigcomm.org/online/files/p63-alfares.pdf), Mohammad Al-Fares, Alexander Loukissas, Amin Vahdat.
  - In this paper, the authors adopt a fat-tree topology to interconnect commodity Ethernet switches, with two-level routing table and some algorithms of flow control
  in order to improve the throughput of data center network.
  - They conducted experiments with a 4-port fat-tree, using 10 physical machines to set up this virtual network. On each machine, there is a Click to perform packet
  routing tasks.
  - Each host generates packet with speed 96 Mbps. The uplinks from the pod switches to the core switch are bandwwidth-limited to 106,67 Mbps and all other links are limited to 96 Mbps.
  - They run five times on an experiemt and the simulation time is 1 minute.
 
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

5. Want to give a suggestion? Feel free: it's open source. you can [raise issues here](https://github.com/vuminhhieu1311/Resi/issues):

<hr>

### Roadmap
  🚧 👷‍ ⛏ 👷 🔧️ 🚧
- [x] Create An Android Projects ReadMe.
- [x] Create a website ReadMe for Html and css and JS projects.
- [ ] Creat a Scripts ReadMe file for Python, php, js type projects
- [ ] Create a ReadMe file for IOS projects

I would love to have your help in making  **readMe master templates** better.

The project is still very incomplete but under development. If there's an [issue](https://github.com/tamzi/ReadMe-MasterTemplates/issues) you'd like to see addressed sooner rather than later:

- [Open an issue](https://github.com/tamzi/ReadMe-MasterTemplates/issues),

    or JUST,

- [Fork the project and send a pull request](https://github.com/tamzi/ReadMe-MasterTemplates/pulls).


Before you contribute though read the contributing guide here: [CONTRIBUTING GUIDE](https://github.com/tamzi/droidconKE2020App/blob/master/contributing.md)



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


