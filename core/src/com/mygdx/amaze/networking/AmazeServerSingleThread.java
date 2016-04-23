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
    private HashMap<InetSocketAddress, GameData> roomData;

    private InetSocketAddress currentWaiting;
    private long waitingTime;
    private static final long WAIT_TIME_THRESHOLD = 5000;

    public AmazeServerSingleThread(int port) throws Exception {
        serverSocket = new DatagramSocket(port);
        room = new HashMap<InetSocketAddress, InetSocketAddress>();
        roomData = new HashMap<InetSocketAddress, GameData>();
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
                case Const.INITIALISE:
                case Const.GET_INITIALISE: handleInitialisationMessage(); break;
                case Const.REQUEST: handleRequestMessage(); break;
            }
        }
    }

    private void getClientMessage() {
        try {
            serverSocket.receive(receivePacket);

            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(receiveData, 0, receivePacket.getLength());
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
        /*
         * Server being the authority here. Item picking and spawning and monster chasing/unchasing
         * will be finalised by the server. Clients cannot bypass the server to notify each other on
         * these information.
         */
        GameData currentRoomData = roomData.get(senderAddress);
        receiveGameData.itemTaken = currentRoomData.itemTaken;
        receiveGameData.monsterChasing = currentRoomData.monsterChasing;
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

        roomData.remove(room.get(senderAddress));
        roomData.remove(senderAddress);
        room.remove(room.get(senderAddress));
        room.remove(senderAddress);
    }

    private void handleInitialisationMessage() {
        InetSocketAddress senderAddress = new InetSocketAddress(receiveGameData.ipAddress, receiveGameData.port);
        if (!room.containsKey(senderAddress)) {
            // DEBUG PRINT
            //System.out.println(senderAddress + " is not in game room, but INGAME message received.");
            return;
        }
        /*
         * If slave client sends GET_INITILISE message before master sends INITIALISE, then we will
         * set the roomData temporarily with the GET_INITIALISE message to let the future handling
         * know that the the slave already asked for INITIALISE message.
         */
        if (receiveGameData.msgType == Const.INITIALISE) {
            System.out.println("handling INITIALISE message from: " + senderAddress);
            GameData existingData = roomData.get(senderAddress);
            if (existingData != null && receiveGameData.level == existingData.level) {
                send(room.get(senderAddress), receiveGameData);
            }
            roomData.put(senderAddress, receiveGameData);
            roomData.put(room.get(senderAddress), receiveGameData);
        } else {
            System.out.println("handling GET_INITIALISE message from: " + senderAddress);
            GameData initGameData = roomData.get(senderAddress);
            if (initGameData == null) {
                roomData.put(senderAddress, receiveGameData);
                roomData.put(room.get(senderAddress), receiveGameData);
            } else {
                if (receiveGameData.level == initGameData.level) {
                    send(room.get(senderAddress), receiveGameData);
                } else {
                    roomData.put(senderAddress, receiveGameData);
                    roomData.put(room.get(senderAddress), receiveGameData);
                }
            }
        }
    }

    private void handleRequestMessage() {
        InetSocketAddress senderAddress = new InetSocketAddress(receiveGameData.ipAddress, receiveGameData.port);
        if (!room.containsKey(senderAddress)) {
            // DEBUG PRINT
            //System.out.println(senderAddress + " is not in game room, but INGAME message received.");
            return;
        }
        // DEBUG PRINT
        evaluateRequest(senderAddress);
    }

    private void evaluateRequest(InetSocketAddress senderAddress) {
        GameData currentRoomData = roomData.get(senderAddress);
        if (currentRoomData == null) currentRoomData = roomData.get(room.get(senderAddress));

        switch(receiveGameData.requestType) {
        case Const.ITEM_REQUEST:
            System.out.println("handling item request from: " + senderAddress);
            /*
             * By default, itemTaken bit field will be 0 for each item. A request will have the
             * corresponding bit set as 1. As a result, if an item has no yet been taken, the
             * conjunction will result in 0. Hence, the requestOutcome is set to true when the
             * conjunction is 0.
             */
            receiveGameData.requestOutcome = (receiveGameData.itemTaken & currentRoomData.itemTaken) == 0;
            currentRoomData.itemTaken |= receiveGameData.itemTaken;
            break;
        case Const.ITEM_RESPAWN_REQUEST:
            System.out.println("handling item respawn request from: " + senderAddress);
            System.out.println("current item status: " + currentRoomData.itemTaken);
            System.out.println("requested item respawn status: " + currentRoomData.itemTaken);
            /*
             * Reset the itemTaken flag by masking
             */
            currentRoomData.itemTaken &= ~receiveGameData.itemTaken;
            receiveGameData.requestOutcome = true;
            break;
        case Const.MONSTER_CHASE_REQUEST:
            System.out.print(System.currentTimeMillis() + " - start chase"+"(id: "+receiveGameData.requestId+") - "+" - outcome ");
            //System.out.println("handling monster chasing request from: " + senderAddress);
            //System.out.println("current monster chasing status: " + currentRoomData.monsterChasing);
            //System.out.println("requested monster chasing status: " + receiveGameData.monsterChasing);
            /*
             * By default, monsterChasing bit field will be 0 for each monster. A request will have
             * the corresponding bit set as 1. As a result, if the monster is not chasing yet, the
             * conjunction will result in 0. Hence, the requestOutcome is set to true when the
             * conjunction is 0.
             */
            receiveGameData.requestOutcome = canChase(currentRoomData);
            if (receiveGameData.requestOutcome) {
                System.out.print("success: ");
            } else {
                System.out.print("fail: ");
            }
            System.out.print(currentRoomData.monsterChasing + " > ");
            if (receiveGameData.requestOutcome) {
                System.out.print("success: ");
                currentRoomData.monsterChasing |= receiveGameData.monsterChasing;
            } else {
                System.out.print("fail: ");
            }
            System.out.print(currentRoomData.monsterChasing + " - sender: ");
            System.out.print(receiveGameData.port);
            break;
        case Const.MONSTER_STOP_CHASE_REQUEST:
            System.out.print(System.currentTimeMillis() + " - stop chase"+"(id: "+receiveGameData.requestId+") - "+" - outcome ");
            //System.out.println("handling monster stop chasing request from: " + senderAddress);
            //System.out.println("current monster chasing status: " + currentRoomData.monsterChasing);
            //System.out.println("requested monster stop chasing status: " + receiveGameData.monsterChasing);
            /*
             * Reset the monsterChasing flag by masking
             */
            receiveGameData.requestOutcome = canStopChase(currentRoomData);
            if (receiveGameData.requestOutcome) {
                System.out.print("success: ");
            } else {
                System.out.print("fail: ");
            }
            System.out.print(currentRoomData.monsterChasing + " > ");
            if (receiveGameData.requestOutcome) {
                System.out.print("success: ");
                currentRoomData.monsterChasing &= ~receiveGameData.monsterChasing;
            } else {
                System.out.print("fail: ");
            }
            System.out.print(currentRoomData.monsterChasing + " - sender: ");
            System.out.print(receiveGameData.port);
            break;
        }
        System.out.println("request outcome: " + receiveGameData.requestOutcome);
        send(senderAddress, receiveGameData);
    }

    private boolean canChase(GameData currentRoomData) {
        /*
         * compare whether master 
         */
        short receivedMasterRequest = (short) (receiveGameData.monsterChasing >> 8);
        short receivedSlaveRequest = (short) (receiveGameData.monsterChasing & 0xff);
        short currentMasterStatus = (short) (currentRoomData.monsterChasing >> 8);
        short currentSlaveStatus = (short) (currentRoomData.monsterChasing & 0xff);

        if (receivedMasterRequest != 0) {
            if ((receivedMasterRequest & currentSlaveStatus) != 0) {
                return false;
            }
        }
        if (receivedSlaveRequest != 0) {
            if ((receivedSlaveRequest & currentMasterStatus) != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canStopChase(GameData currentRoomData) {
        /*
         * compare whether master 
         */
        short receivedMasterRequest = (short) (receiveGameData.monsterChasing >> 8);
        short receivedSlaveRequest = (short) (receiveGameData.monsterChasing & 0xff);
        short currentMasterStatus = (short) (currentRoomData.monsterChasing >> 8);
        short currentSlaveStatus = (short) (currentRoomData.monsterChasing & 0xff);

        if (receivedMasterRequest != 0) {
            if ((receivedMasterRequest & currentMasterStatus) == 0) {
                return false;
            }
        }
        if (receivedSlaveRequest != 0) {
            if ((receivedSlaveRequest & currentSlaveStatus) == 0) {
                return false;
            }
        }
        return true;
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

    private void send(InetSocketAddress sockAddr, GameData gameData) {
        gameData.ServerTimeStamp = System.currentTimeMillis();
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(arrayOutputStream);
            objOutputStream.writeObject(gameData);
            sendData = arrayOutputStream.toByteArray();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sockAddr);
            serverSocket.send(sendPacket);
        } catch (IOException e) {}
    }
}
