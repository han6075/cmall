
package com.cmall.provider.job.dataflow;

import com.google.common.collect.Lists;
import com.cmall.DateUtil;
import com.cmall.elastic.lite.JobParameter;
import com.cmall.elastic.lite.annotation.ElasticJobConfig;
import com.cmall.elastic.lite.job.AbstractBaseDataflowJob;
import com.cmall.provider.model.dto.MessageTaskQueryDto;
import com.cmall.provider.model.enums.JobTaskStatusEnum;
import com.cmall.provider.model.enums.MqSendStatusEnum;
import com.cmall.provider.service.TpcMqMessageService;
import com.cmall.provider.service.UacRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理待确认的消息数据.
 *
 */
@Slf4j
@Component
@ElasticJobConfig(cron = "0 0/10 * * * ?", jobParameter = "fetchNum=1000")
public class HandleWaitingConfirmMessageJob extends AbstractBaseDataflowJob<String> {
	@Resource
	private TpcMqMessageService tpcMqMessageService;
	@Resource
	private UacRpcService uacRpcService;
	@Value("${cmall.message.handleTimeout}")
	private int timeOutMinute;
	private static final String PID_UAC = "PID_UAC";

	/**
	 * Fetch job data list.
	 *
	 * @param jobParameter the job parameter
	 *
	 * @return the list
	 */
	@Override
	protected List<String> fetchJobData(JobParameter jobParameter) {
		MessageTaskQueryDto query = new MessageTaskQueryDto();
		query.setCreateTimeBefore(DateUtil.getBeforeTime(timeOutMinute));
		query.setMessageStatus(MqSendStatusEnum.WAIT_SEND.sendStatus());
		query.setFetchNum(jobParameter.getFetchNum());
		query.setShardingItem(jobParameter.getShardingItem());
		query.setShardingTotalCount(jobParameter.getShardingTotalCount());
		query.setTaskStatus(JobTaskStatusEnum.TASK_CREATE.status());
		query.setProducerGroup(PID_UAC);
		return tpcMqMessageService.queryWaitingConfirmMessageKeyList(query);
	}

	/**
	 * Process job data.
	 *
	 * @param messageKeyList the message key list
	 */
	@Override
	protected void processJobData(List<String> messageKeyList) {
		if (messageKeyList == null) {
			return;
		}
		List<String> resendMessageList = uacRpcService.queryWaitingConfirmMessageKeyList(messageKeyList);
		if (resendMessageList == null) {
			resendMessageList = Lists.newArrayList();
		}
		messageKeyList.removeAll(resendMessageList);
		tpcMqMessageService.handleWaitingConfirmMessage(messageKeyList, resendMessageList);
	}
}