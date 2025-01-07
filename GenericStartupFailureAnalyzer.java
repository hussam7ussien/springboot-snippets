package com.example.demo;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.context.ApplicationContextException;
import org.springframework.validation.BindException;

import java.net.SocketException;

public class GenericStartupFailureAnalyzer<T extends Throwable> extends AbstractFailureAnalyzer<T> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, T cause) {
        // Analyze specific exception types
        if (cause instanceof ApplicationContextException
                && cause.getCause() instanceof PortInUseException) {
            return analyzePortException((PortInUseException) cause.getCause());
        }
        if (cause instanceof SocketException) {
            return analyzeSocketException((SocketException) cause);
        } else if (cause instanceof UnsatisfiedDependencyException) {
            return analyzeUnsatisfiedDependencyException((UnsatisfiedDependencyException) cause);
        } else if (cause instanceof NoSuchBeanDefinitionException) {
            return analyzeNoSuchBeanDefinitionException((NoSuchBeanDefinitionException) cause);
        } else if (cause instanceof BindException) {
            return analyzePortBindingException((BindException) cause);
        } else if (cause instanceof ApplicationContextException) {
            return analyzeApplicationContextException((ApplicationContextException) cause);
        } else if (cause instanceof BeanCreationException) {
            return analyzeBeanCreationException((BeanCreationException) cause);
        }

        // Default fallback for unknown errors
        return new FailureAnalysis("Unknown failure", "No specific action available", cause);
    }

    private FailureAnalysis analyzePortException(PortInUseException cause) {
        String description = String.format("Port issue: %s", cause.getMessage());
        String action = "Configure a new port";
        return new FailureAnalysis(description, action, cause);
    }

    // Analyze Bean Creation Exceptions (e.g., BeanNotOfRequiredTypeException, BeanCreationException)
    private FailureAnalysis analyzeBeanCreationException(BeanCreationException cause) {
        String description = String.format("Bean creation failed: %s", cause.getMessage());
        String action = "Check bean configuration, dependencies, or type mismatches";
        return new FailureAnalysis(description, action, cause);
    }

    // Analyze NoSuchBeanDefinitionException (e.g., bean not found)
    private FailureAnalysis analyzeNoSuchBeanDefinitionException(NoSuchBeanDefinitionException cause) {
        String description = String.format("Bean not found: %s", cause.getMessage());
        String action = "Ensure that the bean is correctly defined in the application context";
        return new FailureAnalysis(description, action, cause);
    }

    // Analyze Unsatisfied Dependency Exceptions (e.g., missing required bean)
    private FailureAnalysis analyzeUnsatisfiedDependencyException(UnsatisfiedDependencyException cause) {
        String description = String.format("Unsatisfied dependency: %s", cause.getMessage());
        String action = "Ensure that all required dependencies are available and properly configured";
        return new FailureAnalysis(description, action, cause);
    }

    // Handle generic SocketException (sometimes thrown for port issues or networking issues)
    private FailureAnalysis analyzeSocketException(SocketException cause) {
        String description = String.format("Network error occurred: %s", cause.getMessage());
        String action = "This could be caused by network issues or unavailable resources. Check if the port is being used by another process.";
        return new FailureAnalysis(description, action, cause);
    }

    // Analyze ApplicationContextException to look for BindException
    private FailureAnalysis analyzeApplicationContextException(ApplicationContextException cause) {
        // Check if the root cause is a BindException (port binding issue)
        if (cause.getCause() instanceof BindException) {
            return analyzePortBindingException((BindException) cause.getCause());
        }

        // Fallback for other ApplicationContext issues
        String description = String.format("Application context initialization failed: %s", cause.getMessage());
        String action = "Check the application context initialization logs for further details";
        return new FailureAnalysis(description, action, cause);
    }

    // Analyze Port Binding Exception (BindException)
    private FailureAnalysis analyzePortBindingException(BindException cause) {
        String description = String.format("Port binding failed: %s", cause.getMessage());
        String action = "The port might already be in use. Try changing the port by updating the 'server.port' property in application.properties or run `lsof -i :<port>` to check for processes using that port.";
        return new FailureAnalysis(description, action, cause);
    }

    // Other specific analyzers...
}


