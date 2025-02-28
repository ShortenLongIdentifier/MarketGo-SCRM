package com.easy.marketgo.web.service.project.impl;

import cn.hutool.core.util.IdUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.DateFormatUtils;
import com.easy.marketgo.core.entity.ProjectConfigEntity;
import com.easy.marketgo.core.entity.WeComSysUserEntity;
import com.easy.marketgo.core.entity.WeComUserTenantLinkEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.repository.user.WeComSysUserRepository;
import com.easy.marketgo.core.repository.wecom.ProjectConfigRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserTenantLinkRepository;
import com.easy.marketgo.web.client.ClientRequestContextHolder;
import com.easy.marketgo.web.model.request.ProjectCreateRequest;
import com.easy.marketgo.web.model.response.ProjectFetchResponse;
import com.easy.marketgo.web.service.project.IProjectService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-18 20:11:35
 * @description : ProjectServiceImpl.java
 */
@Component
@Log4j2
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private WeComSysUserRepository sysUserRepository;
    @Autowired
    private WeComUserTenantLinkRepository userTenantLinkRepository;
    @Autowired
    private ProjectConfigRepository projectConfigRepository;

    @Override
    public ProjectFetchResponse fetchProjects() {

        String userName = ClientRequestContextHolder.current().getUserName();
        ProjectFetchResponse response = ProjectFetchResponse.builder().build();

        WeComSysUserEntity userEntity = sysUserRepository.queryByUserName(userName);
        if (Objects.isNull(userEntity)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS);
        }
        List<WeComUserTenantLinkEntity> linkEntities = userTenantLinkRepository.findByUserUuid(userEntity.getUuid());

        if (CollectionUtils.isEmpty(linkEntities)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY);
        }

        WeComUserTenantLinkEntity linkEntity = linkEntities
                .stream().findAny()
                .orElseThrow((Supplier<RuntimeException>) () -> new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY));

        response.setTenantUuid(linkEntity.getTenantUuid());

        List<ProjectConfigEntity> configEntities = projectConfigRepository.findByTenantUuid(linkEntity.getTenantUuid());
        List<ProjectFetchResponse.ProjectInfo> projectInfos = configEntities
                .stream()
                .map(c -> ProjectFetchResponse
                        .ProjectInfo.builder()
                        .projectName(c.getName())
                        .projectUuid(c.getUuid())
                        .status(c.getStatus())
                        .desc(c.getDesc())
                        .type(c.getType())
                        .createTime(DateFormatUtils.formatDate(c.getCreateTime()))
                        .build()).collect(Collectors.toList());
        response.setProjects(projectInfos);
        return response;
    }

    @Override
    public BaseResponse createProject(ProjectCreateRequest projectCreateRequest) {

        ProjectConfigEntity projectConfigEntity = new ProjectConfigEntity();
        projectConfigEntity.setDesc(projectCreateRequest.getDesc());
        projectConfigEntity.setName(projectCreateRequest.getName());
        projectConfigEntity.setUuid(IdUtil.simpleUUID());
        projectConfigRepository.save(projectConfigEntity);
        return BaseResponse.success();
    }
}
