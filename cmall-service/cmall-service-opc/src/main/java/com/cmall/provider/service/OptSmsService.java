
package com.cmall.provider.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;

/**
 * The interface Opt sms service.
 *
 */
public interface OptSmsService {
	/**
	 * Send sms send sms response.
	 *
	 * @param sendSmsRequest the send sms request
	 *
	 * @return the send sms response
	 */
	SendSmsResponse sendSms(SendSmsRequest sendSmsRequest);
}
