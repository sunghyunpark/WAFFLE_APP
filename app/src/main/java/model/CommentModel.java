package model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SungHyun on 2018. 2. 27..
 */

public class CommentModel {
    @SerializedName("comment_id")
    private String commentId;
    @SerializedName("cafe_id")
    private String cafeId;
    @SerializedName("cafe_name")
    private String cafeName;
    @SerializedName("cafe_thumbnail")
    private String cafeThumbnail;
    private String uid;
    private String name;
    @SerializedName("comment_text")
    private String commentText;
    @SerializedName("created_at")
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public String getCafeThumbnail() {
        return cafeThumbnail;
    }

    public void setCafeThumbnail(String cafeThumbnail) {
        this.cafeThumbnail = cafeThumbnail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

}
