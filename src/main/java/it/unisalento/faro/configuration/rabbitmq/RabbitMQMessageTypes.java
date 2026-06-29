package it.unisalento.faro.configuration.rabbitmq;

public final class RabbitMQMessageTypes {

    // app → backend
    public static final String POSITION_UPDATE   = "POSITION_UPDATE";
    public static final String TASK_COMPLETED    = "TASK_COMPLETED";
    public static final String DIRECT_MESSAGE    = "DIRECT_MESSAGE";

    // backend → app
    public static final String TASK_ASSIGNED     = "TASK_ASSIGNED";
    public static final String AREA_ALERT        = "AREA_ALERT";
    public static final String AREA_SAFE         = "AREA_SAFE";
    public static final String AREA_UNAUTHORIZED = "AREA_UNAUTHORIZED";

    private RabbitMQMessageTypes() {}
}
