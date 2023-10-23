import java.util.*;
import java.io.*;
import java.security.*;

public class PeerThreadOld extends Thread {
    int id;
    String hostname;
    int port;
    int hasFile;

    PeerThreadOld() {
        id = 0000;
        hostname = "lin114-00.cise.ufl.edu";
        port = 2020;
        hasFile = 0;
    }

    PeerThreadOld(DummyPeer peer) {
        this.id = peer.id;
        this.hostname = peer.hostname;
        this.port = peer.port;
        this.hasFile = peer.hasFile;
    }

    public void run() {
        try {
            System.out.println("Thread " + Thread.currentThread().getId() + " is running!");
            DummyPeer peer = new DummyPeer();
            peer.set_id((int) Thread.currentThread().getId());
            peer.get_id();
        }
        catch (Exception e) {
            System.out.println("Exception is caught: " + e.getMessage());
        }
    }

    public int getID() {
        return id;
    }
}

class DummyPeer {
    int id;
    String hostname;
    int port;
    int hasFile;

    public DummyPeer() {
        id = 0000;
        hostname = "lin114-00.cise.ufl.edu";
        port = 2020;
        hasFile = 0;
    }

    void all_out() {
        System.out.println(id + " " + hostname + " " + port + " " + hasFile);
    }

    void set_id(int id) {
        this.id = id;
    }

    void get_id() {
        System.out.println("Peer id: " + this.id);
    }
}

class Multithread {
    public static void main(String[] args) {
        int n = 8;
        for (int i = 0; i < n; i++) {
            PeerThreadOld object = new PeerThreadOld();
            object.start();
        }
        
    }
}
