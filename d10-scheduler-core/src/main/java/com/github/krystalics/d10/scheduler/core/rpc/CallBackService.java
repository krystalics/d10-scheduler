package com.github.krystalics.d10.scheduler.core.rpc;

import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @Author linjiabao001
 * @Date 2022/1/25
 * @Description
 */
@Service
@RpcService
public class CallBackService {

    @Autowired
    private InstanceMapper instanceMapper;

    /**
     * 从远端executor执行完成的任务实例
     * 成功：更新状态、可以增加
     * 失败：尝试check有没有剩余的重试次数，重新创建一个实例进入数据库
     *
     *  fixme 还是要考虑节点失败对状态更新的影响、有可能出现更新了状态但是
     *   - 没有生成新的实例
     *   - 没有通知用户
     * 两种情况，
     * @param instance 运行完成的instance
     */
    public void callback(VersionInstance instance) {
        Instance i = new Instance();
        i.setInstanceId(instance.getInstanceId());
        i.setRunEndTime(new Date());

        switch (Objects.requireNonNull(VersionState.fromInt(instance.getState()))) {
            case SUCCESS:
                i.setState(VersionState.SUCCESS.getState());
                instanceMapper.update(i);
                break;
            case FAILED:
                i.setState(VersionState.FAILED.getState());
                instanceMapper.update(i);

                break;
            default:
                break;
        }
    }

    private void processFailed(VersionInstance instance){

    }
}
