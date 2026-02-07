package readNewFeeds;

import utils.Adb;
import utils.helpers.SystemHelpers;

public class HandleNewsFeedAndShorts {
    public static void ReadTheStory(){
        System.out.println(Adb.getDeviceId() + " read story on device");
        int startX = SystemHelpers.randomInt(150,200);
        int startY = SystemHelpers.randomInt(600,800);
        int endX = SystemHelpers.randomInt(150,200);
        int endY = SystemHelpers.randomInt(100,350);
        int time = SystemHelpers.randomInt(1000,3000);
        Adb.swipe(startX,startY,endX,endY,time);
    }

    public static void ViewTheShorts(){
        System.out.println(Adb.getDeviceId() + " view shorts on device");
        int startX = SystemHelpers.randomInt(150,200);
        int startY = SystemHelpers.randomInt(600,800);
        int endX = SystemHelpers.randomInt(150,200);
        int endY = SystemHelpers.randomInt(100,350);
        int time = SystemHelpers.randomInt(500,1500);
        Adb.swipe(startX,startY,endX,endY,time);
    }
}
