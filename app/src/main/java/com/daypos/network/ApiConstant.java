package com.daypos.network;

public class ApiConstant {


    /// lab domain
   // private static final String DOMAIN = "https://lab-5.sketchdemos.com/PHP-WEB-SERVICES/P-1029-DAYPOS/";

    ///
    private static final String DOMAIN = "https://teq-dev-var19.co.in/daypos/";

    private static final String BASE_URL = DOMAIN + "Api/";

    public static final String IMAGE_PATH = DOMAIN + "assets/uploads/item/";


    public static final String login                         = BASE_URL + "login";
    public static final String SignUp                        = BASE_URL + "SignUp";
    public static final String forgotPassword                = BASE_URL + "forgotPassword";
    public static final String resetPassword                 = BASE_URL + "resetPassword";
    public static final String category_list                 = BASE_URL + "category_list";
    public static final String filterProductCategoryWise     = BASE_URL + "filterProductCategoryWise";
    public static final String addEditCategory               = BASE_URL + "addEditCategory";
    public static final String add_item                      = BASE_URL + "add_item";
    public static final String addEditCustomer               = BASE_URL + "addEditCustomer";
    public static final String search_item_list              = BASE_URL + "search_item_list";
    public static final String add_to_cart                   = BASE_URL + "add_to_cart";
    public static final String cart_item_list                = BASE_URL + "cart_item_list";
    public static final String cart_item_delete              = BASE_URL + "cart_item_delete";
    public static final String cart_item_quantity_update     = BASE_URL + "cart_item_quantity_update";
    public static final String customer_list_by_userID       = BASE_URL + "customer_list_by_userID";
    public static final String customer_by_name_mobile       = BASE_URL + "customer_by_name_mobile";
    public static final String edit_item                     = BASE_URL + "edit_item";
    public static final String add_to_favourite              = BASE_URL + "add_to_favourite";
    public static final String delete_favourite              = BASE_URL + "delete_favourite";
    public static final String check_coupon                  = BASE_URL + "check_coupon";
    public static final String check_out                     = BASE_URL + "check_out";
    public static final String favourite_item_list           = BASE_URL + "favourite_item_list";
    public static final String searchToFavorits              = BASE_URL + "searchToFavorits";






}
