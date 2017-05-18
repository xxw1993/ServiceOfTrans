/*
 *  Copyright (c) 2014 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.cloopen.rest.sdk;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ytx.org.apache.http.HttpEntity;
import ytx.org.apache.http.HttpResponse;
import ytx.org.apache.http.client.methods.HttpGet;
import ytx.org.apache.http.client.methods.HttpPost;
import ytx.org.apache.http.client.methods.HttpRequestBase;
import ytx.org.apache.http.entity.BasicHttpEntity;
import ytx.org.apache.http.impl.client.DefaultHttpClient;
import ytx.org.apache.http.message.AbstractHttpMessage;
import ytx.org.apache.http.util.EntityUtils;

import com.cloopen.rest.sdk.utils.CcopHttpClient;
import com.cloopen.rest.sdk.utils.DateUtil;
import com.cloopen.rest.sdk.utils.EncryptUtil;
import com.cloopen.rest.sdk.utils.LoggerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CCPRestSDK {
	int status;
	private static final int Request_Get = 0;

	private static final int Request_Post = 1;
	private static final String Account_Info = "AccountInfo";
	private static final String Create_SubAccount = "SubAccounts";
	private static final String Get_SubAccounts = "GetSubAccounts";
	private static final String Query_SubAccountByName = "QuerySubAccountByName";

	private static final String SMSMessages = "SMS/Messages";
	private static final String TemplateSMS = "SMS/TemplateSMS";
	private static final String Query_SMSTemplate = "SMS/QuerySMSTemplate";
	private static final String LandingCalls = "Calls/LandingCalls";
	private static final String VoiceVerify = "Calls/VoiceVerify";
	private static final String IvrDial = "ivr/dial";
	private static final String BillRecords = "BillRecords";
	private static final String queryCallState = "ivr/call";
	private static final String callResult = "CallResult";
	private static final String mediaFileUpload = "Calls/MediaFileUpload";
	private String SERVER_IP;
	private String SERVER_PORT;
	private String ACCOUNT_SID;
	private String ACCOUNT_TOKEN;
	private String SUBACCOUNT_SID;
	private String SUBACCOUNT_Token;
	public String App_ID;
	private BodyType BODY_TYPE = BodyType.Type_XML;
	public String Callsid;

	public enum BodyType {
		Type_XML, Type_JSON;
	}

	public enum AccountType {
		Accounts, SubAccounts;
	}

	/**
	 * ��ʼ�������ַ�Ͷ˿�
	 * 
	 * @param serverIP
	 *            ��ѡ���� ��������ַ
	 * @param serverPort
	 *            ��ѡ���� �������˿�
	 */
	public void init(String serverIP, String serverPort) {
		if (isEmpty(serverIP) || isEmpty(serverPort)) {
			LoggerUtil.fatal("��ʼ���쳣:serverIP��serverPortΪ��");
			throw new IllegalArgumentException("��ѡ����:"
					+ (isEmpty(serverIP) ? " ��������ַ " : "")
					+ (isEmpty(serverPort) ? " �������˿� " : "") + "Ϊ��");
		}
		SERVER_IP = serverIP;
		SERVER_PORT = serverPort;
		System.out.println("��ʼ�����");
	}

	/**
	 * ��ʼ�����ʺ���Ϣ
	 * 
	 * @param accountSid
	 *            ��ѡ���� ���ʺ�
	 * @param accountToken
	 *            ��ѡ���� ���ʺ�TOKEN
	 */
	public void setAccount(String accountSid, String accountToken) {
		if (isEmpty(accountSid) || isEmpty(accountToken)) {
			LoggerUtil.fatal("��ʼ���쳣:accountSid��accountTokenΪ��");
			throw new IllegalArgumentException("��ѡ����:"
					+ (isEmpty(accountSid) ? " ���ʺ�" : "")
					+ (isEmpty(accountToken) ? " ���ʺ�TOKEN " : "") + "Ϊ��");
		}
		ACCOUNT_SID = accountSid;
		ACCOUNT_TOKEN = accountToken;
		System.out.println("��ʼ�����ʺ���Ϣ���");
	}

	/**
	 * ��ʼ�����ʺ���Ϣ
	 * 
	 * @param subAccountSid
	 *            ��ѡ���� ���ʺ�
	 * @param subAccountToken
	 *            ��ѡ���� ���ʺ�TOKEN
	 */
	public void setSubAccount(String subAccountSid, String subAccountToken) {
		if (isEmpty(subAccountSid) || isEmpty(subAccountToken)) {
			LoggerUtil.fatal("��ʼ���쳣:subAccountSid��subAccountTokenΪ��");
			throw new IllegalArgumentException("��ѡ����:"
					+ (isEmpty(subAccountSid) ? " ���ʺ�" : "")
					+ (isEmpty(subAccountToken) ? " ���ʺ�TOKEN " : "") + "Ϊ��");
		}
		SUBACCOUNT_SID = subAccountSid;
		SUBACCOUNT_Token = subAccountToken;
	}

	/**
	 * ��ʼ��Ӧ��Id
	 * 
	 * @param appId
	 *            ��ѡ���� Ӧ��Id
	 */
	public void setAppId(String appId) {
		if (isEmpty(appId)) {
			LoggerUtil.fatal("��ʼ���쳣:appIdΪ��");
			throw new IllegalArgumentException("��ѡ����: Ӧ��Id Ϊ��");
		}
		App_ID = appId;
		System.out.println("��ʼ��Ӧ��Id���");
	}

	/**
	 * ��������
	 * 
	 * @param date
	 *            ��ѡ���� day ����ǰһ������ݣ���00:00 �C 23:59��
	 * @param keywords
	 *            ��ѡ���� �ͻ��Ĳ�ѯ�������ɿͻ����ж��岢�ṩ����ͨѶƽ̨��Ĭ�ϲ�����Դ˲���
	 * @return
	 */
	public HashMap<String, Object> billRecords(String date, String keywords) {

		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(date))) {
			LoggerUtil.fatal("��ѡ����: ����  Ϊ��");
			throw new IllegalArgumentException("��ѡ����: ����  Ϊ��");
		}
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			// e1.printStackTrace();
			LoggerUtil.error(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, BillRecords);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("date", date);
				if (!(isEmpty(keywords)))
					json.addProperty("keywords", keywords);

				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><BillRecords>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<date>").append(date).append("</date>");
				if (!(isEmpty(keywords)))
					sb.append("<keywords>").append(keywords)
							.append("</keywords>");

				sb.append("</BillRecords>").toString();
				requsetbody = sb.toString();
			}
			LoggerUtil.info("billRecords Request body = : " + requsetbody);
			// ��ӡ����
			System.out.println("����İ��壺" + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);

			status = response.getStatusLine().getStatusCode();

			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("billRecords response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ����IVR�������
	 * 
	 * @param number
	 *            ��ѡ���� �����к��룬ΪDial�ڵ������
	 * @param userdata
	 *            ��ѡ���� �û����ݣ���<startservice>֪ͨ�з��أ�ֻ������д�����ַ���ΪDial�ڵ������
	 * @param record
	 *            ��ѡ���� �Ƿ�¼����������Ϊtrue��false��Ĭ��ֵΪfalse��¼����ΪDial�ڵ������
	 * @param disnumber
	 *            ��ѡ���� �û������Ժź��룬����ƽ̨���ԺŹ�����ơ�
	 * @return
	 */
	public HashMap<String, Object> ivrDial(String number, String userdata,
			boolean record, String disnumber) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if (isEmpty(number)) {
			LoggerUtil.fatal("��ѡ����: �����к���   Ϊ��");
			throw new IllegalArgumentException("��ѡ����: �����к���   Ϊ��");
		}
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, IvrDial);
			String requsetbody = "";

			StringBuilder sb = new StringBuilder(
					"<?xml version='1.0' encoding='utf-8'?><Request>");
			sb.append("<Appid>").append(App_ID).append("</Appid>")
					.append("<Dial number=").append("\"").append(number)
					.append("\"");
			if (record) {
				sb.append(" record=").append("\"").append(record).append("\"");
			}
			if (userdata != null) {
				sb.append(" userdata=").append("\"").append(userdata)
						.append("\"");
			}
			if (disnumber != null) {
				sb.append(" disnumber=").append("\"").append(disnumber)
						.append("\"");
			}
			sb.append("></Dial></Request>").toString();
			requsetbody = sb.toString();

			LoggerUtil.info("ivrDial Request body = : " + requsetbody);
			System.out.println("����İ��壺" + requsetbody);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();

			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("ivrDial response body = " + result);
		try {
			return xmlToMap(result);
		} catch (Exception e) {
			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ����������֤������
	 * 
	 * @param verifyCode
	 *            ��ѡ���� ��֤�����ݣ�Ϊ���ֺ�Ӣ����ĸ�������ִ�Сд������4-8λ
	 * @param to
	 *            ��ѡ���� ���պ���
	 * @param displayNum
	 *            ��ѡ���� ��ʾ���к��룬��ʾȨ���ɷ�������
	 * @param playTimes
	 *            ��ѡ���� ѭ�����Ŵ�����1��3�Σ�Ĭ�ϲ���1��
	 * @param respUrl
	 *            ��ѡ���� ������֤��״̬֪ͨ�ص���ַ����ͨѶƽ̨�����Url��ַ���ͺ��н��֪ͨ
	 * @param lang
	 *            ��ѡ���� ��������
	 * @param userData
	 *            ��ѡ���� ������˽������
	 * @param welcomePrompt
	 *            ��ѡ���� wav��ʽ���ļ�������ӭ��ʾ�����ڲ�����֤������ǰ���Ŵ����ݣ����verifyCodeʹ�ã�Ĭ��ֵ�գ�
	 *            ��playVerifyCodeΪ����Ч��
	 * @param playVerifyCode
	 *            ��ѡ����
	 *            wav��ʽ���ļ�����������֤�������ȫ�����Ŵ˽ڵ��µ�ȫ�������ļ���Ҳ����ʵ����������֤�빦�ܲ����û��Լ��������ļ�
	 *            ���ò�����verifyCode���߲���ͬʱΪ�գ������߶���Ϊ��ʱ����ʹ��playVerifyCode��
	 * @param maxCallTime
	 *            ��ѡ���� ���ͨ��ʱ��
	 * @return
	 */
	public HashMap<String, Object> voiceVerify(String verifyCode, String to,
			String displayNum, String playTimes, String respUrl, String lang,
			String userData, String welcomePrompt, String playVerifyCode,
			String maxCallTime) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(verifyCode)) || (isEmpty(to)))
			throw new IllegalArgumentException("��ѡ����:"
					+ (isEmpty(verifyCode) ? " ��֤������ " : "")
					+ (isEmpty(to) ? " ���պ��� " : "") + "Ϊ��");
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, VoiceVerify);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("verifyCode", verifyCode);
				json.addProperty("to", to);
				if (!(isEmpty(displayNum)))
					json.addProperty("displayNum", displayNum);

				if (!(isEmpty(playTimes)))
					json.addProperty("playTimes", playTimes);

				if (!(isEmpty(respUrl)))
					json.addProperty("respUrl", respUrl);
				if (!(isEmpty(lang)))
					json.addProperty("lang", lang);
				if (!(isEmpty(userData)))
					json.addProperty("userData", userData);
				if (!(isEmpty(welcomePrompt)))
					json.addProperty("welcomePrompt", welcomePrompt);
				if (!(isEmpty(playVerifyCode)))
					json.addProperty("playVerifyCode", playVerifyCode);
				if (!(isEmpty(maxCallTime)))
					json.addProperty("maxCallTime", maxCallTime);

				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><VoiceVerify>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<verifyCode>").append(verifyCode)
						.append("</verifyCode>").append("<to>").append(to)
						.append("</to>");
				if (!(isEmpty(displayNum)))
					sb.append("<displayNum>").append(displayNum)
							.append("</displayNum>");

				if (!(isEmpty(playTimes)))
					sb.append("<playTimes>").append(playTimes)
							.append("</playTimes>");

				if (!(isEmpty(respUrl)))
					sb.append("<respUrl>").append(respUrl).append("</respUrl>");
				if (!(isEmpty(lang)))
					sb.append("<lang>").append(lang).append("</lang>");
				if (!(isEmpty(userData)))
					sb.append("<userData>").append(userData)
							.append("</userData>");
				if (!(isEmpty(welcomePrompt)))
					sb.append("<welcomePrompt>").append(welcomePrompt)
							.append("</welcomePrompt>");
				if (!(isEmpty(playVerifyCode)))
					sb.append("<playVerifyCode>").append(playVerifyCode)
							.append("</playVerifyCode>");
				if (!(isEmpty(maxCallTime)))
					sb.append("<maxCallTime>").append(maxCallTime)
							.append("</maxCallTime>");

				sb.append("</VoiceVerify>").toString();
				requsetbody = sb.toString();
			}

			LoggerUtil.info("voiceVerify Request body = : " + requsetbody);
			System.out.println("����İ��壺" + requsetbody);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LoggerUtil.info("voiceVerify response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * �������֪ͨ����
	 * 
	 * @param to
	 *            ��ѡ���� ���к���
	 * @param mediaName
	 *            ��ѡ���� �����ļ����ƣ���ʽ wav����mediaTxt����ͬʱΪ�գ���Ϊ��ʱmediaTxt����ʧЧ
	 * @param mediaTxt
	 *            ��ѡ���� �ı����ݣ�Ĭ��ֵΪ��
	 * @param displayNum
	 *            ��ѡ���� ��ʾ�����к��룬��ʾȨ���ɷ�������
	 * @param playTimes
	 *            ��ѡ���� ѭ�����Ŵ�����1��3�Σ�Ĭ�ϲ���1��
	 * @param respUrl
	 *            ��ѡ���� ���֪ͨ״̬֪ͨ�ص���ַ����ͨѶƽ̨�����Url��ַ���ͺ��н��֪ͨ
	 * @param userData
	 *            ��ѡ���� �û�˽������
	 * @param txtSpeed
	 *            ��ѡ���� �ı�ת������ķ����ٶȣ�ȡֵ��Χ��-500��500����mediaTxt��Ч����Ч��Ĭ��ֵΪ0��
	 * @param txtVolume
	 *            ��ѡ���� �ı�ת�������������С��ȡֵ��Χ��-20��20����mediaTxt��Ч����Ч��Ĭ��ֵΪ0��
	 * @param txtPitch
	 *            ��ѡ���� �ı�ת�������������ȡֵ��Χ��-500��500����mediaTxt��Ч����Ч��Ĭ��ֵΪ0��
	 * @param txtBgsound
	 *            ��ѡ���� �ı�ת������ı�������ţ�Ŀǰ��ͨѶƽ̨֧��6�ֱ�������1��6�����ֱ��������룬0Ϊ����Ҫ��������
	 *            ��ʱ��֧�ֵ������Զ��屳��������mediaTxt��Ч����Ч��
	 * @param playMode
	 *            ��ѡ���� �Ƿ�ͬʱ�����ı��������ļ� , 0���� 1���ǣ�Ĭ��0�����Ȳ����ı���
	 * @return
	 */
	public HashMap<String, Object> landingCall(String to, String mediaName,
			String mediaTxt, String displayNum, String playTimes,
			String respUrl, String userData, String txtSpeed, String txtVolume,
			String txtPitch, String txtBgsound, String playMode) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if (isEmpty(to))
			throw new IllegalArgumentException("��ѡ����:"
					+ (isEmpty(to) ? " ���к��� " : "") + "Ϊ��");
		if ((isEmpty(mediaName)) && (isEmpty(mediaTxt)))
			throw new IllegalArgumentException("���������ļ����ƺͲ��������ı����ݲ���ͬʱΪ��");
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, LandingCalls);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("to", to);

				if (!(isEmpty(mediaName)))
					json.addProperty("mediaName", mediaName);

				if (!(isEmpty(mediaTxt)))
					json.addProperty("mediaTxt", mediaTxt);

				if (!(isEmpty(displayNum)))
					json.addProperty("displayNum", displayNum);
				if (!(isEmpty(playTimes)))
					json.addProperty("playTimes", playTimes);

				if (!(isEmpty(respUrl)))
					json.addProperty("respUrl", respUrl);
				if (!(isEmpty(userData)))
					json.addProperty("userData", userData);
				if (!(isEmpty(txtSpeed)))
					json.addProperty("txtSpeed", txtSpeed);
				if (!(isEmpty(txtVolume)))
					json.addProperty("txtVolume", txtVolume);
				if (!(isEmpty(txtPitch)))
					json.addProperty("txtPitch", txtPitch);
				if (!(isEmpty(txtBgsound)))
					json.addProperty("txtBgsound", txtBgsound);
				if (!(isEmpty(playMode)))
					json.addProperty("playMode", playMode);

				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><LandingCall>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<to>").append(to).append("</to>");
				if (!(isEmpty(mediaName)))
					sb.append("<mediaName>").append(mediaName)
							.append("</mediaName>");
				else if (!(isEmpty(mediaName)))
					sb.append("<mediaName>").append(mediaName)
							.append("</mediaName>");

				if (!(isEmpty(mediaTxt)))
					sb.append("<mediaTxt>").append(mediaTxt)
							.append("</mediaTxt>");

				if (!(isEmpty(displayNum)))
					sb.append("<displayNum>").append(displayNum)
							.append("</displayNum>");
				if (!(isEmpty(playTimes)))
					sb.append("<playTimes>").append(playTimes)
							.append("</playTimes>");

				if (!(isEmpty(respUrl)))
					sb.append("<respUrl>").append(respUrl).append("</respUrl>");
				if (!(isEmpty(userData)))
					sb.append("<userData>").append(userData)
							.append("</userData>");
				if (!(isEmpty(txtSpeed)))
					sb.append("<txtSpeed>").append(txtSpeed)
							.append("</txtSpeed>");
				if (!(isEmpty(txtVolume)))
					sb.append("<txtVolume>").append(txtVolume)
							.append("</txtVolume>");
				if (!(isEmpty(txtPitch)))
					sb.append("<txtPitch>").append(txtPitch)
							.append("</txtPitch>");
				if (!(isEmpty(txtBgsound)))
					sb.append("<txtBgsound>").append(txtBgsound)
							.append("</txtBgsound>");
				if (!(isEmpty(playMode)))
					sb.append("<playMode>").append(playMode)
							.append("</playMode>");

				sb.append("</LandingCall>").toString();
				requsetbody = sb.toString();
			}
			LoggerUtil.info("landingCalls Request body = : " + requsetbody);

			System.out.println("����İ��壺" + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LoggerUtil.info("landingCall response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ���Ͷ���ģ������
	 * 
	 * @param to
	 *            ��ѡ���� ���Ž��ն��ֻ����뼯�ϣ���Ӣ�Ķ��ŷֿ���ÿ�����͵��ֻ����������ó���100��
	 * @param templateId
	 *            ��ѡ���� ģ��Id
	 * @param datas
	 *            ��ѡ���� �������ݣ������滻ģ����{���}
	 * @return
	 */
	public HashMap<String, Object> sendTemplateSMS(String to,
			String templateId, String[] datas) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(to)) || (isEmpty(App_ID)) || (isEmpty(templateId)))
			throw new IllegalArgumentException("��ѡ����:"
					+ (isEmpty(to) ? " �ֻ����� " : "")
					+ (isEmpty(templateId) ? " ģ��Id " : "") + "Ϊ��");
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";

		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, TemplateSMS);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("to", to);
				json.addProperty("templateId", templateId);
				if (datas != null) {
					StringBuilder sb = new StringBuilder("[");
					for (String s : datas) {
						sb.append("\"" + s + "\"" + ",");
					}
					sb.replace(sb.length() - 1, sb.length(), "]");
					JsonParser parser = new JsonParser();
					JsonArray Jarray = parser.parse(sb.toString())
							.getAsJsonArray();
					json.add("datas", Jarray);
				}
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<to>").append(to).append("</to>")
						.append("<templateId>").append(templateId)
						.append("</templateId>");
				if (datas != null) {
					sb.append("<datas>");
					for (String s : datas) {
						sb.append("<data>").append(s).append("</data>");
					}
					sb.append("</datas>");
				}
				sb.append("</TemplateSMS>").toString();
				requsetbody = sb.toString();
			}
			// ��ӡ����
			System.out.println("����İ��壺" + requsetbody);
			LoggerUtil.info("sendTemplateSMS Request body =  " + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			HttpResponse response = httpclient.execute(httppost);

			// ��ȡ��Ӧ��

			status = response.getStatusLine().getStatusCode();

			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������" + "Https���󷵻��룺" + status);
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LoggerUtil.info("sendTemplateSMS response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ��ȡ���ʺ���Ϣ
	 * 
	 * @param appId
	 *            ��ѡ���� Ӧ��Id
	 * @param friendlyName
	 *            ��ѡ���� ���ʺ�����
	 * @return
	 */
	public HashMap<String, Object> querySubAccount(String friendlyName) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(friendlyName))) {
			LoggerUtil.fatal("��ѡ����: ���ʺ����� Ϊ��");
			throw new IllegalArgumentException("��ѡ����: ���ʺ����� Ϊ��");
		}
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Query_SubAccountByName);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("friendlyName", friendlyName);
				requsetbody = json.toString();
			} else {
				requsetbody = "<?xml version='1.0' encoding='utf-8'?><SubAccount>"
						+ "<appId>"
						+ App_ID
						+ "</appId>"
						+ "<friendlyName>"
						+ friendlyName + "</friendlyName>" + "</SubAccount>";
			}
			LoggerUtil.info("querySubAccountByName Request body =  "
					+ requsetbody);
			System.out.println("����İ��壺" + requsetbody);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LoggerUtil.info("querySubAccount result " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}

	}

	/**
	 * ��ȡ���ʺ�
	 * 
	 * @param startNo
	 *            ��ѡ���� ��ʼ����ţ�Ĭ�ϴ�0��ʼ
	 * @param offset
	 *            ��ѡ���� һ�β�ѯ�������������С��1���������100��
	 * @return
	 */
	public HashMap<String, Object> getSubAccounts(String startNo, String offset) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Get_SubAccounts);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				if (!(isEmpty(startNo)))
					json.addProperty("startNo", startNo);
				if (!(isEmpty(offset)))
					json.addProperty("offset", offset);
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><SubAccount>");
				sb.append("<appId>").append(App_ID).append("</appId>");
				if (!(isEmpty(startNo)))
					sb.append("<startNo>").append(startNo).append("</startNo>");
				if (!(isEmpty(offset)))
					sb.append("<offset>").append(offset).append("</offset>");
				sb.append("</SubAccount>").toString();
				requsetbody = sb.toString();
			}
			LoggerUtil.info("GetSubAccounts Request body =  " + requsetbody);
			System.out.println("����İ��壺" + requsetbody);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("getSubAccounts result " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ��ȡ���ʺ���Ϣ��ѯ
	 * 
	 * @return
	 */
	public HashMap<String, Object> queryAccountInfo() {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IPΪ��");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "�˿ڴ���");
		}
		if ((isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "���ʺ�Ϊ��");
		}
		if ((isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "���ʺ�TOKENΪ��");
		}

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LoggerUtil.fatal(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpGet httpGet = (HttpGet) getHttpRequestBase(0, Account_Info);
			HttpResponse response = httpclient.execute(httpGet);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("queryAccountInfo response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * �������ʺ�
	 * 
	 * @param friendlyName
	 *            ��ѡ���� ���ʺ����ơ�����Ӣ����ĸ�Ͱ���������������ʺ�Ψһ���ƣ��Ƽ�ʹ�õ��������ַ
	 * @return
	 */
	public HashMap<String, Object> createSubAccount(String friendlyName) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if (isEmpty(friendlyName)) {
			LoggerUtil.fatal("��ѡ����: ���ʺ����� Ϊ��");
			throw new IllegalArgumentException("��ѡ����: ���ʺ����� Ϊ��");
		}

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LoggerUtil.error(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Create_SubAccount);
			String requsetbody = "";

			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("friendlyName", friendlyName);
				requsetbody = json.toString();
			} else {
				requsetbody = "<?xml version='1.0' encoding='utf-8'?><SubAccount>"
						+ "<appId>"
						+ App_ID
						+ "</appId>"
						+ "<friendlyName>"
						+ friendlyName + "</friendlyName>" + "</SubAccount>";
			}
			LoggerUtil.info("CreateSubAccount Request body =  " + requsetbody);
			System.out.println("����İ��壺" + requsetbody);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();

			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("createSubAccount response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ����ģ���ѯ
	 * 
	 * @param templateId
	 *            ��ѡ���� ģ��Id�������˲�����ѯȫ������ģ��
	 * @return
	 */
	public HashMap<String, Object> QuerySMSTemplate(String templateId) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LoggerUtil.error(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Query_SMSTemplate);
			String requsetbody = "";

			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("templateId", templateId);
				requsetbody = json.toString();
			} else {
				requsetbody = "<?xml version='1.0' encoding='utf-8'?><Request>"
						+ "<appId>" + App_ID + "</appId>" + "<templateId>"
						+ templateId + "</templateId>" + "</Request>";
			}
			LoggerUtil.info("QuerySMSTemplate Request body =  " + requsetbody);
			// ��ӡ����
			System.out.println("����İ��壺" + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			HttpResponse response = httpclient.execute(httppost);

			// ��ȡ��Ӧ��

			status = response.getStatusLine().getStatusCode();

			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("QuerySMSTemplate response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ����״̬��ѯ
	 * 
	 * @param callid
	 *            ��ѡ���� ����Id
	 * @param action
	 *            ��ѡ���� ��ѯ���֪ͨ�Ļص�url��ַ
	 * @return
	 */
	public HashMap<String, Object> QueryCallState(String callid, String action) {

		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(callid))) {
			LoggerUtil.fatal("��ѡ����: callid  Ϊ��");
			throw new IllegalArgumentException("��ѡ����: callid Ϊ��");
		}
		Callsid = callid;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LoggerUtil.error(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, queryCallState);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				JsonObject json2 = new JsonObject();
				json.addProperty("Appid", App_ID);
				json2.addProperty("callid", callid);
				if (!(isEmpty(action)))
					json2.addProperty("action", action);
				json.addProperty("QueryCallState", json2.toString());
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><Request>");
				sb.append("<Appid>").append(App_ID).append("</Appid>")
						.append("<QueryCallState callid=").append("\"")
						.append(callid).append("\"");
				if (action != null) {
					sb.append(" action=").append("\"").append(action)
							.append("\"").append("/");
				}

				sb.append("></Request>").toString();
				requsetbody = sb.toString();
			}
			LoggerUtil.info("queryCallState Request body = : " + requsetbody);
			System.out.println("����İ��壺" + requsetbody);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("billRecords response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * ���н����ѯ
	 * 
	 * @param callSid
	 *            ��ѡ���� ����Id
	 * @return
	 */
	public HashMap<String, Object> CallResult(String callSid) {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IPΪ��");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "�˿ڴ���");
		}
		if ((isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "���ʺ�Ϊ��");
		}
		if ((isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "���ʺ�TOKENΪ��");
		}
		Callsid = callSid;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LoggerUtil.fatal(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpGet httpGet = (HttpGet) getHttpRequestBase(0, callResult);
			HttpResponse response = httpclient.execute(httpGet);

			status = response.getStatusLine().getStatusCode();

			System.out.println("Https���󷵻�״̬�룺" + status);
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("queryAccountInfo response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	/**
	 * �����ļ��ϴ�
	 * 
	 * @param filename
	 *            ��ѡ���� �ļ���
	 * @param fis
	 *            ��ѡ���� ������������
	 * @return
	 */
	public String Filename;

	public HashMap<String, Object> MediaFileUpload(String filename,
			FileInputStream fis) {

		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(filename))) {
			LoggerUtil.fatal("��ѡ����: filename  Ϊ��");
			throw new IllegalArgumentException("��ѡ����: filename Ϊ��");
		}
		if (fis == null) {
			LoggerUtil.fatal("��ѡ����: fis  Ϊ��");
			throw new IllegalArgumentException("�������õ��ļ�");
		}

		Filename = filename;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LoggerUtil.error(e1.getMessage());
			throw new RuntimeException("��ʼ��httpclient�쳣" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					mediaFileUpload);

			LoggerUtil.info("MediaFileUpload Request body = : " + fis);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(fis);
			requestBody.setContentLength(fis.available());
			System.out.println("����İ��壺" + requestBody);

			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https���󷵻�״̬�룺" + status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172001", "�������");
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
			return getMyError("172002", "�޷���");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LoggerUtil.info("billRecords response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "���ذ������");
		}
	}

	private HashMap<String, Object> jsonToMap(String result) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		JsonParser parser = new JsonParser();
		JsonObject asJsonObject = parser.parse(result).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
		HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

		for (Map.Entry<String, JsonElement> m : entrySet) {
			if ("statusCode".equals(m.getKey())
					|| "statusMsg".equals(m.getKey()))
				hashMap.put(m.getKey(), m.getValue().getAsString());
			else {
				if ("SubAccount".equals(m.getKey())
						|| "totalCount".equals(m.getKey())
						|| "smsTemplateList".equals(m.getKey())
						|| "token".equals(m.getKey())
						|| "callSid".equals(m.getKey())
						|| "state".equals(m.getKey())
						|| "downUrl".equals(m.getKey())) {
					if (!"SubAccount".equals(m.getKey())
							&& !"smsTemplateList".equals(m.getKey()))
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					else {
						try {
							if ((m.getValue().toString().trim().length() <= 2)
									&& !m.getValue().toString().contains("[")) {
								hashMap2.put(m.getKey(), m.getValue()
										.getAsString());
								hashMap.put("data", hashMap2);
								break;
							}
							if (m.getValue().toString().contains("[]")) {
								hashMap2.put(m.getKey(), new JsonArray());
								hashMap.put("data", hashMap2);
								continue;
							}
							JsonArray asJsonArray = parser.parse(
									m.getValue().toString()).getAsJsonArray();
							ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
							for (JsonElement j : asJsonArray) {
								Set<Entry<String, JsonElement>> entrySet2 = j
										.getAsJsonObject().entrySet();
								HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
								for (Map.Entry<String, JsonElement> m2 : entrySet2) {
									hashMap3.put(m2.getKey(), m2.getValue()
											.getAsString());
								}
								arrayList.add(hashMap3);
							}
							hashMap2.put(m.getKey(), arrayList);
						} catch (Exception e) {
							JsonObject asJsonObject2 = parser.parse(
									m.getValue().toString()).getAsJsonObject();
							Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2
									.entrySet();
							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Map.Entry<String, JsonElement> m2 : entrySet2) {
								hashMap3.put(m2.getKey(), m2.getValue()
										.getAsString());
							}
							hashMap2.put(m.getKey(), hashMap3);
							hashMap.put("data", hashMap2);
						}

					}
					hashMap.put("data", hashMap2);
				} else {

					JsonObject asJsonObject2 = parser.parse(
							m.getValue().toString()).getAsJsonObject();
					Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2
							.entrySet();
					HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
					for (Map.Entry<String, JsonElement> m2 : entrySet2) {
						hashMap3.put(m2.getKey(), m2.getValue().getAsString());
					}
					if (hashMap3.size() != 0) {
						hashMap2.put(m.getKey(), hashMap3);
					} else {
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					}
					hashMap.put("data", hashMap2);
				}
			}
		}
		return hashMap;
	}

	/**
	 * @description ��xml�ַ���ת����map
	 * @param xml
	 * @return Map
	 */
	private HashMap<String, Object> xmlToMap(String xml) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // ���ַ���תΪXML
			Element rootElt = doc.getRootElement(); // ��ȡ���ڵ�
			HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
			ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
			for (Iterator i = rootElt.elementIterator(); i.hasNext();) {
				Element e = (Element) i.next();
				if ("statusCode".equals(e.getName())
						|| "statusMsg".equals(e.getName()))
					map.put(e.getName(), e.getText());
				else {
					if ("SubAccount".equals(e.getName())
							|| "TemplateSMS".equals(e.getName())
							|| "totalCount".equals(e.getName())
							|| "token".equals(e.getName())
							|| "callSid".equals(e.getName())
							|| "state".equals(e.getName())
							|| "downUrl".equals(e.getName())) {
						if (!"SubAccount".equals(e.getName())
								&& !"TemplateSMS".equals(e.getName())) {
							hashMap2.put(e.getName(), e.getText());
						} else if ("SubAccount".equals(e.getName())) {

							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2
									.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
							}
							arrayList.add(hashMap3);
							hashMap2.put("SubAccount", arrayList);
						} else if ("TemplateSMS".equals(e.getName())) {

							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2
									.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
							}
							arrayList.add(hashMap3);
							hashMap2.put("TemplateSMS", arrayList);
						}
						map.put("data", hashMap2);
					} else {

						HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
						for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
							Element e2 = (Element) i2.next();
							// hashMap2.put(e2.getName(),e2.getText());
							hashMap3.put(e2.getName(), e2.getText());
						}
						if (hashMap3.size() != 0) {
							hashMap2.put(e.getName(), hashMap3);
						} else {
							hashMap2.put(e.getName(), e.getText());
						}
						map.put("data", hashMap2);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	private HttpRequestBase getHttpRequestBase(int get, String action)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return getHttpRequestBase(get, action, AccountType.Accounts);
	}

	private HttpRequestBase getHttpRequestBase(int get, String action,
			AccountType mAccountType) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String timestamp = DateUtil.dateToStr(new Date(),
				DateUtil.DATE_TIME_NO_SLASH);
		EncryptUtil eu = new EncryptUtil();
		String sig = "";
		String acountName = "";
		String acountType = "";
		if (mAccountType == AccountType.Accounts) {
			acountName = ACCOUNT_SID;
			sig = ACCOUNT_SID + ACCOUNT_TOKEN + timestamp;
			acountType = "Accounts";
		} else {
			acountName = SUBACCOUNT_SID;
			sig = SUBACCOUNT_SID + SUBACCOUNT_Token + timestamp;
			acountType = "SubAccounts";
		}
		String signature = eu.md5Digest(sig);

		String url = getBaseUrl().append("/" + acountType + "/")
				.append(acountName).append("/" + action + "?sig=")
				.append(signature).toString();
		if (callResult.equals(action)) {
			url = url + "&callsid=" + Callsid;
		}
		if (queryCallState.equals(action)) {
			url = url + "&callid=" + Callsid;
		}
		if (mediaFileUpload.equals(action)) {
			url = url + "&appid=" + App_ID + "&filename=" + Filename;
		}
		LoggerUtil.info(getmethodName(action) + " url = " + url);
		// System.out.println(getmethodName(action) + " url = " + url);
		HttpRequestBase mHttpRequestBase = null;
		if (get == Request_Get)
			mHttpRequestBase = new HttpGet(url);
		else if (get == Request_Post)
			mHttpRequestBase = new HttpPost(url);
		if (IvrDial.equals(action)) {
			setHttpHeaderXML(mHttpRequestBase);
		} else if (mediaFileUpload.equals(action)) {
			setHttpHeaderMedia(mHttpRequestBase);
		} else {
			setHttpHeader(mHttpRequestBase);
		}

		String src = acountName + ":" + timestamp;

		String auth = eu.base64Encoder(src);
		mHttpRequestBase.setHeader("Authorization", auth);
		System.out.println("�����Url��" + mHttpRequestBase);// ��ӡUrl
		return mHttpRequestBase;

	}

	private String getmethodName(String action) {
		if (action.equals(Account_Info)) {
			return "queryAccountInfo";
		} else if (action.equals(Create_SubAccount)) {
			return "createSubAccount";
		} else if (action.equals(Get_SubAccounts)) {
			return "getSubAccounts";
		} else if (action.equals(Query_SubAccountByName)) {
			return "querySubAccount";
		} else if (action.equals(SMSMessages)) {
			return "sendSMS";
		} else if (action.equals(TemplateSMS)) {
			return "sendTemplateSMS";
		} else if (action.equals(LandingCalls)) {
			return "landingCalls";
		} else if (action.equals(VoiceVerify)) {
			return "voiceVerify";
		} else if (action.equals(IvrDial)) {
			return "ivrDial";
		} else if (action.equals(BillRecords)) {
			return "billRecords";
		} else {
			return "";
		}
	}

	private void setHttpHeaderXML(AbstractHttpMessage httpMessage) {
		httpMessage.setHeader("Accept", "application/xml");
		httpMessage.setHeader("Content-Type", "application/xml;charset=utf-8");
	}

	private void setHttpHeaderMedia(AbstractHttpMessage httpMessage) {
		if (BODY_TYPE == BodyType.Type_JSON) {
			httpMessage.setHeader("Accept", "application/json");
			httpMessage.setHeader("Content-Type",
					"application/octet-stream;charset=utf-8;");
		} else {
			httpMessage.setHeader("Accept", "application/xml");
			httpMessage.setHeader("Content-Type",
					"application/octet-stream;charset=utf-8;");
		}
	}

	private void setHttpHeader(AbstractHttpMessage httpMessage) {
		if (BODY_TYPE == BodyType.Type_JSON) {
			httpMessage.setHeader("Accept", "application/json");
			httpMessage.setHeader("Content-Type",
					"application/json;charset=utf-8");

		} else {
			httpMessage.setHeader("Accept", "application/xml");
			httpMessage.setHeader("Content-Type",
					"application/xml;charset=utf-8");
		}
	}

	private StringBuffer getBaseUrl() {
		StringBuffer sb = new StringBuffer("https://");
		sb.append(SERVER_IP).append(":").append(SERVER_PORT);
		sb.append("/2013-12-26");
		return sb;
	}

	private boolean isEmpty(String str) {
		return (("".equals(str)) || (str == null));
	}

	private HashMap<String, Object> getMyError(String code, String msg) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("statusCode", code);
		hashMap.put("statusMsg", msg);
		return hashMap;
	}

	private HashMap<String, Object> subAccountValidate() {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IPΪ��");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "�˿ڴ���");
		}
		if (isEmpty(SUBACCOUNT_SID))
			return getMyError("172008", "���ʺſ�");
		if (isEmpty(SUBACCOUNT_Token))
			return getMyError("172009", "���ʺ�TOKEN��");
		return null;
	}

	private HashMap<String, Object> accountValidate() {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IPΪ��");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "�˿ڴ���");
		}
		if ((isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "���ʺ�Ϊ��");
		}
		if ((isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "���ʺ�TOKENΪ��");
		}
		if ((isEmpty(App_ID))) {
			return getMyError("172012", "Ӧ��IDΪ��");
		}
		return null;
	}

	private void setBodyType(BodyType bodyType) {
		BODY_TYPE = bodyType;
	}
}