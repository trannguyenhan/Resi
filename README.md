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

