package proxy;

import org.openqa.selenium.By;
import utils.Adb;
import utils.DeviceManager;
import utils.MobileUI;

public class ProxyManager {
    By addNew_Btn = By.xpath("//android.widget.TextView[@text=\"Add new proxy\"]");
    By proxyName_Input = By.xpath("//android.widget.TextView[@text=\"Name\"]/following-sibling::android.widget.EditText[1]");
    By serverIP_Input = By.xpath("//android.widget.TextView[@text=\"Server IP\"]/following-sibling::android.widget.EditText[1]");
    By port_Input = By.xpath("//android.widget.TextView[@text=\"Port\"]/following-sibling::android.widget.EditText[1]");
    By userNameInput = By.xpath("//android.widget.TextView[@text=\"Username\"]/following-sibling::android.widget.EditText[1]");
    By passwordInput = By.xpath("//android.widget.TextView[@text=\"Password\"]/following-sibling::android.widget.EditText[1]");
    By createBtn = By.xpath("//android.widget.TextView[@text=\"Create\"]");

    By connection_status = By.xpath("//android.widget.TextView[contains(@text, \"connected\")]");

    public void OpenOxyProxyManagerApp(){
        Adb.sendAdbCommand("shell am start -n io.oxylabs.proxymanager/.MainActivity");
    }
    public void CloseOxyProxyManagerApp(){
        Adb.sendAdbCommand("shell am force-stop io.oxylabs.proxymanager");
    }

    public void clickAddNewProxy(){
        MobileUI.clickElement(addNew_Btn);
    }

    public void setProxyName(String name){
        MobileUI.setText(proxyName_Input, name);
    }
    public void setServerIP (String value){
        MobileUI.setText(serverIP_Input, value);
    }

    public void setPort(String value){
        MobileUI.setText(port_Input, value);
    }
    public void setUserName(String username){
        MobileUI.setText(userNameInput, username);
    }

    public void setPassword(String value){
        MobileUI.setText(passwordInput, value);
    }

    public void clickCreate(){
        MobileUI.clickElement(createBtn);
    }

    public void setProxyForDevice(InforProxy infor){
        CloseOxyProxyManagerApp();
        MobileUI.sleep(2);
        OpenOxyProxyManagerApp();
        MobileUI.sleep(2);
        if (!checkPorxyExist(infor.getServerIp())){
            clickAddNewProxy();
            setProxyName(infor.getName());
            setServerIP(infor.getServerIp());
            setPort(infor.getPort());
            setUserName(infor.getUserName());
            setPassword(infor.getPassWord());
            clickCreate();
        }

    }
    public boolean checkPorxyConnected(){
        return MobileUI.getTextElementByPageSource(connection_status,"trang thai ket noi proxy").contains("Connected to");
    }

    public void connectProxy(InforProxy infor){
        setProxyForDevice(infor);
        MobileUI.clickElement(getProxyOption(infor.getServerIp()));
    }

    public boolean checkPorxyExist(String proxyIp){
        return MobileUI.isElementPresentAndDisplayed(getProxyOption(proxyIp),1);
    }

    public By getProxyOption(String proxyIp){
        return By.xpath("//android.widget.TextView[@text=\"Proxy List\"]/following-sibling::android.view.View/android.widget.TextView[contains(@text, '" + proxyIp + "')]");
    }

}
