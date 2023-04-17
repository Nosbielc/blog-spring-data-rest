package com.nosbielc.blogspringdatarest.persistence.enums;

public enum CommentStatus {
    PENDING_APPROVAL(0, "Pending Approval"),
    APPROVED(1, "Approved"),
    REJECTED(2, "Rejected"),
    SPAM(3, "Spam"),
    DELETED(4, "Deleted");

    private int code;
    private String displayName;

    CommentStatus(int code, String displayName) {
        this.displayName = displayName;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CommentStatus fromCode(int code) {
        for (CommentStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + code);
    }
}
