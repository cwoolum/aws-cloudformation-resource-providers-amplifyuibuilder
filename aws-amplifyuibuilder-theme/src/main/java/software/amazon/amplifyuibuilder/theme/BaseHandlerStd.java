package software.amazon.amplifyuibuilder.theme;

import software.amazon.amplifyuibuilder.common.TaggingHelpers;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.cloudformation.proxy.*;

// Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  protected static ProgressEvent<ResourceModel, CallbackContext> handleErrorInternal(
      ResourceHandlerRequest<ResourceModel> awsRequest,
      Exception exception,
      ProxyClient<AmplifyUiBuilderClient> client,
      ResourceModel model,
      CallbackContext context) {
    if (TaggingHelpers.isTagBasedAccessDenied(exception)) {
      return ProgressEvent.failed(model, context, HandlerErrorCode.UnauthorizedTaggingOperation,
          exception.getMessage());
    }

    return ProgressEvent.failed(model, context, HandlerErrorCode.InternalFailure, exception.getMessage());
  }

  @Override
  public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final Logger logger) {
    return handleRequest(
        proxy,
        request,
        callbackContext != null ? callbackContext : new CallbackContext(),
        proxy.newProxy(ClientBuilder::getClient),
        logger
    );
  }

  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger);
}
