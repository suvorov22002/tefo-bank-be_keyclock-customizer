package com.tefo.keycloak;

import com.tefo.keycloak.constants.UserStatus;
import com.tefo.keycloak.dto.UserResponseDto;
import com.tefo.keycloak.utils.RestTemplateUtils;
import com.tefo.keycloak.utils.UserUtils;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.authentication.authenticators.util.AuthenticatorUtils;
import org.keycloak.models.UserModel;

public class CustomAuthenticator extends UsernamePasswordForm {

    private static final String PASSWORD_FIELD = "password";

    @Override
    protected boolean isDisabledByBruteForce(AuthenticationFlowContext context, UserModel user) {
        String bruteForceError = AuthenticatorUtils.getDisabledByBruteForceEventError(context, user);
        if (bruteForceError != null) {
            context.getEvent().user(user);
            context.getEvent().error(bruteForceError);
            Response challengeResponse;
            if (bruteForceError.equals("user_disabled")) {
                challengeResponse = challenge(context, "statusBlockedMessage", PASSWORD_FIELD);
            } else {
                challengeResponse = challenge(context, disabledByBruteForceError(), PASSWORD_FIELD);
            }
            context.forceChallenge(challengeResponse);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean enabledUser(AuthenticationFlowContext context, UserModel user) {
        if (isDisabledByBruteForce(context, user)) {
            return false;
        } else if (!user.isEnabled()) {
            context.getEvent().user(user);
            context.getEvent().error("user_disabled");
            String status = UserUtils.getUserStatusFromAttributes(user.getAttributes());
            if (UserStatus.INACTIVE.name().equals(status) || UserStatus.NEW.name().equals(status)) {
                context.getEvent().user(user);
                Response challengeResponse = challenge(context, "accessDeniedMessage", PASSWORD_FIELD);
                context.forceChallenge(challengeResponse);
            } else {
                Response challengeResponse = challenge(context, "accountDisabledMessage", PASSWORD_FIELD);
                context.forceChallenge(challengeResponse);
            }
            return false;
        } else {
            long activeUserSessions = context.getSession()
                    .sessions()
                    .getActiveUserSessions(context.getRealm(), context.getAuthenticationSession().getClient());
            UserResponseDto userInfo = RestTemplateUtils.getUserById(UserUtils.getUserIdFromAttributes(user.getAttributes()), context.getRealm().getName());
            if (!userInfo.isAllowMultipleSessions() && activeUserSessions >= 1) {
                Response challengeResponse = challenge(context, "accessDeniedMessage", PASSWORD_FIELD);
                context.forceChallenge(challengeResponse);
                return false;
            }
            return true;
        }
    }
}
