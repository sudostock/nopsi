/*
 * HostComm.java
 *
 *
 */

package networking;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * This class handles most of the
 * @author Alex Filby
 *
 */
public class netController {
    private int particles;
    private CDQueue  clients;
    private CDQueue  dQ;
    private List lClients;
    private boolean addr;
    private boolean resultsB;
    private boolean dataS;
    private double[][] results;
    private LinkedList <Integer> resultLeft;
    private  Thread send;
    private Thread rec;
    private Thread mas;
    
    public netController(int particles) {
        this.particles = particles;
        addr = false;
        dataS = false;
        clients = new CDQueue(particles);
        dQ = new CDQueue(particles);
        results = new double[particles][2];
        resultLeft = new LinkedList();
        resetList();
        
        
    }
    
    public void addQClient(InetAddress address) {
        clients.put(address);
//        try {
//            qClients.put(address);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//        addr = true;
    }
//
    public InetAddress pullQClient() {
        return (InetAddress) clients.take();
        
//        InetAddress tempA = null;
//        System.out.println(addr + "clients");
//      /*  if(!addr)
//            try{
//                System.out.println("ABOUT TO WAIT FOR CLIENTS");
//                wait();
//            }catch(InterruptedException ex) {
//                ex.printStackTrace();
//            }*/
//        try {
//            tempA = (InetAddress) qClients.take();
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//
//      /*  if(qClients.isEmpty() == true)
//            addr = false;
//        else
//            addr = true;*/
//        return tempA;
        
    }
    
    public void storeResults(int particle, int epochs, double error) {
        results[particle][0] = epochs;
        results[particle][1] = error;
        resultLeft.remove(particle);
        if(resultLeft.isEmpty() == true){
            resultsB = true;
            synchronized (mas) {
                mas.notify();
            }
        }
        
    }
    
    /* Check for notify() or anything else */
    public synchronized double[][] getResults() {
        System.out.println(resultsB + "results");
        if(!resultsB) {
            try{
                System.out.println("about to wait for RESULTS!");
                wait();
            }catch(InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        resultsB = false;
        return results;
    }
    
    public void storeTestData(double testData[][]) {
        System.out.println("In storing data!");
        double testInfo[];
        for(int i = 0; i < particles; i++) {
            testInfo = new double[4];
            testInfo[0] = i;
            testInfo[1] = testData[i][0];
            testInfo[2] = testData[i][1];
            testInfo[3] = testData[i][2];
            try {
                dQ.put(testInfo);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            dataS = true;
            synchronized (send) {
                send.notify();
            }
        }
    }
    
    /* Needs to be fixed */
    public synchronized double[] retrieveTestData() {
        double[] temp = null;
        if(!dataS)
            try{
                System.out.println("about to wait for TESTDATA");
                wait();
            }catch(Exception e) {
                e.printStackTrace();
            }
        System.out.println("Got some test data, now gonna get a client!");
        System.out.println();
        try {
            temp =(double[]) dQ.take();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println(temp);
        
        if(dQ.isEmpty())
            dataS = false;
        return temp;
    }
    
    public void resetList() {
        for(int i = 0; i < particles; i++) {
            resultLeft.add(i);
        }
        
    }
    
    public void setThreads(Thread master, Thread send, Thread recieve) {
        mas = master;
        this.send = send;
        rec = recieve;
    }
    
}
