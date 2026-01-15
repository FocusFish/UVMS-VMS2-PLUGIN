package fish.focus.uvms.plugins.vms2;

import fish.focus.schema.exchange.module.v1.ExchangeBaseRequest;
import fish.focus.schema.exchange.module.v1.ExchangeModuleMethod;
import fish.focus.schema.exchange.module.v1.SetCommandRequest;
import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import static org.junit.Assert.fail;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_LOOKUP_STR, propertyValue = MessageConstants.QUEUE_EXCHANGE_EVENT),
})
public class MockExchange implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(MockExchange.class);

    @Override
    public void onMessage(Message message) {
        LOG.info("received message: {}", message);

        try {
            TextMessage textMessage = (TextMessage) message;
            String function = textMessage.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY);
            ExchangeModuleMethod exchangeMethod = (function != null) ? ExchangeModuleMethod.valueOf(function) : tryConsumeExchangeBaseRequest(textMessage).getMethod();
            switch (exchangeMethod) {
                case SET_MOVEMENT_REPORT:
                    DataHolder.HAS_RECEIVED_MOVEMENT.set(true);
                    LOG.info("set movement report {}", DataHolder.HAS_RECEIVED_MOVEMENT.get());
                    break;
                default:
                    LOG.error("[ Didn't expect to get this method: {} ] ", exchangeMethod);
            }
        } catch (JMSException e) {
            fail("Got JMS exception: " + e.getMessage());
        }
    }

    private ExchangeBaseRequest tryConsumeExchangeBaseRequest(TextMessage textMessage) {
        try {
            if (textMessage.getText().startsWith("<ns2:AcknowledgeResponse xmlns:ns2=\"urn:plugin.exchange.schema.focus.fish:v1\">")) {
                ExchangeBaseRequest plugin = new SetCommandRequest();
                plugin.setMethod(ExchangeModuleMethod.PLUGIN_SET_COMMAND_ACK);
                return plugin;
            }
            return JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeBaseRequest.class);
        } catch (Exception e) {
            LOG.error("Error when consuming ExchangeBaseRequest", e);
            return null;
        }
    }
}
