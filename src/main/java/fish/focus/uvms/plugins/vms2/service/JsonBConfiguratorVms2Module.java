package fish.focus.uvms.plugins.vms2.service;

import fish.focus.uvms.commons.date.JsonBConfigurator;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonBConfiguratorVms2Module extends JsonBConfigurator {

    public JsonBConfiguratorVms2Module() {
        super();
    }

    @Override
    public Jsonb getContext(Class<?> type) {
        return JsonbBuilder.newBuilder()
                .build();
    }
}