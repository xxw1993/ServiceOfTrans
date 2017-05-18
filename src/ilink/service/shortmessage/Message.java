package ilink.service.shortmessage;

import ilink.service.DataBase.MessageSet;
import ilink.service.DataBase.RequestCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.cloopen.rest.sdk.CCPRestSDK;

public class Message extends HttpServlet {
	private final static String TAG = "Message";
	private String Code;
	private int requestCode;
	private String PhoneNumber;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(TAG + "--->被访问了");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json;charset=utf-8");
		req.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		JSONObject json = new JSONObject();

		try {// 获取参数
			Code = req.getParameter("Code");
			PhoneNumber = req.getParameter("PhoneNumber");

			try {
				requestCode = Integer.parseInt(Code);
				if (requestCode == RequestCode.REGISTER_CODE) {
					GetRegisterCode();
				} else if (requestCode == RequestCode.USERBACK_CODE) {

				} else if (requestCode == RequestCode.APPEXCEPTION_CODE) {

				} else if (requestCode == RequestCode.WEBEXCEPTION_CODE) {

				} else if (requestCode == RequestCode.ROADSHOWTIME_CODE) {

				} else if (requestCode == RequestCode.ACCOUNTFREEZE_CODE) {

				} else if (requestCode == RequestCode.FREEZECANCEL_CODE) {

				} else if (requestCode == RequestCode.SYSTEMUPDATE_CODE) {

				} else if (requestCode == RequestCode.RESETPWD_CODE) {

				} else if (requestCode == RequestCode.CHANGEPWD_CODE) {

				} else {

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void GetRegisterCode() {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = null;
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init(MessageSet.MESSAGE_SERVEL_URL,
				MessageSet.MESSAGE_SERVEL_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount(MessageSet.MESSAGE_MAIN_ACCOUNT,
				MessageSet.MESSAGE_MAIN_ACCOUNT_TOKEN);// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId(MessageSet.MESSAGE_APP_ID);// 初始化应用ID
		result = restAPI.sendTemplateSMS(PhoneNumber,
				MessageSet.MESSAGE_REGISTER, new String[] { "112",
						MessageSet.MESSAGE_TIME });

		System.out.println(" result=" + result);

		if ((MessageSet.MESSAGE_SUCCESS_CODE).equals(result.get("statusCode"))) {
			// 正常返回输出data包体信息（map）
			HashMap<String, Object> data = (HashMap<String, Object>) result
					.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object object = data.get(key);
				System.out.println(key + " = " + object);
			}
		} else {
			// 异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") + " 错误信息= "
					+ result.get("statusMsg"));
		}
	}

}
