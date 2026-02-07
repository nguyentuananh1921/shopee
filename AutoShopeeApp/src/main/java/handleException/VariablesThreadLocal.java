package handleException;

public class VariablesThreadLocal {

    private static ThreadLocal<Boolean> err = ThreadLocal.withInitial(() -> false);

    private static ThreadLocal<Boolean> completed = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Integer> rewardCount = ThreadLocal.withInitial(() -> 0);

    private static ThreadLocal<Integer> widthScreen = ThreadLocal.withInitial(() -> 0);
    private static ThreadLocal<Integer> heighScreen = ThreadLocal.withInitial(() -> 0);

    private static ThreadLocal<String> urlLink = ThreadLocal.withInitial(() -> null);


    public static int hasWidthScreen() {
        return widthScreen.get();
    }

    public static void setWidthScreen(int width) {
        widthScreen.set(width);
    }

    public static int hasHeighScreen() {
        return heighScreen.get();
    }

    public static void setHeightScreen(int heigh) {
        heighScreen.set(heigh);
    }

    public static boolean hasCompleted() {
        return completed.get();
    }

    public static void setCompleted(boolean value) {
        err.set(value);
    }

    public static void setError(boolean value) {
        err.set(value);
    }

    public static boolean hasError() {
        return err.get();
    }

    public static void clear() {
        err.remove();
    }

    public static void setRewardCount(int numberOf){
        rewardCount.set(numberOf);
    }

    public static int hasRewardCount(){
        return rewardCount.get();
    }

    public static void setUrlLink(String value){
        urlLink.set(value);
    }

    public static String getUrlLink(){
        return urlLink.get();
    }


}