package com.github.krystalics.d10.scheduler.executor.rpc;

import com.github.krystalics.d10.scheduler.common.constant.TaskType;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.common.exception.InstanceUpdateException;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.executor.common.Constants;
import com.github.krystalics.d10.scheduler.executor.service.ResourceService;
import com.github.krystalics.d10.scheduler.executor.utils.LogUtils;
import com.github.krystalics.d10.scheduler.executor.utils.OSUtils;
import com.github.krystalics.d10.scheduler.executor.worker.InstanceRunPool;
import com.github.krystalics.d10.scheduler.rpc.api.ITaskRunnerService;
import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
@Service
@RpcService("ITaskRunnerService")
public class TaskRunnerServiceImpl implements ITaskRunnerService {
    private static final Logger log = LoggerFactory.getLogger(TaskRunnerServiceImpl.class);

    @Autowired
    private InstanceMapper instanceMapper;

    @Autowired
    private ResourceService resourceService;

    @Override
    public boolean addInstance(VersionInstance instance) {
        log.info("i get the instance {}", instance);
        if (!machineResourceCheck(instance.getCpuAvg(), instance.getMemoryAvg())) {
            return false;
        }

        if (resourceService.resourceApply(instance.getCpuAvg(), instance.getMemoryAvg())) {
            InstanceRunPool.add(instance);
            updateInstance(instance);
            return true;
        }

        return false;
    }

    private void updateInstance(VersionInstance instance) {
        try {
            Instance i = new Instance();
            i.setInstanceId(instance.getInstanceId());
            i.setState(VersionState.RUNNING.getState());
            Date now = new Date();
            i.setRunStartTime(now);
            i.setNode(IPUtils.getHost());
            String jobAddress = LogUtils.logFile(TaskType.fromInt(instance.getType()).getDesc(), instance.getInstanceId(), now);
            i.setJobLogAddress(jobAddress);

            instanceMapper.update(i);
        } catch (Exception e) {
            //??????????????????????????????????????????
            resourceService.resourceReturn(instance.getCpuAvg(), instance.getMemoryAvg());
            throw new InstanceUpdateException("instance update error " + e);
        }
    }

    /**
     * ?????????????????????check?????????????????????
     * ???20%???memory????????????
     * <p>
     * keypoint ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????cpu????????????
     * ??????????????????????????????????????????????????????
     *
     * @param memoryApply ??????????????????????????????cpu
     * @return ???????????????????????????true
     */
    private boolean machineResourceCheck(double cpuApply, double memoryApply) {
        int cmax = OSUtils.cpuLogicalProcessorCount();
        double cu = OSUtils.cpuUsage();
        double mu = OSUtils.memoryUsage();
        double mmax = OSUtils.memoryLogicalMaxSize();

        double cuf = (cpuApply / cmax) + cu;
        if (cuf >= Constants.CPU_USAGE_LIMIT) {
            return false;
        }

        double muf = (memoryApply / mmax) + mu;
        if (muf >= Constants.MEMORY_USAGE_LIMIT) {
            return false;
        }

        return true;
    }
}
