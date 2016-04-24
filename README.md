# Amaze

## Installation

1. Make sure you have android SDK installed. 
2. This game runs on its own server. Hence, before installation of the game,
   make sure to set the server IP address and port number in the AmazeClient
   class appropriately.
3. To compile without installing APK into phone:
 * `./gradlew build`
4. To compile and install APK into phone:
 * `./gradlew android:InstallDebug`

_If you are running Windows, replace `./gradlew` with `gradlew`_

## How to run

Start the server:
* `./server_launcher.sh` for shell or
* `server_launcher` for windows cmd/powershell

Once the APK has been installed into the phone, you can simply start the game
from the phone by clicking on the Amaze icon.

If you want to test the game from desktop, run:

`./gradlew desktop:run`
