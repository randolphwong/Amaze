package com.mygdx.amaze.networking;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        AmazeServerSingleThread server = new AmazeServerSingleThread(5668);
        server.start();
    }
}
