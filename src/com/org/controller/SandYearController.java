package com.org.controller;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;

import com.org.common.CommonConstant;
import com.org.container.CommonContainer;
import com.org.container.UserManager;
import com.org.model.reflect.ReflectUtil;
import com.org.rute.MessageHander;
import com.org.rute.PushMessageRute;
import com.org.services.busi.SandYearService;
import com.org.servlet.CommonController;
import com.org.servlet.SmpHttpServlet;
import com.org.util.SpringUtil;
import com.org.utils.PropertiesUtil;
import com.org.utils.StringUtil;
import com.org.utils.WxUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class SandYearController extends SmpHttpServlet implements CommonController {
    private static final long serialVersionUID = 1L;
    private Log log = LogFactory.getLog(SandYearController.class);

    /**
     * 环节权限查询
     * @Description 
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午4:58:51
     */
    public void checkLinkAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String link = request.getParameter("link");

        JSONObject noticeData = new JSONObject();

        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject json = yService.checkLinkAuth(link);

        String respCode = json.getString(CommonConstant.RESP_CODE);
        String respMsg = json.getString(CommonConstant.RESP_MSG);
        String linkStatus = json.getString(CommonConstant.LINK_STATUS);
        noticeData.put("respCode", respCode);
        noticeData.put("respMsg", respMsg);
        noticeData.put("linkStatus", linkStatus);
        this.write(noticeData, "utf-8", response);
        return;
    }

    /**
     * 去抽奖页面
     * @Description 
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午5:01:15
     */
    public void tocj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        // 抽奖管理员
        if (session.getAttribute("cjmanager") == null) {
            // 如果未登录
            this.forward("/view/cjlogin.jsp", request, response);
            return;
        }

        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject jsonObject = yService.queryCurrentAward();

        String currentAwards = "";
        if (jsonObject == null) {
            currentAwards = "5";
        } else {
            String currentAwardsTemp = jsonObject.getString("currentAward");
            String isStart = jsonObject.getString("isStart");
            if (isStart.equals("0") && !currentAwardsTemp.equals("t")) {
                // 已抽
                currentAwards = String.valueOf(Integer.valueOf(currentAwardsTemp) - 1);
            } else {
                // 未抽
                currentAwards = currentAwardsTemp;
            }
        }

        request.getSession().setAttribute("currentAwards", currentAwards);
        this.forward("/view/cj_prize-index.jsp", request, response);
        return;
    }

    /**
     * 抽奖管理员登录
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午4:58:51
     */
    public void cjchecklgn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String enterpwd = request.getParameter("enterpwd");
        String _enterpwd = PropertiesUtil.getValue("award", "enterpwd");

        JSONObject noticeData = new JSONObject();
        if (enterpwd.equals(_enterpwd)) {
            noticeData.put("respCode", "10000");
            request.getSession().setAttribute("cjmanager", "logined");
        } else {
            noticeData.put("respCode", "");
            noticeData.put("respMsg", "口令错误");
        }
        this.write(noticeData, "utf-8", response);
        return;
    }

    /**
     * 用户登录接口
     * @Description 
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午3:03:27
     */
    public void queryYearMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String phoneNumber = request.getParameter("phoneNumber");
        String type = "1";
        // String name = request.getParameter("userName");

        HttpSession session = request.getSession(true);

        JSONObject noticeData = new JSONObject();

        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject json = yService.queryYearMember(phoneNumber, type);

        String respCode = json.getString(CommonConstant.RESP_CODE);
        noticeData.put("respCode", respCode);
        if (respCode.equals("10000")) {
            JSONObject usermeg = json.getJSONObject("usermeg");
            session.setAttribute("usermeg", usermeg);
        } else {
            String respMsg = json.getString(CommonConstant.RESP_MSG);
            noticeData.put("respMsg", respMsg);
        }
        this.write(noticeData, "utf-8", response);
        return;

    }

    public void toIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray arr = (JSONArray) CommonContainer.getData(CommonConstant.TER_12);
        request.setAttribute("rewardarrary", arr);
        this.forward("/view/index.jsp", request, response);
        return;
    }

    /**
     * 查找同桌用户信息
     * @Description 
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午3:04:11
     */
    public void querySameTableMemberList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String phoneNumber = request.getParameter("phoneNumber");
            String type = "2";
            JSONObject noticeData = new JSONObject();
            HttpSession session = request.getSession(true);

            SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
            JSONObject json = yService.queryYearMember(phoneNumber, type);

            String respCode = json.getString(CommonConstant.RESP_CODE);
            noticeData.put("respCode", respCode);
            if (respCode.equals("10000")) {
                // 同桌其他用户列表
                JSONArray otherMembers = json.getJSONArray("resultInfo");
                session.setAttribute("otherMembers", otherMembers);
            } else {
                // 如果不成功，进入错误页面，显示错误信息
                String respMsg = json.getString(CommonConstant.RESP_MSG);
                request.setAttribute("respMsg", respMsg);
            }
            this.forward("/view/seat.jsp", request, response);
            return;
        } catch (Exception e) {
            this.log.error("个人信息查询/同桌人员查询失败：" + e.getMessage());
            throw e;
        }

    }

    /**
     * 节目、票数查询
     * @param request
     * @param response
     */
    public void queryProgram(HttpServletRequest request, HttpServletResponse response) {
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject json = yService.updateProgram("", "", "2");
        JSONArray allProgram = json.getJSONArray("resultInfo");

        request.setAttribute("allProgram", allProgram);
        try {
            this.forward("/view/program_vote.jsp", request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 到节目状态更新页面
     * @param request
     * @param response
     */
    public void toProgramFlag(HttpServletRequest request, HttpServletResponse response) {
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject json = yService.updateProgram("", "", "2");
        JSONArray allProgram = json.getJSONArray("resultInfo");

        request.setAttribute("allProgram", allProgram);
        try {
            this.forward("/view/program_flag.jsp", request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 到节目状态更新页面
     * @param request
     * @param response
     * @throws Exception 
     */
    public void updateProgramStartFlag(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String isstart = request.getParameter("isstart");
        String pnumber = request.getParameter("pnumber");
        if (isstart.equals("y")) {
            // 如果已经是开始状态了，更新后则变为空，标识为未开始
            isstart = "";
        } else {
            isstart = "y";
        }

        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        yService.updateProgramStartFlag(isstart, pnumber);
        this.redirect("/sandYear/toProgramFlag.do", response);
        return;
    }

    /**
     * 投票
     * @param request
     * @param response
     * @throws Exception
     */
    public void vote(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 先查询现有的节目票数
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject usermeg = (JSONObject) request.getSession().getAttribute("usermeg");
        String phoneNumber = usermeg.getString("moible");
        String pnumber = request.getParameter("pnumber");

        JSONObject json = yService.updateProgram(phoneNumber, pnumber, "1");
        if (json.getString(CommonConstant.RESP_CODE).equals("10000")) {
            // 更新成功后，存储到session pname 约定为节目的id, pnumber 为页面传入的投票节目id
            usermeg.put("pname", pnumber);
        }
        JSONArray allProgram = json.getJSONArray("resultInfo");

        request.setAttribute("allProgram", allProgram);
        this.forward("/view/program_vote.jsp", request, response);
        return;
    }

    public void updateProgram(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String phoneNumber = request.getParameter("phoneNumber");
        String pnumber = request.getParameter("pnumber"); // 节目编号
        String type = request.getParameter("type");
        try {
            SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
            JSONObject json = yService.updateProgram(phoneNumber, pnumber, type);
            if ("2".equals(type)) {
                JSONArray allProgram = json.getJSONArray("resultInfo");
                System.out.println(allProgram.size());
            }
        } catch (Exception e) {
            this.log.error("年会节目投票/年会节目查询失败：" + e.getMessage());
            throw e;
        }
    }

    /**
     * 查询用户获奖信息
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午4:39:35
     */
    public void queryMyPrize(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        JSONObject usermeg = (JSONObject) session.getAttribute("usermeg");
        if (usermeg == null) {
            this.forward("/view/login.jsp", request, response);
            return;
        }

        String phoneNumber = usermeg.getString("moible");
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject json = yService.userDraw(phoneNumber, "", "1", "", "");
        String respCode = json.getString(CommonConstant.RESP_CODE);
        try {
            if (respCode.equals("10000")) {
                request.setAttribute("yearMember", json.getJSONObject("resultInfo"));
                this.forward("/view/prize_winning.jsp", request, response);
            } else {
                this.forward("/view/prize_not_winning.jsp", request, response);
            }
        } catch (Exception e) {
            this.log.error("我的奖品查询失败：" + e.getMessage());
        }
    }

    public void lotteryDrawState(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String phoneNumber = request.getParameter("phoneNumber");
        JSONObject noticeData = new JSONObject();
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject json = yService.userDraw(phoneNumber, "", "1", "", "");
        System.out.println("json:" + json);
        String respCode = json.getString(CommonConstant.RESP_CODE);
        noticeData.put("respCode", respCode);
        noticeData.put("isprize", json.getJSONObject("resultInfo").getString("isprize"));
        String respMsg = json.getString(CommonConstant.RESP_MSG);
        noticeData.put("respMsg", respMsg);
        this.write(noticeData, "utf-8", response);
        return;
    }

    public void acceptAward(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String phoneNumber = request.getParameter("phoneNumber");
        try {
            SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
            JSONObject json = yService.acceptAward(phoneNumber);
            String respCode = json.getString(CommonConstant.RESP_CODE);
            if (respCode.equals("10000")) {
                this.redirect("/sandYear/toHelp.do", response);
                // this.forward("/sandYear/toHelp.do", request, response);
            } else {
                request.setAttribute("errmsg", json);
                this.forward("/view/acceptError.jsp", request, response);
            }
        } catch (Exception e) {
            this.log.error("扫描领奖失败：" + e.getMessage());
            throw e;
        }
    }

    /*
     * public void userDraw(HttpServletRequest request,HttpServletResponse response)throws Exception{
    		JSONObject noticeData = new JSONObject();
    		
    		String level = request.getParameter("level");
    		SandYearService yService = (SandYearService)SpringUtil.getBean("sandYearService");
    		JSONObject json = yService.userDraw("", level, "2", "", "");
    		String respCode = json.getString(CommonConstant.RESP_CODE);
    		noticeData.put("respCode", respCode);
    		noticeData.put("level", level);
    		if("10000".equals(respCode)){
    			JSONArray memArray = json.getJSONArray("resultInfo");
    			noticeData.put("memArray", memArray);
    			log.info(level+"等奖："+memArray);
    		}else{
    			String respMsg = json.getString(CommonConstant.RESP_MSG);				
    			noticeData.put("respMsg", respMsg);
    		}
    		this.write(noticeData, "utf-8", response);				
    		return;
    	}*/

    // 用户抽奖
    public void userDraw(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> paramsMap = this.getParamMap(request);
        String level = paramsMap.get("level").toString();
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        // 0: 已抽 1: 未抽
        yService.saveCurrentAward(level, "1");

        JSONObject noticeData = new JSONObject();
        JSONArray currentAwardUserList = new JSONArray();
        PushMessageRute rute = (PushMessageRute) SpringUtil.getBean("pushMessageRute");
        MessageHander msgHander = rute.rute(paramsMap);
        //
        currentAwardUserList = msgHander.getMessage();

        if (currentAwardUserList != null && !currentAwardUserList.isEmpty()) {
            // 中奖信息缓存到内存中。方法扩展 ：保存到内存的同时，也保存到数据库
            // 更新奖项状态到数据库
            yService.saveCurrentAward(level, "0");
            noticeData.put("respCode", "10000");
            noticeData.put("respMsg", "成功");
            noticeData.put("memArray", currentAwardUserList);
        } else {

        }

        this.write(noticeData, "utf-8", response);
        return;

    }

    // 去补抽奖
    public void tobucj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currentAwards = request.getParameter("currentAwards");
        String buCounts = request.getParameter("buCounts");

        request.setAttribute("currentAwards", currentAwards);
        request.getSession().setAttribute("buCounts", buCounts);
        this.forward("/view/bucj.jsp", request, response);
        return;
    }

    /**
     * 补抽奖
     * @param request
     * @param response
     * @throws Exception
     */
    public void bucj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> paramsMap = this.getParamMap(request);
        String level = paramsMap.get("level").toString();

        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        // 0: 已抽 1: 未抽
        yService.saveCurrentAward(level, "1");

        JSONObject noticeData = new JSONObject();
        JSONArray currentAwardUserList = new JSONArray();
        com.org.temp.BuMessageRute rute = (com.org.temp.BuMessageRute) SpringUtil.getBean("buMessageRute");
        com.org.temp.BuMessageHander msgHander = rute.rute(paramsMap);
        //
        currentAwardUserList = msgHander.getMessage();
        if (currentAwardUserList != null && !currentAwardUserList.isEmpty()) {
            // 中奖信息缓存到内存中。方法扩展 ：保存到内存的同时，也保存到数据库
            // 更新奖项状态到数据库
            yService.saveCurrentAward(level, "0");
            noticeData.put("respCode", "10000");
            noticeData.put("respMsg", "成功");
            noticeData.put("memArray", currentAwardUserList);
        } else {

        }

        this.write(noticeData, "utf-8", response);
        return;

    }

    public void toHelp(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String token = WxUtil.getToken();
        String timestamp = String.valueOf(StringUtil.getTimestamp()); // 必填，生成签名的时间戳
        String nonceStr = UUID.randomUUID().toString(); // 必填，签名，见附录1
        String url = request.getRequestURL().toString();
        String signature = WxUtil.localSign(timestamp, nonceStr, url); // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
        String appid = PropertiesUtil.getValue("wx", "appid");
        this.log.info("toHelp: " + this.getParamMap(request));
        this.log.info("toHelp: nonceStr:" + nonceStr);
        this.log.info("toHelp: url:" + url);
        this.log.info("toHelp: signature:" + signature);
        this.log.info("toHelp: appid:" + appid);

        request.setAttribute("timestamp", timestamp);
        request.setAttribute("nonceStr", nonceStr);
        request.setAttribute("url", url);
        request.setAttribute("signature", signature);
        request.setAttribute("cacheToken", token);
        request.setAttribute("appId", appid);

        request.setAttribute("usermeg", request.getSession().getAttribute("usermeg"));

        this.forward("/view/help.jsp", request, response);
        return;
    }

    public void queryAwardUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String moible = request.getParameter("moible");
        SandYearService yService = (SandYearService) SpringUtil.getBean("sandYearService");
        JSONObject res = yService.queryAwardUser(moible);
        if (res.getString(CommonConstant.RESP_CODE).equals("100000")) {
            request.setAttribute("aim", res.getJSONObject("aim"));
        }

        this.forward("/view/award_query.jsp", request, response);
        return;
    }

    public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String aim = request.getParameter("cname");
        String mtd = request.getParameter("mtd");
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        Object obj = ReflectUtil.ref(aim, mtd, key, value);
        JSONObject result = new JSONObject();
        result.put("obj", obj);
        this.write(result, CommonConstant.UTF8, response);
    }

    public void shakeaward(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        JSONObject usermeg = (JSONObject) session.getAttribute("usermeg");
        if (usermeg == null) {
            this.forward("/view/login.jsp", request, response);
            return;
        }

        JSONObject result = new JSONObject();

        // 先判断有没有中奖
        if (StringUtils.isNotEmpty(usermeg.getString("rewardstate"))) {
            result.put("respCode", "");
            result.put("respMsg", "您已中奖, 不能再参与此次抽奖");
            this.write(result, CommonConstant.UTF8, response);
            return;
        }

        String moible = usermeg.getString("moible");
        // 如果是三等奖 特等奖
        UserManager.addUserToTemporary(moible);
        result.put("respCode", "10000");
        result.put("respMsg", "您已进入抽奖队列,请等候抽奖结果");
        this.write(result, CommonConstant.UTF8, response);
        return;
    }

    /**
     * 刮一刮
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param request
     * @param response
     * @throws Exception
     * @author Shindo   
     * @date 2016年12月21日 下午4:54:25
     */
    public void toguaj(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        JSONObject usermeg = (JSONObject) session.getAttribute("usermeg");
        if (usermeg == null) {
            this.forward("/view/login.jsp", request, response);
            return;
        }

        JSONObject result = new JSONObject();

        // 先判断有没有中奖
        if (StringUtils.isNotEmpty(usermeg.getString("rewardstate"))) {
            result.put("respCode", "");
            result.put("respMsg", "您已中奖, 不能再参与此次抽奖");
            this.write(result, CommonConstant.UTF8, response);
            return;
        }

        String moible = usermeg.getString("moible");
        // 如果是三等奖 特等奖
        UserManager.addUserToTemporary(moible);
        result.put("respCode", "10000");
        result.put("respMsg", "您已进入抽奖队列,请等候抽奖结果");
        this.write(result, CommonConstant.UTF8, response);
        return;
    }

    @Override
    public void post(HttpServletRequest request, HttpServletResponse response) throws Exception {
    }

}
