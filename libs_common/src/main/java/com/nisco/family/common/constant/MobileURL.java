package com.nisco.family.common.constant;

/**
 * Created by Liuys on 2016/7/4
 */
public class MobileURL {

    //通讯录、薪资（南钢）、ERP公告
    public static final String URL = "https://mobile.nisco.cn/rest/service";

    //获取请求ERP公告的tk  userno,pwd
    public static final String GET_ERP_TK_URL = NiscoUrl.MOBILE_URL + "/ad/login/%s/%s";
    //ERP公告 userno,pageindex(第几页)
    public static final String GET_ERP_NOTICE_URL = URL + "/ad/erpboard/getBoard/%s/%s?_tk=%s";
    //ERP公告 文件下载
    public static final String GET_ERP_NOTICE_FILE = URL + "/ad/erpboard/geterpfile/%S/%S?_tk=%S";
    //船板定制界面权限
    public static final String GET_USER_PERMISION = URL + "/ad/getuserPermision/%s?_tk=%s";
    //船板定制界面1
    public static final String GET_CAL_ORDENTER = URL + "/ad/ordtrack/calordenter/%s/%s/%s/%s?_tk=%s";

    //船板定制界面2
    public static final String GET_ORDER_PRODUCT = URL + "/ad/ordtrack/ordproduct/%s/%s/%s/%s?_tk=%s";

    //船板定制界面3
    public static final String GET_ORDER_PROCESS =URL+ "/ad/ordtrack/ordprocess/%s/%s/%s/%s/%s?_tk=%s";

    //获取薪资 userno(工号) strdate(日期) token
    public static final String GET_PAY_URL = URL + "/ad/pay/getPay/%s/%s/?_tk=%s";

    //获取用户部门号 userno(工号)
    public static final String GET_USER_DEPNO_URL = URL + "/ad/xtcontact/lodeuserdep/%s?_tk=%s";

    //获取用户部门和本部门用户信息 userno(工号) depno(部门号)   pageIndex(分页)
    public static final String GET_ADDRESS_LIST_URL = URL + "/ad/xtcontact/lodedepuser/%s/%s/%s?_tk=%s";

    //安全管理
    public static final String URL1 = "http://erp.nisco.cn/erp/es";
    public static final String URL2 = "http://erp.nisco.cn/erp";
    //根据工号获得此人所有的检查记录
    public static final String GET_INSPECTRECORD_USERACCOUNT_URL = URL1 + "/do?_pageId=esjjDangerSourceInspect&_action=getInspectRecordByUserAccount&userAccount=%s";

    //根据危险源编号获取危险源的详情
    public static final String GET_DANGERSOURCE_BYCODE_URL = URL1 + "/do?_pageId=esjjDangerSource&_action=getDangerSourceByCode&code=%s";

    //根据检查历史Id获取到检查历史的详情
    public static final String GET_INSPECTDETAIL_BYID_URL = URL1 + "/do?_pageId=esjjDangerSourceInspect&_action=getInspectDetailById&id=%s";

    //根据危险源Id获取需要检查的检查项信息
    public static final String GET_CHECKITEM_DANGERSOURCEID_URL = URL1 + "/do?_pageId=esjjDangerSourceInspect&_action=getCheckItemsByDangerSourceId&dangerSourceId=%s";

    //提交例行检查与有危险源的随手拍这两种检查类型的数据
    public static final String POST_ACTION_URL = URL1 + "/do?_pageId=esjjDangerSourceInspect&_action=create";

    //图片上传接口
    public static final String POST_FILE_UPLOAD_URL = URL1 + "/jsp/fileUpload.jsp";

    //获取所有公司别
    public static final String GET_ALL_COMPANY_URL = URL1 + "/do?_pageId=esjjPublicComponent&_action=findAllCompany";

    //获得erp组织架构数据来生成选择车间的树形
    public static final String GET_DEPT_INFO_URL = URL1 + "/do?_pageId=esjjPublicComponent&_action=getDeptInfoByCompanyCode&companyCode=%s";


    ///e服务的外部接口
    //预约挂号
    public static final String SERVICE_TYPE_0 = "http://mhos.jiankang51.cn/wx12320/home";
    //公交交换
    public static final String SERVICE_TYPE_1 = "https://map.baidu.com/mobile/webapp/third/transit/force=superman&city=%E5%8D%97%E4%BA%AC&code=315&r=0.249495696742";
    //天气预报
    public static final String SERVICE_TYPE_2 = "http://wx.weather.com.cn/mweather/101190101.shtml#1";
    //手机充值
    public static final String SERVICE_TYPE_3 = "http://wap.js.10086.cn/WSCZYL.thtml?ch=46";
    //买汽车票
    public static final String SERVICE_TYPE_4 = "https://m.chebada.com/?refId=98669456";
}
