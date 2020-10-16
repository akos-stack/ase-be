package com.bloxico.userservice.web.error;

public enum ErrorCodes {

    /**
     * USER
     */
    USER_EXISTS("1", "Upon registration, when given email already exists."),
    USER_DOES_NOT_EXIST("2", "For various situations, when user passed by parameter (email) does not exist."),
    OLD_PASSWORD_DOES_NOT_MATCH("3", "When updating known password, if provided old password does not match with (current) password."),
    MATCH_REGISTRATION_PASSWORD_ERROR("4", "When registering user, if password and matchPassword values are not valid"),
    INVALID_USERNAME("5", "Upon \"Log In\" into the application, if the provided username or password are not valid. (This should be further tested, it may be occuring for wrong username only)"),
    USER_PROFILE_DOES_NOT_EXIST("6", "Will probably never happen, when accessing my profile if (for any reason) profile cannot be found."),

    /**
     * TOKEN
     */
    TOKEN_EXPIRED("10", "\"Token provided has expired.\n" +
            "In case of expired Registration token - redirect to refresh token endpoint\n" +
            "In case of expired Password reset token - user has to manually click on \"\"Forgot password\"\" again\""),
    TOKEN_NOT_FOUND("11", "Token is cannot be found in the database. (Could be deleted by scheduler since we frequently delete expired tokens)."),

    /**
     * INTERNAL ERRORS
     */
    REGION_NOT_FOUND("20", "This happens if provided region cannot be found - should not happen, since user can chose from regions provided by database."),
    ROLE_NOT_FOUND("21", "This happens if provided role cannot be found - should not happen, all users have implicit \"USER\" role that is initially being loaded into database"),
    EXCHANGE_RATE_NOT_FOUND("22", "This happens if provided exchange rate cannot be found - should not happen, since exchange rate "),
    PARTNER_NOT_FOUND("23", "Happens if provided partner cannot be found - should not happen."),

    /**
     * WALLET ADDRESS
     */
    WALLET_ADDRESS_NOT_FOUND("30", "When provided wallet address cannot be found using address hash."),
    WALLET_ADDRESS_ALREADY_EXISTS("31", "When inserting new wallet address, if that address hash already exists in the database."),
    WALLET_ADDRESS_NOT_VALID("32", "Address hash validator threw false validation"),

    /**
     * EXTERNAL REGISTRATION
     */
    PARTNER_USER_ID_ALREADY_EXISTS("40", "The queryparam value that was provided from external registration point is already within database - somebody is already registered with this partner id."),

    /**
     * PARTNER DATA POLLING
     */
    COIN_USER_ID_ALREADY_EXISTS("41", "Not occuring on the front end"),
    PARTNER_USER_ID_DOES_NOT_EXIST("42", "Not occuring on the front end"),
    COIN_USER_ID_DOES_NOT_EXIST("43", "Not occuring on the front end"),
    PAIR_DOES_NOT_EXIST("44", "Not occuring on the front end"),

    /**
     * REST TEMPLATE FETCHING
     */
    RING_RING_FETCHING_ERROR("50", "\"When user gets registered using external registration, server tries to fetch data from partner's service in order to get data necessary for dashboard.\n"),
    EXCHANGE_RATE_FETCHING_ERROR("51", "There was a failed attempt to fetch data for exchange rate from either Crex or CMC"),

    /*
    * TRANSACTION
     */
    TRANSACTION_NOT_FOUND("60", "TransactionKeys with given id cannot be found."),
    TRANSACTION_STATUS_LOCKED("61", "TransactionKeys status is locked, it has probably been persisted in Blockchain"),
    TRANSACTION_CANCEL_ERROR("62", "TransactionKeys cancel error."),
    TRANSACTION_AMOUNT_IS_TOO_LOW("63", "TransactionKeys amount is too low."),
    BLOCKCHAIN_TRANSACTION_FETCHING_ERROR("64", "There was an error with fetching data from the blockchain service."),
    BLOCKCHAIN_PERSISTING_ERROR("65", "There was an error in response when persisting data into blockchain"),

    /*
     * Oauth2 Service
     */
    CLIENT_CREDENTIALS_AUTH_ERROR("70", "There was an error when authorizing with client credentials");

    private final String code;
    private final String description;

    ErrorCodes(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
