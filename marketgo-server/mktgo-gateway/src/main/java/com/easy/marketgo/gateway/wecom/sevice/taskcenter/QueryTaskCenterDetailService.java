package com.easy.marketgo.gateway.wecom.sevice.taskcenter;

import com.easy.marketgo.common.enums.WeComMassTaskMetricsType;
import com.easy.marketgo.common.enums.WeComMediaTypeEnum;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComChangeStatusRequest;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterContentClientResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterDetailClientResponse;
import com.easy.marketgo.react.model.response.WeComTaskCenterDetailResponse;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/6/23 1:29 PM
 * Describe:
 */

@Slf4j
@Service
public class QueryTaskCenterDetailService {

    @Autowired
    private WeComClientTaskCenterService weComClientTaskCenterService;

    public BaseResponse listTaskCenter(List<String> type,
                                       List<String> taskTypes,
                                       Integer pageNum,
                                       Integer pageSize,
                                       String corpId,
                                       List<String> statuses,
                                       String keyword,
                                       String memberId,
                                       List<String> createUserIds,
                                       String sortKey,
                                       String sortOrder,
                                       String startTime,
                                       String endTime) {
        return null;
    }

    public BaseResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid, String uuid) {
        WeComTaskCenterDetailResponse response = weComClientTaskCenterService.getTaskCenterDetails(corpId, memberId,
                taskUuid, uuid);
        WeComTaskCenterDetailClientResponse clientResponse = new WeComTaskCenterDetailClientResponse();
        BeanUtils.copyProperties(response, clientResponse);
        List<WeComTaskCenterDetailClientResponse.AttachmentsMessage> attachmentsMessageList = new ArrayList<>();
        for (WeComTaskCenterDetailResponse.AttachmentsMessage item : response.getAttachments()) {
            WeComTaskCenterDetailClientResponse.AttachmentsMessage message =
                    new WeComTaskCenterDetailClientResponse.AttachmentsMessage();
            BeanUtils.copyProperties(item, message);
            if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.TEXT.getType())) {
                WeComTaskCenterDetailClientResponse.TextMessage textMessage =
                        new WeComTaskCenterDetailClientResponse.TextMessage();
                BeanUtils.copyProperties(item.getText(), textMessage);
                message.setText(textMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.IMAGE.getType())) {
                WeComTaskCenterDetailClientResponse.ImageAttachmentsMessage imageMessage =
                        new WeComTaskCenterDetailClientResponse.ImageAttachmentsMessage();
                BeanUtils.copyProperties(item.getImage(), imageMessage);
                message.setImage(imageMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.VIDEO.getType())) {
                WeComTaskCenterDetailClientResponse.VideoAttachmentsMessage videoMessage =
                        new WeComTaskCenterDetailClientResponse.VideoAttachmentsMessage();
                BeanUtils.copyProperties(item.getVideo(), videoMessage);
                message.setVideo(videoMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.FILE.getType())) {
                WeComTaskCenterDetailClientResponse.FileAttachmentsMessage fileMessage =
                        new WeComTaskCenterDetailClientResponse.FileAttachmentsMessage();
                BeanUtils.copyProperties(item.getFile(), fileMessage);
                message.setFile(fileMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.MINIPROGRAM.getType())) {
                WeComTaskCenterDetailClientResponse.MiniProgramAttachmentsMessage miniMessage =
                        new WeComTaskCenterDetailClientResponse.MiniProgramAttachmentsMessage();
                BeanUtils.copyProperties(item.getMiniProgram(), miniMessage);
                message.setMiniProgram(miniMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.LINK.getType())) {
                WeComTaskCenterDetailClientResponse.LinkAttachmentsMessage linkMessage =
                        new WeComTaskCenterDetailClientResponse.LinkAttachmentsMessage();
                BeanUtils.copyProperties(item.getLink(), linkMessage);
                message.setLink(linkMessage);

            }
