# Networking

## GameData

Communication between the server and client uses the GameData class. It
implements the Serializable interface. This allows the class to be serialised
into byte array that can be sent as UDP packets.

The Game class consists of an enum field (MessageType) that distinguish the type
of message that the client/server wants to send. The values of MessageType are:

- PREGAME
- INGAME
- POSTGAME

Besides MessageType, the GameData also consists of player states (xy
coordinates).

## AmazeServer

A UDP server that:

- listens for new clients
- pair two unique clients
- relay message between paired clients

### Working principles

Server runs in a single threaded program that loops indefinitely, listening for
UDP packets. The data of the packet will be assumed to be the GameData object.
Any data that is not a GameData will be discarded.

Each packet received will be checked whether it is one of the three message
types: PREGAME, INGAME and POSTGAME. Each message type will then be handled by
their corresponding handlers.

### Methods

##### getClientMessage

This is the method where all UDP message will be received. The packets are
de-serialized into the GameData object and the GameData.msgType is checked to
determine which handler will process the data.

##### handlePreGameMessage

Each time a client message comes in, we will check whether the client is already
waiting or not.  If it is, we will update the time since it sent its last
request to join a room. On the other hand, if it is a new client, we will check
whether there is an existing client that is waiting. A pairing is done when an
existing client is waiting, otherwise this client will be put on waiting.

In order to ensure that the client that is currently waiting is really waiting,
that is, the client did not cancel the game, we will check the time since it
last sent a request to join the room. Since the client will periodically send a
new request (every second), we can determine that a client has already stopped
waiting if the time of its last request exceeds a certain threshold
(WAIT\_TIME\_THRESHOLD).

##### handleInGameMessage and handlePostGameMessage

These methods simply relays the message from one client to the other. However,
the handlePostGameMessage also removes the clients from the room.

### Design considerations

The server is designed to be single-threaded to keep it simple and avoid
unnecessary concurrency overheads and issues. However, this could possibly pose
a performance issue when the message handling becomes a bottleneck when the
frequency of packets arrival becomes large.

### Issues

- The server is currently designed to assume that the UDP packets do not get
  lost!

## AmazeClient

A UDP client that is capable of sending and receiving data to and from the
AmazeServer. This class provides convenient methods/API for the game
programming: joinRoom(), getGameData(), sendGameData(), etc.

### Working principles

The client operates in two phases. The first phase consists of the operation to
join a room. The second phase consists of everything else. All packets are sent
and received by a dedicated thread. Hence there is a sender thread and a
receiver thread. This allows the sendGameData and getGameData methods to be
non-blocking (without using nio.DatagramChannel).

In order for this non-blocking operation to work, we make use of BlockingQueues
(sendQueue, receiveQueue) for communication between threads. See the methods
below for more information.

### Methods

##### joinRoom



##### getGameData



##### sendGameData


##### setNetworkListener


### Design considerations



### Issues

- The client is currently designed to assume that the UDP packets do not get
  lost!

## AmazeNetworkListener

TODO

## Usage

##### Windows:
Double click on server\_launcher.bat in the project's root directory

##### Mac OS and Linux:
run `./server_launcher` from the project's root directory
