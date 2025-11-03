package main;

import config.Configuration;
import organizare.ConfigurationLoader;

public class Main {
        public static void main(String[] args) throws Exception {
            String path = (args.length>0)? args[0] : "config.json";
            Configuration cfg = ConfigurationLoader.load(path);
            System.out.println("Config OK: docs="+cfg.documents.size()+", offices="+cfg.offices.size());
        }
}
