package com.bank.ooad.patterns.strategy;

import com.bank.ooad.models.system.NotificationMessage;
import org.springframework.stereotype.Component;

@Component("emailGateway")
public class EmailGateway implements INotificationGateway {
    @Override
    public boolean dispatch(NotificationMessage message) {
        System.out.println("EmailGateway dispatching email: " + message.getPayload());
        return true;
    }

    @Override
    public String captureDeliveryStatus() {
        return "DELIVERED";
    }
}
