package tellko.smarthub.Entities;

public class AppObject {

    String appName;
    String base64;
    Integer imgRes;

    public AppObject(String appName, String base64) {
        this.appName = appName;
        this.base64 = base64;
    }

    public AppObject(String appName, Integer imgRes) {
        this.appName = appName;
        this.imgRes = imgRes;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public void setImgRes(Integer imgRes) {
        this.imgRes = imgRes;
    }

    public String getBase64() {
        return base64;
    }

    public Integer getImgRes() {
        return imgRes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


}
