package is.apim.selfsignup.handler.internal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import is.apim.selfsignup.handler.CustomUserPostSelfRegistrationHandler;

@Component(
        name = "is.apim.selfsignup.handler",
        immediate = true)
public class CustomServiceComponent {
    @Activate
    protected void activate(ComponentContext componentContext) throws Exception {
        componentContext.getBundleContext()
                .registerService(AbstractEventHandler.class.getName(), new CustomUserPostSelfRegistrationHandler(),
                        null);
    }
}
