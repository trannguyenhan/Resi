# RESI
A reduction simulation of data packet transmission in Data Center Network, using fat-tree topology <br />
View README.md original : [https://github.com/vuminhhieu1311/Resi/blob/master/README.md](https://github.com/vuminhhieu1311/Resi/blob/master/README.md)
(Full description for the project structure and problem)
<hr>

## Two level table
- With a two level table, flows are foreseen the paths coming from a source to a destination are fixed.
- The two level tables are simple and easy to deploy, but when there are instances where the threads have the same path, they cannot be processed, resulting in reduced throughput.
- How to pair with throughput reaches 50% with two level tables placed in the [data folder](data/pair50).

Example case with throughput is 50%, with k = 4 :

| Sources | Destination |
|---------|-------------|
| 0       | 11          |
| 1       | 19          |
| 2       | 26          |
| 3       | 18          |
| 8       | 24          |
| 9       | 16          |
| 10      | 1           |
| 11      | 17          |
| 16      | 10          |
| 17      | 8           |
| 18      | 27          |
| 19      | 25          |
| 24      | 9           |
| 25      | 3           |
| 26      | 2           |
| 27      | 0           |

## Flow classification

Flow classification includes 2 ideas : 
- Recognize subsequent packets of the same flow, and forward them on the same outgoing port.
- Periodically reassign a minimal number of flow output ports to minimize any disparity between the aggregate flow capacity of different ports. 

With ideas 2, we have 2 functions help sort flow, so that less congestion :  

```
IncomingPacket(packet) {
	Hash source and destination fields of packets (id of flow)
	
	if(seen(hash)){
		assign flow to port x previously assigned
	} else {
		record new flow f
		assign f to the least-loaded upward port x
		send packet on port x
	}
}

// call every t(s), with RearrangementEvent Object in package events.layers
RearrangeFlows(){
	find port p_max and port p_min with outgoing traffic
	calculate D = p_max - p_min 
	
	find largest flow f smaller D from port p_max assign to port p_min if exists
	
}
```

Example case with throughput improve from 50% to 82.5%, with k = 4 :

| Sources | Destination |
|---------|-------------|
| 0       | 8           |
| 1       | 16          |
| 2       | 17          |
| 3       | 19          |
| 8       | 0           |
| 9       | 2           |
| 10      | 3           |
| 11      | 27          |
| 16      | 24          |
| 17      | 26          |
| 18      | 9           |
| 19      | 25          |
| 24      | 18          |
| 25      | 10          |
| 26      | 11          |
| 27      | 1           |

Example case with throughput improve from 50% to 70.19%, with k = 4 :

| Sources | Destination |
|---------|-------------|
| 0       | 18          |
| 1       | 10          |
| 2       | 11          |
| 3       | 19          |
| 8       | 2           |
| 9       | 0           |
| 10      | 17          |
| 11      | 27          |
| 16      | 24          |
| 17      | 26          |
| 18      | 9           |
| 19      | 25          |
| 24      | 16          |
| 25      | 8           |
| 26      | 1           |
| 27      | 3           |

Example case with throughput improve from 50% to 65.37%, with k = 4 :

| Sources | Destination |
|---------|-------------|
| 0       | 18          |
| 1       | 8           |
| 2       | 11          |
| 3       | 17          |
| 8       | 16          |
| 9       | 2           |
| 10      | 1           |
| 11      | 3           |
| 16      | 26          |
| 17      | 24          |
| 18      | 25          |
| 19      | 27          |
| 24      | 0           |
| 25      | 10          |
| 26      | 9           |
| 27      | 19          |

Example case with throughput improve from 50% to 100%, with k = 4 : run with SameIDOutgoing pair host

| Sources | Destination |
|---------|-------------|
| 0       | 1           |
| 1       | 3           |
| 0       | 2           |
| 1       | 0           |
| 0       | 8           |
| 1       | 10          |
| 0       | 9           |
| 1       | 11          |
| 0       | 16          |
| 1       | 18          |
| 0       | 17          |
| 1       | 19          |
| 0       | 24          |
| 1       | 26          |
| 0       | 25          |
| 1       | 27          |

With case have throughput = 100%, when run with flow classification, throughput is 100% too :

| Sources | Destination |
|---------|-------------|
| 0       | 1           |
| 1       | 3           |
| 2       | 0           |
| 3       | 2           |
| 8       | 9           |
| 9       | 11          |
| 10      | 8           |
| 11      | 10          |
| 16      | 17          |
| 17      | 19          |
| 18      | 16          |
| 19      | 18          |
| 24      | 25          |
| 25      | 27          |
| 26      | 24          |
| 27      | 26          |
