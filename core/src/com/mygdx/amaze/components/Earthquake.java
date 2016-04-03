package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.screens.PlayScreen;

import java.util.Random;

/**
 * Created by Dhanya on 03/04/2016.
 */
public class Earthquake {

        public float time;
        Random random;
        float x, y;
        float current_time;
        float power;
        float current_power;

        public Earthquake(){
            time = 0;
            current_time = 0;
            power = 0;
            current_power = 0;
        }

        // Call this function with the force of the shake
        // and how long it should last
        public void rumble(float power, float time) {
            System.out.println("Earthquake.rumble enabled");
            random = new Random();
            this.power = power;
            this.time = time;
            this.current_time = 0;
            System.out.println("time: " + this.time);
        }

        public void tick(float delta, PlayScreen screen, Player player){
            if(current_time <= time) {
                System.out.println("current time: " + current_time + ", time: " + time);
                current_power = power * ((time - current_time) / time);
                // generate random new x and y values taking into account
                // how much force was passed in
                x = (random.nextFloat() - 0.5f) * 2 * current_power;
                y = (random.nextFloat() - 0.5f) * 2 * current_power;

                // Set the camera to this new x/y position
                screen.camera.translate(-x, -y);
                current_time += delta;
            } else {
//                System.out.println("Shaking over, resume normal functioning");
                // When the shaking is over move the camera back to the player position
                screen.camera.position.x = player.x;
                screen.camera.position.y = player.y;
            }
        }
    }

