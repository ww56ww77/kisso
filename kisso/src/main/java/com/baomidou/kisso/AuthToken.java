/**
 * Copyright (c) 2011-2014, hubin (243194995@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.kisso;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.kisso.common.IpHelper;
import com.baomidou.kisso.common.encrypt.RSA;
import com.baomidou.kisso.common.util.RandomUtil;
import com.baomidou.kisso.exception.KissoException;

/**
 * <p>
 * SSO 跨域信任 Token
 * </p>
 * 
 * @author hubin
 * @Date 2015-11-30
 */
public class AuthToken extends Token {

	/* 用户ID */
	private String userId;

	/* 32 uuid */
	private String uuid;

	/* ras sign */
	private String rsaSign;

	protected AuthToken() {
	}

	/**
	 * 有参构造函数
	 * 
	 * @param request
	 * @param privateKey
	 *            RSA 密钥
	 */
	public AuthToken(HttpServletRequest request, String privateKey) {
		this.uuid = RandomUtil.get32UUID();
		this.setIp(IpHelper.getIpAddr(request));
		this.sign(privateKey);
	}

	/**
	 * 生成签名字节数组
	 * 
	 * @return byte[]
	 */
	public byte[] signByte() {
		StringBuffer sb = new StringBuffer(getUuid());
		sb.append("-").append(this.getIp());
		return sb.toString().getBytes();
	}

	/**
	 * 设置签名 rsaSign
	 * 
	 * @param privateKey
	 *            RSA 密钥
	 */
	public void sign(String privateKey) {
		try {
			this.rsaSign = RSA.sign(signByte(), privateKey);
		} catch (Exception e) {
			logger.error("sign error: ", e);
			throw new KissoException(" AuthToken RSA sign error. ");
		}
	}

	/**
	 * 验证 AuthToken 签名是否合法
	 * 
	 * @param publicKey
	 *            RSA 公钥
	 * @return
	 */
	public AuthToken verify(String publicKey) {
		try {
			/**
			 * RSA 验证摘要 是否合法
			 */
			if (RSA.verify(signByte(), publicKey, getRsaSign())) {
				return this;
			}
		} catch (Exception e) {
			logger.error("verify error: ", e);
			throw new KissoException(" AuthToken RSA verify error. ");
		}
		return null;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRsaSign() {
		return rsaSign;
	}

	public void setRsaSign(String rsaSign) {
		this.rsaSign = rsaSign;
	}
}