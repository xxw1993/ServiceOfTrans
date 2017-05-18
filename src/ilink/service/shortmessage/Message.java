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
		System.out.println(TAG + "--->��������");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json;charset=utf-8");
		req.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		JSONObject json = new JSONObject();

		try {// ��ȡ����
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
				MessageSet.MESSAGE_SERVEL_PORT);// ��ʼ����������ַ�Ͷ˿ڣ���ʽ���£���������ַ����Ҫдhttps://
		restAPI.setAccount(MessageSet.MESSAGE_MAIN_ACCOUNT,
				MessageSet.MESSAGE_MAIN_ACCOUNT_TOKEN);// ��ʼ�����ʺź����ʺ�TOKEN
		restAPI.setAppId(MessageSet.MESSAGE_APP_ID);// ��ʼ��Ӧ��ID
		result = restAPI.sendTemplateSMS(PhoneNumber,
				MessageSet.MESSAGE_REGISTER, new String[] { "112",
						MessageSet.MESSAGE_TIME });

		System.out.println(" result=" + result);

		if ((MessageSet.MESSAGE_SUCCESS_CODE).equals(result.get("statusCode"))) {
			// �����������data������Ϣ��map��
			HashMap<String, Object> data = (HashMap<String, Object>) result
					.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object object = data.get(key);
				System.out.println(key + " = " + object);
			}
		} else {
			// �쳣�������������ʹ�����Ϣ
			System.out.println("������=" + result.get("statusCode") + " ������Ϣ= "
					+ result.get("statusMsg"));
		}
	}

}
