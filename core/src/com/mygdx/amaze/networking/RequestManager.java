package com.mygdx.amaze.networking;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class RequestManager {

    private static RequestManager requestManager;
    private Array<Request> requests;
    private ObjectMap<Integer, Request> toResolve;

    private RequestManager() {
        requests = new Array<Request>();
        toResolve = new ObjectMap<Integer, Request>();
    }

    public static RequestManager getInstance() {
        if (requestManager == null) requestManager = new RequestManager();
        return requestManager;
    }

    public void newRequest(Request request) {
        requests.add(request);
    }

    public void makeRequest(NetworkData networkData) {
        Request request;
        while (requests.size > 0) {
            request = requests.removeIndex(0);
            networkData.createDummyData();
            request.makeRequest(networkData);
            toResolve.put(request.getId(), request);
            networkData.sendToServer();
        }
    }

    public void resolve(NetworkData networkData) {
        int requestId = networkData.getRequestId();
        boolean requestOutcome = networkData.getRequestOutcome();
        Request request;
        try {
            request = toResolve.remove(requestId);
            if (requestOutcome) request.execute();
        } catch (NullPointerException e) { // request not inside map?
            e.printStackTrace();
        }
    }
}
