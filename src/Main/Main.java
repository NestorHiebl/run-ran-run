package Main;

import Networking.WeatherData;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        WeatherData wp = new WeatherData();
        System.out.println(wp.getWeatherString() + " " + wp.getHumidity());

        JFrame window = new JFrame("Window test");
        window.setContentPane(new GamePanel(wp));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
    }

}
