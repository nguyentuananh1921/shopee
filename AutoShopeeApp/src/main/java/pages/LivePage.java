package pages;

import handleException.VariablesThreadLocal;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import utils.Adb;
import utils.MobileUI;
import utils.helpers.ElementState;
import utils.helpers.SystemHelpers;

import java.time.LocalDateTime;

public class LivePage {
    public static String linkLiveStreams;
    private final By countTimeSecon = By.xpath("//android.widget.TextView[@text='Xem ']/parent::android.view.ViewGroup/android.widget.TextView[@text=' để nhận thưởng']/preceding-sibling::android.view.ViewGroup/android.widget.TextView");
    private final By TimeBonus = By.xpath("//android.widget.TextSwitcher[@resource-id='com.shopee.vn.dfpluginshopee7:id/tv_title_switch']/android.widget.TextView");
    private final By tien_Thuong = By.xpath("//android.widget.TextView[@text='Nhận']/parent::android.view.ViewGroup/parent::android.view.ViewGroup/following-sibling::android.view.ViewGroup//android.widget.TextView");
    private final By tien_thuong_final = By.xpath("//android.widget.TextView[@text='Nhận']/parent::android.view.ViewGroup/parent::android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup//android.widget.TextView");

    private final By nhanXuLuu = By.xpath("//android.widget.TextView[@resource-id='com.shopee.vn.dfpluginshopee7:id/tv_state_btn' and @text='Lưu']");
    private final By coinNum = By.xpath("//android.widget.TextView[@resource-id='com.shopee.vn.dfpluginshopee7:id/tv_coin_num']");

    private final By comfirmPageLive = By.xpath("//android.widget.FrameLayout[@resource-id='com.shopee.vn.dfpluginshopee7:id/rn_controller']");

    private final By followButton = By.xpath("//android.widget.TextView[@resource-id='com.shopee.vn.dfpluginshopee7:id/btn_follow']");
    private final By commentInput = By.xpath("//android.widget.EditText[@resource-id=\"com.shopee.vn.dfpluginshopee7:id/et_input_message\"]");
    private final By sendComment = By.xpath("//android.widget.ImageView[@resource-id=\"com.shopee.vn.dfpluginshopee7:id/confrim_btn\"]");
    private final By commentInout = By.xpath("//android.widget.TextView[@resource-id=\"com.shopee.vn.dfpluginshopee7:id/tv_send_message\"]");
    private final By likeButton = By.id("com.shopee.vn.dfpluginshopee7:id/iv_like");

    private final By shopName = By.xpath("//android.widget.TextView[@resource-id=\"com.shopee.vn.dfpluginshopee7:id/tv_anchor_name\"]");
    private final By shareButton = By.xpath("//android.widget.ImageView[@resource-id=\"com.shopee.vn.dfpluginshopee7:id/iv_share\"]");
    private final By coppyLinkButton = By.xpath("//android.widget.TextView[@resource-id=\"com.shopee.vn:id/bs_list_title\" and @text=\"Sao chép Link\"]");
    private final By navigateTabLive = By.xpath("//android.widget.TextView[@text=\"Live\"]");

    private final By liveAndVideo = By.xpath("//android.widget.FrameLayout[@resource-id=\"com.shopee.vn:id/old_wrapper\"]/android.widget.TextView[@text=\"Live & Video\"]");
    private boolean coinDefault = false;
    private boolean followed = false;
    private boolean daTungNhanThuong = false;

    public static int TongSoXu = 0;


    String[] listComment = {
            "còn mẫu khác không shop?",
            "chúc shop 1 ngày vui vẻ",
            "giá bán thế nào ạ",
            "có rẻ hơn được nữa không shop"
    };

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean isCoinDefault(){
        return coinDefault;
    }

    public void setCoinDefault(boolean coinDefault) {
        this.coinDefault = coinDefault;
    }

    public String getTextCountTime(){
        return MobileUI.getElementText(countTimeSecon,5);

    }

