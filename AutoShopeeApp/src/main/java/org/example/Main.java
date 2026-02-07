package org.example;

import utils.helpers.SystemHelpers;

import java.util.List;


public class Main {

    public static void main(String[] args) {
        List <String> devices = SystemHelpers.getConnectedDevices();
        for (int i = 0; i<devices.size(); i++){
            Excute excute = new Excute(String.valueOf(i*2 + 4723), devices.get(i));
            Thread thread = new Thread(excute);
            thread.start();
        }
    }
}