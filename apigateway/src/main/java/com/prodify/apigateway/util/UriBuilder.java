//package com.prodify.apigateway.util;
//
//public class UriBuilder {
//
//    private StringBuilder pathBuilder;
//
//    // Constructor to initialize the base URI using ServiceName enum
//    public UriBuilder(ServiceName serviceName) {
//        this.pathBuilder = new StringBuilder("lb://")
//                .append(serviceName.serviceName)
//                .append(serviceName.path);
//    }
//
//    // Method to dynamically add path parameters (e.g., {userId})
//    public UriBuilder addPathParameter(String paramValue) {
//        if (this.pathBuilder.charAt(this.pathBuilder.length() - 1) != '/') {
//            this.pathBuilder.append('/');
//        }
//        this.pathBuilder.append(paramValue);
//        return this; // return the builder to allow chaining
//    }
//
//    // Method to replace placeholder with actual value (e.g., {userId})
//    public UriBuilder replacePathParameter(String placeholder, String replacement) {
//        int startIdx = this.pathBuilder.indexOf(placeholder);
//        if (startIdx != -1) {
//            int endIdx = startIdx + placeholder.length();
//            this.pathBuilder.replace(startIdx, endIdx, replacement);
//        }
//        return this; // return the builder to allow chaining
//    }
//
//    // Method to add query parameters
//    public UriBuilder addQueryParam(String key, String value) {
//        if (this.pathBuilder.indexOf("?") == -1) {
//            this.pathBuilder.append("?");
//        } else {
//            this.pathBuilder.append("&");
//        }
//        this.pathBuilder.append(key).append("=").append(value);
//        return this; // return the builder to allow chaining
//    }
//
//    // Method to build and return the final URI as a string
//    public String build() {
//        return this.pathBuilder.toString();
//    }
//}


package com.prodify.apigateway.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UriBuilder {

    private StringBuilder pathBuilder;

    // Private constructor to prevent direct instantiation using 'new'
    private UriBuilder(ServiceName serviceName, String path) {
        this.pathBuilder = new StringBuilder(serviceName.serviceUrl);
        if (path != null && !path.isEmpty()) {
            this.pathBuilder.append("/").append(path);
        }
        log.info(this.pathBuilder.toString());
    }

    // Static factory method to create an instance of UriBuilder
    public static UriBuilder of(ServiceName serviceName, String path) {
        return new UriBuilder(serviceName, path);
    }

    // Method to add path parameters dynamically
    public UriBuilder addPathParameter(String paramKey, String paramValue) {
        if (this.pathBuilder.charAt(this.pathBuilder.length() - 1) != '/') {
            this.pathBuilder.append('/');
        }
        this.pathBuilder.append(paramKey).append('/').append(paramValue);
        return this; // return the builder to allow chaining
    }

    // Method to replace placeholder with actual value (for cases where we have a placeholder like {userId})
    public UriBuilder replacePathParameter(String placeholder, String replacement) {
        int startIdx = this.pathBuilder.indexOf(placeholder);
        if (startIdx != -1) {
            int endIdx = startIdx + placeholder.length();
            this.pathBuilder.replace(startIdx, endIdx, replacement);
        }
        return this; // return the builder to allow chaining
    }

    // Method to add query parameters
    public UriBuilder addQueryParam(String key, String value) {
        if (this.pathBuilder.indexOf("?") == -1) {
            this.pathBuilder.append("?");
        } else {
            this.pathBuilder.append("&");
        }
        this.pathBuilder.append(key).append("=").append(value);
        return this; // return the builder to allow chaining
    }

    // Method to build and return the final URI as a string
    public String build() {
        return this.pathBuilder.toString();
    }
}
