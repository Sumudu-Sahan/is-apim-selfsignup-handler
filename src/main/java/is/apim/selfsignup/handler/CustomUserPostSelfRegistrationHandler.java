package is.apim.selfsignup.handler;

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.core.handler.InitConfig;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.recovery.IdentityRecoveryConstants;
import org.wso2.carbon.identity.recovery.IdentityRecoveryServerException;
import org.wso2.carbon.identity.recovery.util.Utils;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import java.util.Arrays;
import java.util.List;

public class CustomUserPostSelfRegistrationHandler extends AbstractEventHandler {

    @Override
    public String getName() {
        return "customUserPostSelfRegistration";
    }

    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        String tenantDomain =
                (String) event.getEventProperties().get(IdentityEventConstants.EventProperty.TENANT_DOMAIN);
        String userName =
                (String) event.getEventProperties().get(IdentityEventConstants.EventProperty.USER_NAME);

        // The handler should be called as a post add user event.
        if (IdentityEventConstants.Event.POST_ADD_USER.equals(event.getEventName())) {
            try {
                executeUserRegistrationWorkflow(tenantDomain, userName);
            } catch (IdentityRecoveryServerException e) {
                throw new IdentityEventException(HandlerConstants.ERROR_MSG_ADDING_CUSTOM_ROLES_TO_USER, e);
            }
        }
    }

    /**
     * This method adds new role to the existing user roles
     *
     * @param tenantDomain tenant domain extracted from the event
     * @param userName     username extracted from the event
     * @throws org.wso2.carbon.identity.recovery.IdentityRecoveryServerException when unable to
     *                                                                           retrieve userStoreManager instance
     */
    private void executeUserRegistrationWorkflow(String tenantDomain, String userName) throws IdentityRecoveryServerException {
        try {
            // Realm service is used to get the UserStoreManager
            UserStoreManager userStoreManager = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm().getUserStoreManager();
            // Start a tenant flow
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            carbonContext.setTenantDomain(tenantDomain, true);

            if (userStoreManager.isExistingUser(userName)) {
                //This code segment will execute if the user is in the userstore
                List<String> roleList = Arrays.asList(userStoreManager.getRoleListOfUser(userName));//You can get all the assigned roles of the user as a list

                if (roleList.contains(HandlerConstants.SELF_SIGNUP_ROLE) && !roleList.contains(HandlerConstants.TEST_SUBSCRIBER_GROUP)) { //Internal/selfsignup role will assign when doing the user self registration
                    String[] rolesThatNeedToAdd = {HandlerConstants.TEST_SUBSCRIBER_GROUP}; //You can add user roles that need to assign to the user
                    String[] rolesThatNeedToRemove = {}; //You can add user roles that need to unassigned from the user
                    userStoreManager.updateRoleListOfUser(userName, rolesThatNeedToRemove, rolesThatNeedToAdd);
                }
            }
        } catch (UserStoreException e) {
            throw Utils.handleServerException(IdentityRecoveryConstants.ErrorMessages.ERROR_CODE_UNEXPECTED, userName, e);
        } finally {
            Utils.clearArbitraryProperties();
            PrivilegedCarbonContext.endTenantFlow();
        }
    }


    @Override
    public void init(InitConfig configuration) throws IdentityRuntimeException {
        super.init(configuration);
    }

    @Override
    public int getPriority(MessageContext messageContext) {
        return HandlerConstants.HIGH_PRIORITY;
    }

}
