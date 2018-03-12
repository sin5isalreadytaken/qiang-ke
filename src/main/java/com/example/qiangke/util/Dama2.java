package com.example.qiangke.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;


public class Dama2 {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String URL_CODE = "http://10.10.23.70:8521?softwareid={sid}&typeid={tid}";

	private static Dama2 INSTANCE = null;
	
	private static Dama2 getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new Dama2();
		}
		return INSTANCE;
	}
	
	private HttpClient m_pHttp = new DefaultHttpClient();

	private synchronized String getCode(byte[] by, int nType) {
		String strUrl = URL_CODE.replace("{sid}", "appstore");
		strUrl = strUrl.replace("{tid}", nType + "");
		try {
			HttpPost pPost = new HttpPost(strUrl);
			ByteArrayInputStream bis = new ByteArrayInputStream(by);
			HttpEntity pEntity = new InputStreamEntity(bis);
			pPost.setEntity(pEntity);
			HttpResponse pResp = m_pHttp.execute(pPost);
			String strCode = IOUtils.toString(pResp.getEntity().getContent());
			pPost.abort();
			if (!StringUtils.isEmpty(strCode)) {
				JSONObject json= JSON.parseObject(strCode);
				return json.getString("ans");
			}
		} catch (Exception e) {
			logger.info("get code error: " + strUrl + ", " + e.getMessage(), e);
		}
		return null;
	}

	public static String getRandomCode(byte[] by, int nCodeType) {
		return Dama2.getInstance().getCode(by, nCodeType);
	}

}
