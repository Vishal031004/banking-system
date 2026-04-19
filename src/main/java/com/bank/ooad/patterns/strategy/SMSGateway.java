package com.bank.ooad.patterns.strategy;

import com.bank.ooad.models.system.NotificationMessage;
import org.springframework.stereotype.Component;

@Component("smsGateway")
public class SMSGateway implements INotificationGateway {
    @Override
    public boolean dispatch(NotificationMessage message) {
        System.out.println("SMSGateway dispatching SMS: " + message.getPayload());
        return true;
    }

    @Override
    public String captureDeliveryStatus() {
        return "DELIVERED";
    }
}
