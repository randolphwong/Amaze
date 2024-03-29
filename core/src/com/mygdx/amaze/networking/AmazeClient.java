package com.mygdx.amaze.networking;

import com.badlogic.gdx.Gdx;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
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
//    private static final String SERVER_IP_ADDRESS = "10.12.15.125";
    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5668;
    private static final int PACKET_SIZE = 1024;

    // udp stuffs
    private DatagramSocket clientSocket;

    /**
     * invariant: these data structures will only be modified by a single
     * thread at any point of time
     */
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
    }

    public void start() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(1000);

        InetAddress serverIpAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
        sendPacket = new DatagramPacket(sendData, sendData.length, serverIpAddress, SERVER_PORT);
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
    }

    public void startMultiplayerGame() {
        if (joinRoomThread != null) joinRoomThread.interrupt();
        try {
            if (joinRoomThread != null) joinRoomThread.join();
        } catch (InterruptedException e) {
            Gdx.app.error("AmazeClient", "startMultiplayerGame()");
            Thread.currentThread().interrupt();
        }
        if (!gameStarted) {
            receiveQueue = new LinkedBlockingQueue<GameData>();
            sendQueue = new LinkedBlockingQueue<GameData>();
            startSender();
            startReceiver();
            gameStarted = true;
        }
    }

    public void stop() {
        if (joinRoomThread != null) joinRoomThread.interrupt();
        if (senderThread != null) senderThread.interrupt();
        if (receiverThread != null) receiverThread.interrupt();

        try {
            if (joinRoomThread != null) joinRoomThread.join();
            if (senderThread != null) senderThread.join();
            if (receiverThread != null) receiverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GameData data = new GameData();
        data.msgType = Const.POSTGAME;
        try {
            sendGameDataBlocking(data);
        } catch (SocketException e) {
            Gdx.app.error("AmazeClient", "socket closed while sending POSTGAME message");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Gdx.app.error("AmazeClient", "interrupted while sending POSTGAME message");
        }
        gameStarted = false;
    }

    public void close() {
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

    private void sender() {
        while (!Thread.interrupted()) {
            try {
                sendGameDataBlocking(sendQueue.take());
            } catch (SocketException e) {
                Gdx.app.error("AmazeClient", "socket closed in sender thread");
            } catch (InterruptedException e) {
                Gdx.app.error("AmazeClient", "senderThread interrupted.");
                return;
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

    private void receiver() {
        while (!Thread.interrupted()) {
            try {
                GameData gameData = getGameDataBlocking();
                if (gameData != null) {
                    receiveQueue.put(gameData);
                }
            } catch (SocketException e) {
                Gdx.app.error("AmazeClient", "socket closed in receiverThread.");
                return;
            } catch (SocketTimeoutException e) {
                Gdx.app.error("AmazeClient", "socket timed out in receiverThread.");
            } catch (InterruptedException e) {
                Gdx.app.error("AmazeClient", "receiverThread interrupted.");
                return;
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

    /**
     * precondition: networkListener != null
     */
    private void join() {
        while (!Thread.interrupted()) {
            GameData data = new GameData();
            data.msgType = Const.PREGAME;

            try {
                sendGameDataBlocking(data);

                data = getGameDataBlocking();
                if (data != null && data.msgType == Const.INGAME) {
                    networkListener.onRoomCreated(data);
                    break;
                }
            } catch (SocketException e) {
                Gdx.app.error("AmazeClient", "socket closed in joinRoomThread.");
                return;
            } catch (SocketTimeoutException e) {
                Gdx.app.error("AmazeClient", "socket timed out in joinRoomThread.");
            } catch (InterruptedException e) {
                Gdx.app.error("AmazeClient", "joinRoomThread interrupted");
                return;
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

    /**
     * invariant: this method will only be called by a single thread at any point of time
     */
    private void sendGameDataBlocking(GameData gameData) throws InterruptedException, SocketException {
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(arrayOutputStream);
            objOutputStream.writeObject(gameData);
            sendData = arrayOutputStream.toByteArray();
            sendPacket.setData(sendData);
            clientSocket.send(sendPacket);
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            Gdx.app.error("AmazeClient", "sendGameDataBlocking()", e);
        }
    }

    /**
     * invariant: this method will only be called by a single thread at any point of time
     */
    private GameData getGameDataBlocking() throws InterruptedException, SocketTimeoutException, SocketException {
        GameData gameData = null;
        try {
            clientSocket.receive(receivePacket);
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(receivePacket.getData());
            ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
            gameData = (GameData) objectInputStream.readObject();
        } catch (SocketException e) {
            throw e;
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            Gdx.app.error("AmazeClient", "getGameDataBlocking()", e);
        } catch (IOException e) {
            Gdx.app.error("AmazeClient", "getGameDataBlocking()", e);
        }
        return gameData;
    }

    public void setNetworkListener(AmazeNetworkListener networkListener) {
        this.networkListener = networkListener;
    }
}
