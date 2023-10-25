package com.tefo.keycloak;

import com.tefo.keycloak.constants.UserStatus;
import com.tefo.keycloak.utils.RestTemplateUtils;
import com.tefo.keycloak.utils.UserUtils;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;

import java.util.Optional;
import java.util.concurrent.Executors;

public class CustomEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;
    private final RealmProvider model;

    public CustomEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case LOGIN:
                processSuccessLoginEvent(event);
                break;
            case LOGIN_ERROR:
                processErrorLoginEvent(event);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {}

    @Override
    public void close() {
    }

    private void processSuccessLoginEvent(Event event) {
        UserModel user = getUserModel(event);
        if (!UserUtils.getUserStatusFromAttributes(user.getAttributes()).equals(UserStatus.ACTIVE.name())) {
            RestTemplateUtils.activateUser(UserUtils.getUserIdFromAttributes(user.getAttributes()));
        }
    }

    private void processErrorLoginEvent(Event event) {
        Optional<String> statusOptional = getUserModel(event).getAttributes().get(UserUtils.USER_STATUS_ATTRIBUTE).stream().findFirst();
        statusOptional.ifPresent(status -> {
            if (!(status.equals(UserStatus.INACTIVE.name()) || status.equals(UserStatus.NEW.name()))) {
                Executors.newSingleThreadExecutor().execute(() -> RestTemplateUtils.setUserStatus(event.getUserId(), event.getError(), status));
            }
        });
    }

    private UserModel getUserModel(Event event) {
        return session.users().getUserById(session.getContext().getRealm(), event.getUserId());
    }
}
