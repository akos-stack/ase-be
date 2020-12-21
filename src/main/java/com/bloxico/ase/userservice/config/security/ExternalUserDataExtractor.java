package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.util.List;
import java.util.Map;

public enum ExternalUserDataExtractor {

    GOOGLE {
        @Override
        public String getId(Map<String, Object> attributes) {
            return (String) attributes.get("sub");
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            return (String) attributes.get("email");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return (String) attributes.get("name");
        }
    },

    FACEBOOK {
        @Override
        public String getId(Map<String, Object> attributes) {
            return (String) attributes.get("id");
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            return (String) attributes.get("email");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return (String) attributes.get("name");
        }
    },

    GITHUB {
        @Override
        public String getId(Map<String, Object> attributes) {
            return ((Integer) attributes.get("id")).toString();
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            return (String) attributes.get("email");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return (String) attributes.get("name");
        }
    },

    LINKEDIN {
        @Override
        public String getId(Map<String, Object> attributes) {
            return null;
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            @SuppressWarnings("unchecked")
            var elements = (List<Map<String, Map<String, String>>>) attributes.get("elements");
            return elements
                    .get(0)
                    .get("handle~")
                    .get("emailAddress");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return getEmail(attributes);
        }
    };

    public static ExternalUserDataExtractor of(String name) {
        return valueOf(name.toUpperCase());
    }

    public abstract String getId(Map<String, Object> attributes);

    public abstract String getEmail(Map<String, Object> attributes);

    public abstract String getName(Map<String, Object> attributes);

    public UserProfile validateUserProfile(UserProfile user) {
        if (!user.getProvider().equalsIgnoreCase(name()))
            throw new InternalAuthenticationServiceException(
                    "Use " + user.getProvider() + " provider");
        if (!user.getEnabled())
            throw new InternalAuthenticationServiceException(
                    "User is disabled");
        return user;
    }

    public UserProfile updatedUserProfile(UserProfile user, Map<String, Object> attributes) {
        user.setName(getName(attributes));
        user.setUpdaterId(user.getId());
        return user;
    }

    public UserProfile newUserProfile(Map<String, Object> attributes) {
        var user = new UserProfile();
        user.setProvider(name());
        user.setProviderId(getId(attributes));
        user.setEmail(getEmail(attributes));
        user.setName(getName(attributes));
        user.setEnabled(true);
        return user;
    }

}
