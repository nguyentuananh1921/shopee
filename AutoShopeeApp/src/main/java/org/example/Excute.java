package org.example;

import handleException.VariablesThreadLocal;
import pages.LivePage;
import utils.DeviceManager;

import static pages.LivePage.*;

public class Excute implements Runnable{
    private String port;
    private String deviceId;
    public Excute(String port, String deviceId) {
        this.port = port;
        this.deviceId = deviceId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    @Override
    public void run() {

        System.out.println("Thread: " + Thread.currentThread().getName());
        DeviceManager devicemanager = new DeviceManager(getPort(), getDeviceId());
        LivePage shopeeLive = new LivePage();
        while (true){
            try {
                devicemanager.runAppiumServer();
                devicemanager.setUpDriverTest();
                shopeeLive.navigateToLivePage();
                while (true) {
                    if(VariablesThreadLocal.hasError()){
                        VariablesThreadLocal.clear();
                        break;
                    }
                    shopeeLive.nhanThuongTungXu();
                    shopeeLive.nhanThuongHangNgay();
                    System.out.println("Tổng số tiền nhận được: " + TongSoXu);
                    if(VariablesThreadLocal.hasRewardCount() >= 10) break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(VariablesThreadLocal.hasRewardCount() >= 10) {
                    System.out.println("copleted");
                    break;
                }
                devicemanager.tearDownDriver();
                devicemanager.stopAppiumServer();

            }
        }
    }
}
