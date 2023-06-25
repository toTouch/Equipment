package com.xiliulou.afterserver.mns;

import com.xiliulou.iot.entity.ReceiverMessage;
import com.xiliulou.iot.mns.HardwareHandlerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AfterHardwareHandlerManager extends HardwareHandlerManager {
    @Override
    public boolean chooseCommandHandlerProcessReceiveMessage(ReceiverMessage receiverMessage) {
        return false;
    }
}