    public void nhanThuongTungXu(){

        boolean change = false;
        try{
            ElementState hasPrize = MobileUI.isElementDisplayedByPageSource(TimeBonus);
            if(hasPrize.equals(ElementState.DISPLAYED)){
                int coinBonus = Integer.parseInt(MobileUI.getTextElementByPageSource(coinNum,"Số tiền thưởng"));
                if(coinBonus >= 500){
                    daTungNhanThuong = true;
                    change = true;
                    linkLiveStreams = getLinkLiveStreams();
                    clickReactButton(SystemHelpers.randomInt(5,20));
                    clickFollowShop();
                    commentLiveStreams();
//                    MobileUI.sleep(5);
                    if(MobileUI.getTextElementByPageSource(TimeBonus,"Time").contains("Nhấn để nhận thưởng!")) {
                        MobileUI.clickElement(nhanXuLuu, 5);
                        VariablesThreadLocal.setRewardCount(VariablesThreadLocal.hasRewardCount() + 1);
                        setCoinDefault(true);
                        System.out.println(Adb.getDeviceId() + ": Số lần nhận thưởng: " + VariablesThreadLocal.hasRewardCount() + "---" + coinBonus);
                        TongSoXu += coinBonus;
                        MobileUI.sleep(5);
                    }
                }
            }
            else if(hasPrize.equals(ElementState.UNKNOWN)){
                change = true;
                System.out.println("appium crashed...");
            }
            else if(daTungNhanThuong){
                change = true;
                daTungNhanThuong = false;
                int timeStart = LocalDateTime.now().getMinute();
                while (true){
                    if(MobileUI.isElementDisplayedByPageSource(TimeBonus).equals(ElementState.DISPLAYED)){
                        break;
                    }
                    int timeFinish = LocalDateTime.now().getMinute();
                    if((timeStart + 15) % 59 >= timeFinish){
                        break;
                    }
                }
            }

            if(!change){
                change = navigateUrl();
            }

            if(!change) {
                String shopCurrent = MobileUI.getElementText(shopName);
                String nextShop;
                do {
                    scrollLivePage();
                    MobileUI.sleep(2);
                    nextShop = MobileUI.getElementText(shopName);
                } while (shopCurrent.equals(nextShop));
                resetStatus();
                MobileUI.waitForElementPresent(comfirmPageLive, 10);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void nhanThuongHangNgay(){
        if(isCoinDefault()){
            try{
                ElementState state = MobileUI.isElementDisplayedByPageSource(tien_Thuong);
                if (state.equals(ElementState.DISPLAYED)){
                    String SoTienNhan = MobileUI.getTextElementByPageSource(tien_Thuong,"nhận tiền thường");
                    if (!SoTienNhan.isEmpty()){
                        MobileUI.clickElement(tien_Thuong);
                        TongSoXu += Integer.parseInt(SoTienNhan);
                        setCoinDefault(false);
                    }
                }
//                else {
//                    if (MobileUI.isElementDisplayedByPageSource(tien_thuong_final).equals(ElementState.DISPLAYED)){
//                        MobileUI.clickElement(tien_thuong_final);
//                        setCoinDefault(false);
//                    }
//                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void clickReactButton(int num){
        Point point = MobileUI.getElementPosition(likeButton);
        int x = point.getX();
        int y = point.getY();
        for( int i = 0; i< num; i++){
            Adb.tap(x,y);
        }
    }
    public void clickFollowShop(){
        if(!isFollowed()){
            ElementState state = MobileUI.isElementDisplayedByPageSource(followButton);
            if(state.equals(ElementState.DISPLAYED)){
                MobileUI.clickElement(followButton);
            }
            setFollowed(true);
        }
    }
    private void resetStatus(){
        setFollowed(false);
    }
    private void scrollLivePage(){
        int width = VariablesThreadLocal.hasWidthScreen();
        int height = VariablesThreadLocal.hasHeighScreen();
        if(width < 1){
            Dimension size = MobileUI.getScreenSize();
            width = size.getWidth();
            height = size.getHeight();
            VariablesThreadLocal.setWidthScreen(width);
            VariablesThreadLocal.setHeightScreen(height);
        }

        int x1 = SystemHelpers.randomInt((int) (width * 0.8), (int) (width * 0.95));
        int x2 = SystemHelpers.randomInt((int) (width * 0.8), (int) (width * 0.95));
        int y1 = SystemHelpers.randomInt((int) (height * 0.6), (int) (height * 0.8));
        int y2 = SystemHelpers.randomInt((int) (height * 0.1), (int) (height * 0.3));
        int time = SystemHelpers.randomInt(300, 800);

        Adb.swipe(x1, y1, x2, y2, time);

    }

    public void navigateToLivePage(){
        if(!MobileUI.isElementPresentAndDisplayed(liveAndVideo)){
            Adb.back();
        }
        MobileUI.clickElement(liveAndVideo);
        MobileUI.clickElement(navigateTabLive);
        MobileUI.sleep(10);
        scrollLivePage();
    }

    private void navigateLiveTab(){
        MobileUI.clickElement(navigateTabLive);
    }
    public void commentLiveStreams(){
        if(SystemHelpers.randomInt(1, 10) <= 3){
            MobileUI.clickElement(commentInout);
            MobileUI.setText(commentInput, listComment[SystemHelpers.randomInt(0, listComment.length - 1)]);
            MobileUI.clickElement(sendComment);
        }
    }

    private String getLinkLiveStreams(){
        MobileUI.clickElement(shareButton);
        MobileUI.clickElement(coppyLinkButton);
        return Adb.getClipboardText();
    }
    private boolean navigateUrl(){
        boolean navigate = false;
        if(linkLiveStreams != null){
            if(VariablesThreadLocal.getUrlLink().isEmpty() || !VariablesThreadLocal.getUrlLink().equals(linkLiveStreams)){
                Adb.navigateToLink(linkLiveStreams);
                VariablesThreadLocal.setUrlLink(linkLiveStreams);
                navigate = true;
            }
        }
        return navigate;
    }
}
