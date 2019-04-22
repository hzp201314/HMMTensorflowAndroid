package com.nisco.family.common.model;

/**
 * Created by cathy on 2016/11/21.
 */
public class Content {
    private String Id;//内容Id
    private String Title;//内容标题
    private String ImageUrl;//图片URL
    private String CategoryIds;//分类ids(以分号间隔)
    private String IsValid;//是否可见
    private String AccessCount;//访问次数
    private String ContentFrom;//内容来源
    private String CreateUserId;//创建人
    private String CreateTime;//创建时间
    private String ModifyUserId;//修改人
    private String ModifyTime;//修改时间
    private String EventTime;//发生时间

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getCategoryIds() {
        return CategoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        CategoryIds = categoryIds;
    }

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getAccessCount() {
        return AccessCount;
    }

    public void setAccessCount(String accessCount) {
        AccessCount = accessCount;
    }

    public String getContentFrom() {
        return ContentFrom;
    }

    public void setContentFrom(String contentFrom) {
        ContentFrom = contentFrom;
    }

    public String getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateUserId(String createUserId) {
        CreateUserId = createUserId;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getModifyUserId() {
        return ModifyUserId;
    }

    public void setModifyUserId(String modifyUserId) {
        ModifyUserId = modifyUserId;
    }

    public String getModifyTime() {
        return ModifyTime;
    }

    public void setModifyTime(String modifyTime) {
        ModifyTime = modifyTime;
    }

    public String getEventTime() {
        return EventTime;
    }

    public void setEventTime(String eventTime) {
        EventTime = eventTime;
    }
}
