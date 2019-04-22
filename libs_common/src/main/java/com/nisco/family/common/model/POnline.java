package com.nisco.family.common.model;

import java.io.Serializable;

/**
 * Created by Liuys on 2016/7/12
 */
public class POnline implements Serializable {
    private String id;//问题Id
    private String imgUrl;//用户头像地址
    private String askUserName;//用户姓名
    private String createUserNo;//创建者工号
    private String askTime;//时间
    private String askTitle;//title
    private String askContent;//内容
    private String readAmount;//阅读数量 accessCount+accessCountPC
    private String applyUserName;//回复者
    private String applyDept;//回复者部门
    private String applyDeptId;//回复者部门Id
    private String status;//回复状态
    private String applyUserNo;//回复者工号
    private String applyContent;//回复内容
    private String typeName;//类型
    private String typeId;//类型Id
    private String auditUserName;//审核人员名字
    private String auditContent;//审核内容
    private String isCoordination;//是否需协调


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        imgUrl = imgUrl;
    }

    public String getAskUserName() {
        return askUserName;
    }

    public void setAskUserName(String askUserName) {
        this.askUserName = askUserName;
    }

    public String getCreateUserNo() {
        return createUserNo;
    }

    public void setCreateUserNo(String createUserNo) {
        this.createUserNo = createUserNo;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getAskTitle() {
        return askTitle;
    }

    public void setAskTitle(String askTitle) {
        this.askTitle = askTitle;
    }

    public String getAskContent() {
        return askContent;
    }

    public void setAskContent(String askContent) {
        this.askContent = askContent;
    }

    public String getReadAmount() {
        return readAmount;
    }

    public void setReadAmount(String readAmount) {
        this.readAmount = readAmount;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getApplyDept() {
        return applyDept;
    }

    public void setApplyDept(String applyDept) {
        this.applyDept = applyDept;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplyUserNo() {
        return applyUserNo;
    }

    public void setApplyUserNo(String applyUserNo) {
        this.applyUserNo = applyUserNo;
    }

    public String getApplyContent() {
        return applyContent;
    }

    public void setApplyContent(String applyContent) {
        this.applyContent = applyContent;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getApplyDeptId() {
        return applyDeptId;
    }

    public void setApplyDeptId(String applyDeptId) {
        this.applyDeptId = applyDeptId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public String getAuditContent() {
        return auditContent;
    }

    public void setAuditContent(String auditContent) {
        this.auditContent = auditContent;
    }

    public String getIsCoordination() {
        return isCoordination;
    }

    public void setIsCoordination(String isCoordination) {
        this.isCoordination = isCoordination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
