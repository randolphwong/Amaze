package com.mygdx.amaze.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;

import com.mygdx.amaze.utilities.Const;

public class AmazeServerSingleThread {

    private static final int PACKET_SIZE = 1024;

    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private byte[] receiveData;
    private byte[] sendData;
    private GameData receiveGameData;

    private HashMap<InetSocketAddress, InetSocketAddress> room;

    private InetSocketAddress currentWaiting;
    private long waitingTime;
    private static final long WAIT_TIME_THRESHOLD = 5000;

    public AmazeServerSingleThread(int port) throws Exception {
        serverSocket = new DatagramSocket(port);
        room = new HashMap<InetSocketAddress, InetSocketAddress>();
        currentWaiting = null;

        sendData = new byte[PACKET_SIZE];
        receiveData = new byte[PACKET_SIZE];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
    }

    public void start() {
        while (true) {
            getClientMessage();
            if (receiveGameData == null) continue;

            // DEBUG PRINT
            //System.out.println("received packet");
            
            switch (receiveGameData.msgType) {
                case Const.PREGAME: handlePreGameMessage(); break;
                case Const.INGAME: handleInGameMessage(); break;
                case Const.POSTGAME: handlePostGameMessage(); break;
            }
        }
    }

    private void getClientMessage() {
        try {
            Arrays.fill(receiveData, (byte) 0);
            serverSocket.receive(receivePacket);

            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(receiveData);
            ObjectInputStream objInputStream = new ObjectInputStream(arrayInputStream);
            receiveGameData = (GameData) objInputStream.readObject();

            // add the sender address so that the server knows who to relay the message to
            receiveGameData.ipAddress = receivePacket.getAddress().getHostAddress();
            receiveGameData.port = receivePacket.getPort();
        } catch (ClassNotFoundException e) {
            // DEBUG PRINT
            System.out.println("Received packet is not recognised.");
        } catch (Exception e) {}
    }

    private void handlePreGameMessage() {
        InetSocketAddress senderAddress = new InetSocketAddress(receiveGameData.ipAddress, receiveGameData.port);
        // DEBUG PRINT
        System.out.println(senderAddress + " is attempting to join game.");
        if (room.containsKey(senderAddress)) return; // ignore if a client is already in game

        // ignore if the client is already waiting
        if (currentWaiting == null) {
            currentWaiting = senderAddress;
            waitingTime = System.currentTimeMillis();
        } else if (!currentWaiting.equals(senderAddress)) {
            if ((System.currentTimeMillis() - waitingTime) > WAIT_TIME_THRESHOLD) {
                currentWaiting = senderAddress;
                waitingTime = System.currentTimeMillis();
            } else {
                createPairing(currentWaiting, senderAddress);
                waitingTime = 0;
            }
        } else {
            waitingTime = System.currentTimeMillis();
        }
    }

    private void handleInGameMessage() {
        InetSocketAddress senderAddress = new InetSocketAddress(receiveGameData.ipAddress, receiveGameData.port);
        if (!room.containsKey(senderAddress)) {
            // DEBUG PRINT
            //System.out.println(senderAddress + " is not in game room, but INGAME message received.");
            return;
        }
        // DEBUG PRINT
        //System.out.println("handling in game message from: " + senderAddress);
        send(room.get(senderAddress), receiveGameData);
    }

    private void handlePostGameMessage() {
        InetSocketAddress senderAddress = new InetSocketAddress(receiveGameData.ipAddress, receiveGameData.port);
        if (!room.containsKey(senderAddress)) {
            // DEBUG PRINT
            //System.out.println(senderAddress + " is not in game room, but POSTGAME message received.");
            return;
        }
        // DEBUG PRINT
        System.out.println(senderAddress + " has left the game room.");
        System.out.println((room.get(senderAddress)) + " removed from game room.");
        send(room.get(senderAddress), receiveGameData);
        room.remove(room.get(senderAddress));
        room.remove(senderAddress);
    }

    private void createPairing(InetSocketAddress clientA, InetSocketAddress clientB) {
        currentWaiting = null;
        room.put(clientA, clientB);
        room.put(clientB, clientA);

        GameData newGameData = new GameData();
        newGameData.msgType = Const.INGAME;
        // DEBUG PRINT
        System.out.println("Sending confirmation to " + clientA);
        System.out.println("Sending confirmation to " + clientB);
        // assuming that these packets don't get lost!
        newGameData.clientType = Const.MASTER_CLIENT;
        send(clientA, newGameData);
        newGameData.clientType = Const.SLAVE_CLIENT;
        send(clientB, newGameData);
    }

    private void send(InetSocketAddress sockAddr, Serializable obj) {
        Arrays.fill(sendData, (byte) 0);
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(arrayOutputStream);
            objOutputStream.writeObject(obj);
            sendData = arrayOutputStream.toByteArray();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sockAddr);
            serverSocket.send(sendPacket);
        } catch (IOException e) {}
    }
}
