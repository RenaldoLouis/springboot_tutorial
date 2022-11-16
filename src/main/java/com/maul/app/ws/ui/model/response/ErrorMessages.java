package com.maul.app.ws.ui.model.response;

public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"), INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record with provided id is not found"), AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update record"), COULD_NOT_DELETE_RECORD("Could not delete record"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified"), TOKEN_EXPIRED("Token Expired"),
    TOKEN_NOT_FOUND("Token Not Found"),
    EMAIL_ADDRESS_NOT_FOUND("Email address could not be found"), CREATE_DELIVERY_FAILED("Create Delivery Failed"),
    CREATE_COURIER_FAILED("Create Courier Failed"), COMPLETE_DELIVERY_FAILED("Complete Delivery Failed"),
    DELIVERY_ALREADY_DONE("Delivery Already Done"), DELIVERY_NOT_DONE_YET("Delivery Not Done Yet"),
    DELETE_DELIVERY_FAILED("Delete Delivery Failed");

    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
