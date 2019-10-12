package com.info.charith.smartwarrantyapp;

public enum Config {
    Instance;

//    public static String ServerUrl = "http://192.168.1.12:8080/restservice/api/";
    public static String ServerUrl = "http://ec2-3-228-111-159.compute-1.amazonaws.com:8080/restservice/api/";
    public static String get_dealers_url = ServerUrl + "dealeruserservice/dealers";
    public static String confirm_user_url= ServerUrl + "dealeruserservice/confirmregitrationdata";
    public static String save_user_url = ServerUrl + "dealeruserservice/registerdealeruser";
    public static String login_url = ServerUrl + "dealeruserservice/login";
    public static String imei_url = ServerUrl + "dealeruserservice/warranty";
    public static String update_warranty = ServerUrl + "dealeruserservice/updatenewwarranty";
    public static String change_password= ServerUrl + "dealeruserservice/changepassword";
    public static String get_product_url= ServerUrl + "dealeruserservice/product/";
    public static String get_activity_reports_url= ServerUrl + "dealeruserservice/filteractivationlist";
    public static String get_summary_reports_url= ServerUrl + "dealeruserservice/summarylist/";
    public static String get_dealer_from_code_url= ServerUrl + "adminuserservice/dealer/";
    public static String logout_url= ServerUrl + "dealeruserservice/logout/";

    public static String date_time_pattern="yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    public static String standard_date_time_pattern="yyyy-MM-dd HH:mm";
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public String passwordPattern = ".*[a-zA-Z].*";


}
