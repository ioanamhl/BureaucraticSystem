package main;

import config.Configuration;
import organizare.*;

public class App {
    public static void main(String[] args) throws Exception {
        Configuration cfg = ConfigurationLoader.load("config.json");
        new Simulare(cfg).ruleaza();
    }
}