package vn.id.devblog.blog_server.common.constants;

public final class PermissionConstants {
    private PermissionConstants() {
    }

//    public static final String CREATE_POST = "CREATE_POST";
    public static final String UPDATE_ANY_POST = "UPDATE_ANY_POST";
    public static final String DELETE_ANY_POST = "DELETE_ANY_POST";
    public static final String FORCE_DELETE_ANY_POST = "FORCE_DELETE_ANY_POST";
    public static final String RESTORE_ANY_POST = "RESTORE_ANY_POST";
    public static final String APPROVE_POST = "APPROVE_POST";

    public static final String CREATE_CATEGORY = "CREATE_CATEGORY";
    public static final String UPDATE_CATEGORY = "UPDATE_CATEGORY";
    public static final String DELETE_CATEGORY = "DELETE_CATEGORY"; // Safe delete
    public static final String FORCE_DELETE_CATEGORY = "FORCE_DELETE_CATEGORY";
    public static final String RESTORE_CATEGORY = "RESTORE_CATEGORY";

    public static final String APPROVE_COMMENT = "APPROVE_COMMENT";
    public static final String DELETE_ANY_COMMENT = "DELETE_ANY_COMMENT";
    public static final String RESTORE_ANY_COMMENT = "RESTORE_ANY_COMMENT";

    public static final String UPDATE_TAG = "UPDATE_TAG";
    public static final String DELETE_TAG = "DELETE_TAG";
    public static final String APPROVE_TAG = "APPROVE_TAG";

    public static final String VIEW_USERS = "VIEW_USERS";
    public static final String BAN_USER = "BAN_USER";
    public static final String UPDATE_USER_ROLE = "UPDATE_USER_ROLE";

    public static final String VIEW_DASHBOARD = "VIEW_DASHBOARD";
}