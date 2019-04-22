package com.nisco.family.common.model;

/**
 * Created by tianzy on 2018/11/5.
 */

public class EventBusBean {
    public EventBusBean() {
    }

    private String isLoad; // "1" 加载

    public EventBusBean(String isLoad) {
        this.isLoad = isLoad;
    }

    public String getIsLoad() {
        return isLoad;
    }

    public void setIsLoad(String isLoad) {
        this.isLoad = isLoad;
    }

    /**
     * 采购派车申请，新增完明细关闭新增主档界面
     */
    private int purchaseClose;

    public int getPurchaseClose() {
        return purchaseClose;
    }

    public void setPurchaseClose(int purchaseClose) {
        this.purchaseClose = purchaseClose;
    }

    /**
     *  vanGuardDelete 车辆自送自提明细界面删除按钮是否显示 “0” 不显示  “1” 显示
     */
    private String vanGuardDelete;

    public String getVanGuardDelete() {
        return vanGuardDelete;
    }

    public void setVanGuardDelete(String vanGuardDelete) {
        this.vanGuardDelete = vanGuardDelete;
    }

    /**
     *  转库接受入库移库
     *  "0" 不操作   “1” 切换到已移库界面
     */
    private String moveIncoming;

    public String getMoveIncoming() {
        return moveIncoming;
    }

    public void setMoveIncoming(String moveIncoming) {
        this.moveIncoming = moveIncoming;
    }

    /**
     *  转库接受入库
     */
    private String acceptIncoming;

    public String getAcceptIncoming() {
        return acceptIncoming;
    }

    public void setAcceptIncoming(String acceptIncoming) {
        this.acceptIncoming = acceptIncoming;
    }

    /**
     * 接受入库（生产库到成品库） 待入库第一个捆号 带到已入库和查询界面
     */
    private String bundleNo;

    public String getBundleNo() {
        return bundleNo;
    }

    public void setBundleNo(String bundleNo) {
        this.bundleNo = bundleNo;
    }

    /**
     *  转库接受入库移库
     *  "0" 不操作   “1” 切换到已出库界面
     */
    private String isOutBound;

    public String getIsOutBound() {
        return isOutBound;
    }

    public void setIsOutBound(String isOutBound) {
        this.isOutBound = isOutBound;
    }

    /**
     *  转库接受入库移库
     *  "0" 不操作   “1” 切换到已出库界面
     */
    private String isTurnLibrary;

    public String getIsTurnLibrary() {
        return isTurnLibrary;
    }

    public void setIsTurnLibrary(String isTurnLibrary) {
        this.isTurnLibrary = isTurnLibrary;
    }

    /**
     *  是否刷新合同盖章人员查询界面
     */
    private String isContractStamped;

    public String getIsContractStamped() {
        return isContractStamped;
    }

    public void setIsContractStamped(String isContractStamped) {
        this.isContractStamped = isContractStamped;
    }

    /**
     *  销售管理 刷新合同
     */
    private int contractRefresh;

    public int getContractRefresh() {
        return contractRefresh;
    }

    public void setContractRefresh(int contractRefresh) {
        this.contractRefresh = contractRefresh;
    }

    /**
     *  销售管理 客户合同关闭详情
     */
    private int closeContract;

    public int getCloseContract() {
        return closeContract;
    }

    public void setCloseContract(int closeContract) {
        this.closeContract = closeContract;
    }
}
