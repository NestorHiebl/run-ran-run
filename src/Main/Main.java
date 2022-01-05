package Main;

import Networking.WeatherPuller;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        WeatherPuller wp = new WeatherPuller();
        System.out.println(wp.getWeatherString());

        JFrame window = new JFrame("Window test");
        window.setContentPane(new GamePanel());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
    }

}
