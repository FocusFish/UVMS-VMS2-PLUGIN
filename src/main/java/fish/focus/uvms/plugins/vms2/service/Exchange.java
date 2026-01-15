package fish.focus.uvms.plugins.vms2.service;

import fish.focus.schema.exchange.module.v1.ExchangeModuleMethod;
import fish.focus.schema.exchange.movement.v1.SetReportMovementType;
import fish.focus.schema.exchange.plugin.types.v1.PluginType;
import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.uvms.exchange.model.constant.ExchangeModelConstants;
import fish.focus.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import fish.focus.uvms.plugins.vms2.exception.PositionException;
import fish.focus.uvms.plugins.vms2.gen.model.VesselPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;
import java.time.Instant;

@RequestScoped
public class Exchange {

    private static final Logger LOGGER = LoggerFactory.getLogger(Exchange.class);

    @Resource(mappedName = "java:/" + ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE)
    private Queue exchangeQueue;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    ExchangeMapper exchangeMapper;

    public void save(VesselPosition vesselPosition) {
        SetReportMovementType reportType = exchangeMapper.getSetReportMovementType(vesselPosition);
        String position = ExchangeModuleRequestMapper.createSetMovementReportRequest(reportType, "VMS2",
                null, Instant.now(),  PluginType.SATELLITE_RECEIVER, "VMS2", null);

        sendPositionToExchange(position, ExchangeModuleMethod.SET_MOVEMENT_REPORT.value());
    }

    public void sendPositionToExchange(String text, String function) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, 1);
             MessageProducer producer = session.createProducer(exchangeQueue);
        ) {
            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY, function);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(message);

            LOGGER.debug("Position sent to exchange: {}", message);
        } catch (JMSException e) {
            LOGGER.error("[ Error when sending position to exchange. {}] {}", text, e.getMessage());
            throw new PositionException("Failed to send position to exchange" + e.getMessage());
        }
    }
}
