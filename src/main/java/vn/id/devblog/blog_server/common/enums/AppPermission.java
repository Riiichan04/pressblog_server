package vn.id.devblog.blog_server.common.enums;

public enum AppPermission {
    //Post permission
    CREATE_POST,
    UPDATE_ANY_POST,
    DELETE_ANY_POST,

    //Comment permission
    DELETE_ANY_COMMENT,

    //User permission
    BAN_USER,
    MANAGE_ROLES;
}