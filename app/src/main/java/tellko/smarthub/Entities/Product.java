package tellko.smarthub.Entities;

public class Product {
    private long id;

    private String brandName;
    private String brandID;
    private boolean brandStatus;
    private String api;
    private String accessoryWarrantyPeriod;
    private String deviceWarrantyPeriod;
    private String serviceWarrantyPeriod;
    private String dapPolicy;
    private String brandPrefix;
    private String imgBase64;
    private Integer priority;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public String getBrandPrefix() {
        return brandPrefix;
    }

    public void setBrandPrefix(String brandPrefix) {
        this.brandPrefix = brandPrefix;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public boolean isBrandStatus() {
        return brandStatus;
    }

    public void setBrandStatus(boolean brandStatus) {
        this.brandStatus = brandStatus;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAccessoryWarrantyPeriod() {
        return accessoryWarrantyPeriod;
    }

    public void setAccessoryWarrantyPeriod(String accessoryWarrantyPeriod) {
        this.accessoryWarrantyPeriod = accessoryWarrantyPeriod;
    }

    public String getDeviceWarrantyPeriod() {
        return deviceWarrantyPeriod;
    }

    public void setDeviceWarrantyPeriod(String deviceWarrantyPeriod) {
        this.deviceWarrantyPeriod = deviceWarrantyPeriod;
    }

    public String getServiceWarrantyPeriod() {
        return serviceWarrantyPeriod;
    }

    public void setServiceWarrantyPeriod(String serviceWarrantyPeriod) {
        this.serviceWarrantyPeriod = serviceWarrantyPeriod;
    }

    public String getDapPolicy() {
        return dapPolicy;
    }

    public void setDapPolicy(String dapPolicy) {
        this.dapPolicy = dapPolicy;
    }
}
