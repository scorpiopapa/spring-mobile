package com.joinway.mobile.bean.form;

import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import com.joinway.bean.logging.annotation.LogMask;

@ApiObject(name = "LoginForm", description = "用户登录信息")
public class LoginForm extends AuthForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6560550145301724019L;

	@ApiObjectField(description = "jpush的registerId")
	@NotBlank
	@LogMask
	String imId;
	
	@ApiObjectField(description = "手机类型:A-Android;I-IOS")
	@NotBlank
	@LogMask
	String mobileType;
	
	public String getImId() {
		return imId;
	}

	public void setImId(String imId) {
		this.imId = imId;
	}

	public String getMobileType() {
		return mobileType;
	}

	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}
}
