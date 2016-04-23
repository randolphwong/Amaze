package com.mygdx.amaze.networking;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        AmazeServer server = new AmazeServer(5668);
        server.start();
    }
}
