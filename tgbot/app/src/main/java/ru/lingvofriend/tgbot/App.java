package ru.lingvofriend.tgbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            logger.fatal("Expected one command-line argument: bot token");
            return;
        }
        System.out.println("Success");
    }
    
    private static final Logger logger = LogManager.getLogger();
}
