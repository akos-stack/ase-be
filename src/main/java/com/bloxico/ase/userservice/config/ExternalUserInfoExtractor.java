package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.entity.user.UserProfile;

import java.util.Map;

public enum ExternalUserInfoExtractor {

    GOOGLE {
        @Override
        public String getId(Map<String, Object> attributes) {
            return (String) attributes.get("sub");
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            return (String) attributes.get("name");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return (String) attributes.get("email");
        }
    },

    FACEBOOK {
        @Override
        public String getId(Map<String, Object> attributes) {
            return (String) attributes.get("id");
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            return (String) attributes.get("name");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return (String) attributes.get("email");
        }
    },

    GITHUB {
        @Override
        public String getId(Map<String, Object> attributes) {
            return ((Integer) attributes.get("id")).toString();
        }

        @Override
        public String getEmail(Map<String, Object> attributes) {
            return (String) attributes.get("name");
        }

        @Override
        public String getName(Map<String, Object> attributes) {
            return (String) attributes.get("email");
        }
    };

    public static ExternalUserInfoExtractor of(String name) {
        return valueOf(name.toUpperCase());
    }

    public abstract String getId(Map<String, Object> attributes);

    public abstract String getEmail(Map<String, Object> attributes);

    public abstract String getName(Map<String, Object> attributes);

    public UserProfile newUserProfile(Map<String, Object> attributes) {
        var user = new UserProfile();
        user.setProvider(name());
        user.setProviderId(getId(attributes));
        user.setEmail(getEmail(attributes));
        user.setName(getName(attributes));
        user.setEnabled(true);
        return user;
    }

    public UserProfile updateUserProfile(UserProfile user, Map<String, Object> attributes, String provider) {
        if (!provider.equalsIgnoreCase(user.getProvider()))
            // TODO extend
            throw new RuntimeException("Please use your " + user.getProvider() + " account to login.");
        if (!user.getEnabled())
            // TODO extend
            throw new RuntimeException("User is disabled");
        user.setName(getName(attributes));
        return user;
    }

}