//            else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.VOICE.getType())) {
//                WeComTaskCenterDetailClientResponse.VideoAttachmentsMessage videoAttachmentsMessage =
//                        new WeComTaskCenterDetailClientResponse.VideoAttachmentsMessage();
//                BeanUtils.copyProperties(item.get(), videoAttachmentsMessage);
//                message.setVideo(videoAttachmentsMessage);
//
//            }
            attachmentsMessageList.add(message);
        }
        clientResponse.setAttachments(attachmentsMessageList);
        clientResponse.setTaskName(response.getTaskName());
        clientResponse.setTaskUuid(response.getTaskUuid());
        List<WeComTaskCenterDetailClientResponse.WeComTaskCenterMessage> taskList = new ArrayList<>();
        WeComTaskCenterDetailClientResponse.WeComTaskCenterMessage taskMessage =
                new WeComTaskCenterDetailClientResponse.WeComTaskCenterMessage();
        taskMessage.setPlanTime(response.getPlanTime());
        taskMessage.setTargetTime(response.getTargetTime());
        taskMessage.setTargetType(response.getTargetType());
        taskMessage.setUuid(response.getUuid());
        List<WeComTaskCenterDetailClientResponse.ExternalUserMessage> externalUserMessageList = new ArrayList<>();
        for (WeComTaskCenterDetailResponse.ExternalUserMessage item : response.getExternalUserId()) {
            WeComTaskCenterDetailClientResponse.ExternalUserMessage user =
                    new WeComTaskCenterDetailClientResponse.ExternalUserMessage();
            BeanUtils.copyProperties(item, user);
            externalUserMessageList.add(user);
        }
        taskMessage.setExternalUserId(externalUserMessageList);
        taskList.add(taskMessage);
        clientResponse.setTaskList(taskList);
        return BaseResponse.success(clientResponse);
    }

    public BaseResponse changeTaskCenterMemberStatus(String corpId, WeComChangeStatusRequest request) {
        if (request.getType().equalsIgnoreCase(WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue())) {
            weComClientTaskCenterService.changeTaskCenterMemberStatus(corpId, request.getMemberId(),
                    request.getTaskUuid(), request.getUuid(), request.getSentTIme(), request.getStatus());
        } else {
            weComClientTaskCenterService.changeTaskCenterExternalUserStatus(corpId, request.getMemberId(),
                    request.getTaskUuid(), request.getUuid(), request.getExternalUserId(), request.getSentTIme(),
                    request.getStatus());
        }
        return BaseResponse.success();
    }

    public BaseResponse getTaskCenterContent(String corpId, String memberId, String taskUuid) {
        List<WeComTaskCenterDetailResponse> responses = weComClientTaskCenterService.getTaskCenterContent(corpId,
                memberId,
                taskUuid);
        WeComTaskCenterContentClientResponse clientResponse = new WeComTaskCenterContentClientResponse();
        BeanUtils.copyProperties(responses.get(0), clientResponse);
        List<WeComTaskCenterContentClientResponse.AttachmentsMessage> attachmentsMessageList = new ArrayList<>();
        for (WeComTaskCenterDetailResponse.AttachmentsMessage item : responses.get(0).getAttachments()) {
            WeComTaskCenterContentClientResponse.AttachmentsMessage message =
                    new WeComTaskCenterContentClientResponse.AttachmentsMessage();
            BeanUtils.copyProperties(item, message);
            if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.TEXT.getType())) {
                WeComTaskCenterContentClientResponse.TextMessage textMessage =
                        new WeComTaskCenterContentClientResponse.TextMessage();
                BeanUtils.copyProperties(item.getText(), textMessage);
                message.setText(textMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.IMAGE.getType())) {
                WeComTaskCenterContentClientResponse.ImageAttachmentsMessage imageMessage =
                        new WeComTaskCenterContentClientResponse.ImageAttachmentsMessage();
                BeanUtils.copyProperties(item.getImage(), imageMessage);
                message.setImage(imageMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.VIDEO.getType())) {
                WeComTaskCenterContentClientResponse.VideoAttachmentsMessage videoMessage =
                        new WeComTaskCenterContentClientResponse.VideoAttachmentsMessage();
                BeanUtils.copyProperties(item.getVideo(), videoMessage);
                message.setVideo(videoMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.FILE.getType())) {
                WeComTaskCenterContentClientResponse.FileAttachmentsMessage fileMessage =
                        new WeComTaskCenterContentClientResponse.FileAttachmentsMessage();
                BeanUtils.copyProperties(item.getFile(), fileMessage);
                message.setFile(fileMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.MINIPROGRAM.getType())) {
                WeComTaskCenterContentClientResponse.MiniProgramAttachmentsMessage miniMessage =
                        new WeComTaskCenterContentClientResponse.MiniProgramAttachmentsMessage();
                BeanUtils.copyProperties(item.getMiniProgram(), miniMessage);
                message.setMiniProgram(miniMessage);

            } else if (item.getMsgType().equalsIgnoreCase(WeComMediaTypeEnum.LINK.getType())) {
                WeComTaskCenterContentClientResponse.LinkAttachmentsMessage linkMessage =
                        new WeComTaskCenterContentClientResponse.LinkAttachmentsMessage();
                BeanUtils.copyProperties(item.getLink(), linkMessage);
                message.setLink(linkMessage);

            }
            attachmentsMessageList.add(message);
        }
        clientResponse.setAttachments(attachmentsMessageList);
        return BaseResponse.success(clientResponse);
    }

}
