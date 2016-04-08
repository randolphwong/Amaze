package com.mygdx.amaze.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import com.mygdx.amaze.utilities.Const;

public class AmazeClient {

//    private static final String SERVER_IP_ADDRESS = "192.168.1.169";
    private static final String SERVER_IP_ADDRESS = "10.12.15.125";
    private static final int SERVER_PORT = 5668;
    private static final int PACKET_SIZE = 1024;

    // udp stuffs
    private DatagramSocket clientSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private byte[] sendData;
    private byte[] receiveData;

    // message queue for communication between threads
    private LinkedBlockingQueue<GameData> receiveQueue;
    private LinkedBlockingQueue<GameData> sendQueue;

    // all threads
    private Thread joinRoomThread;
    private Thread senderThread;
    private Thread receiverThread;

    private boolean gameStarted = false;

    private AmazeNetworkListener networkListener;

    public AmazeClient() {
        sendData = new byte[PACKET_SIZE];
        receiveData = new byte[PACKET_SIZE];

        receiveQueue = new LinkedBlockingQueue<GameData>();
        sendQueue = new LinkedBlockingQueue<GameData>();
    }

    public void start() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(1000);

        InetAddress serverIpAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
        sendPacket = new DatagramPacket(sendData, sendData.length, serverIpAddress, SERVER_PORT);
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
    }

    public void startMultiplayerGame() {
        if (!gameStarted) {
            startSender();
            startReceiver();
            gameStarted = true;
        }
    }

    public void stop() {
        GameData data = new GameData();
        data.msgType = Const.POSTGAME;
        sendGameDataBlocking(data);

        if (joinRoomThread != null) joinRoomThread.interrupt();
        if (senderThread != null) senderThread.interrupt();
        if (receiverThread != null) receiverThread.interrupt();

        try {
            if (joinRoomThread != null) joinRoomThread.join(100);
            if (senderThread != null) senderThread.join(100);
            if (receiverThread != null) receiverThread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clientSocket.close();
    }

    private void startSender() {
        senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sender();
            }
        });
        senderThread.start();
    }

    // TODO: is this thread-safe?
    private void sender() {
        while (true) {
            if (Thread.interrupted()) return;
            if (clientSocket.isClosed()) return;

            try {
                sendGameDataBlocking(sendQueue.take());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startReceiver() {
        receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiver();
            }
        });
        receiverThread.start();
    }

    // TODO: is this thread-safe?
    private void receiver() {
        while (true) {
            if (Thread.interrupted()) return;
            if (clientSocket.isClosed()) return;

            try {
                GameData gameData = getGameDataBlocking();
                if (gameData != null) {
                    receiveQueue.put(gameData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * precondition: networkListener != null
     */
    public void joinRoom() throws NullPointerException {
        if (networkListener == null) {
            throw new NullPointerException("AmazeNetworkListener cannot be null.");
        }

        joinRoomThread = new Thread(new Runnable() {
            @Override
            public void run() {
                join();
            }
        });
        joinRoomThread.start();
    }

    // TODO: is this thread-safe?
    /**
     * precondition: networkListener != null
     */
    private void join() {
        while (true) {
            if (Thread.interrupted()) return;
            if (clientSocket.isClosed()) return;

            GameData data = new GameData();
            data.msgType = Const.PREGAME;
            sendGameDataBlocking(data);

            data = getGameDataBlocking();
            if (data != null && data.msgType == Const.INGAME) {
                // clear receiveQueue in case it has any PREGAME data
                receiveQueue.clear();
                networkListener.onRoomCreated(data);
                break;
            }
        }
    }

    /**
     * postcondition: doesn't block, but data will be discarded if buffer is
     * full
     */
    public void sendGameData(GameData gameData) {
        sendQueue.offer(gameData);
    }

    /**
     * postcondition: doesn't block
     */
    public GameData getGameData() {
        return receiveQueue.poll();
    }

    private void sendGameDataBlocking(GameData gameData) {
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(arrayOutputStream);
            objOutputStream.writeObject(gameData);
            sendData = arrayOutputStream.toByteArray();
            sendPacket.setData(sendData);
            clientSocket.send(sendPacket);
        } catch (SocketTimeoutException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GameData getGameDataBlocking() {
        GameData gameData = null;
        try {
            clientSocket.receive(receivePacket);
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(receivePacket.getData());
            ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
            gameData = (GameData) objectInputStream.readObject();
        } catch (SocketTimeoutException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameData;
    }

    public void setNetworkListener(AmazeNetworkListener networkListener) {
        this.networkListener = networkListener;
    }
}
