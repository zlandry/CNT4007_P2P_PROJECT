# CNT4007 Term Project
## Group Number 19
- Zachary Landry (stickynote11@ufl.edu)
    - Project scaffolding and general structure
- Jeremy Martin (jeremymartin@ufl.edu)
    - Interprocess communication
- Christopher Bursch (burschc@ufl.edu)
    - File reading and message structure

Video Link: [Link](https://www.youtube.com/watch?v=OFwC73z0QZU)

## What was completed:
- 1a) Peers will read Common.cfg and PeerInfo.cfg and set all relevant variables.
- 1b) Peers make TCP connections to other peers.
- 1c) After a connection and handshake, peers start to transfer files.
- 2a) Peers exchange a handshake message after connecting to each other, before sending any other messages.
- 2b) Bitfield messages are created but not fully implemented.
- 3a) Request messages are incomplete, but are partially implemented.
- 3b) Have messages are incomplete, but are partially implemented.
- 3c) Not interested messages are incomplete, but partially implemented.
- 3d) Interested messages are incomplete, but partially implemented. 
- 3e) Peers send pieces during exchange.
- 4a) The program can shutdown correctly.
## What was not completed:
- 2c) Peers do not send "interested" or "not interested" - file transfer just begins. 
- 2d) The program does not send choke and unchoke messages on given intervals.
- 2e) The program does not optimistically unchoke neighbors.
- 3f) The program is not set up to interpret have messages and update the relevant bitfield.


## Running the project:
In the folder CNT4007_P2P_PROJECT, run javac peerProcess.java. Then, to start peers, just run java peerProcess 1001 and java peerProcess 1002.

## Project Description
This repo aims to implement a simple peer to peer file sharing program, similar to bitTorrent, in Java.
