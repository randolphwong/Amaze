package com.mygdx.amaze.networking;

public abstract class Request {

    protected int requestId;

    public abstract void makeRequest(NetworkData networkData);

    public abstract void execute();

    public int getId() {
        return requestId;
    }
}
