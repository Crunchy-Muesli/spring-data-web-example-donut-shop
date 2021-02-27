package donut.shop.constant;

public class DonutShopConstant {

    private DonutShopConstant() {
    }

    public static final String EXCEPTION = "Exception";

    //Admin rest service urls
    public static final String ADMIN_REST = "/admin/";
    public static final String ADMIN_GET_INGREDIENTS = "get-ingredients";
    public static final String ADMIN_NEW_DONUT = "new-donut";
    public static final String ADMIN_UPDATE_DONUT = "update-donut/{donutName}";
    public static final String ADMIN_DELETE_DONUT = "delete-donut";
    public static final String ADMIN_ADD_INGREDIENTS = "add-ingredients";
    public static final String ADMIN_DELETE_INGREDIENTS = "delete-ingredients";

    //Customer rest service urls
    public static final String CUSTOMER_REST = "/customer/";
    public static final String CUSTOMER_PLACE_ORDER = "place-order";
    public static final String CUSTOMER_REVIEW = "write-review/{orderId}";
    public static final String CUSTOMER_GET_DONUTS = "get-donuts";
}
