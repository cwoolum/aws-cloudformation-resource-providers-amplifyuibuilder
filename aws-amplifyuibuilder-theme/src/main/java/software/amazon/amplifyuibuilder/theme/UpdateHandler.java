package software.amazon.amplifyuibuilder.theme;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.amplifyuibuilder.common.TaggingHelpers;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.UpdateThemeResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

public class UpdateHandler extends BaseHandlerStd {

  protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    ResourceModel model = request.getDesiredResourceState();

    // To pass contract test: contract_update_without_create
    if (model.getId() == null || model.getId().equals("")) {
      throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getId());
    }

    return ProgressEvent.progress(model, callbackContext)
        .then(progress -> proxy
            .initiate("AWS-AmplifyUIBuilder-Theme::Update", proxyClient, model, progress.getCallbackContext())
            .translateToServiceRequest(Translator::translateToUpdateRequest)
            .makeServiceCall((updateThemeRequest, proxyInvocation) -> {
              UpdateThemeResponse response = (UpdateThemeResponse) ClientWrapper.execute(
                  proxy,
                  updateThemeRequest,
                  proxyInvocation.client()::updateTheme,
                  ResourceModel.TYPE_NAME,
                  model.getId(),
                  logger);

              final String themeArn = TaggingHelpers.generateArn(
                  request.getRegion(),
                  request.getAwsAccountId(),
                  updateThemeRequest.appId(),
                  updateThemeRequest.environmentName(),
                  "themes",
                  updateThemeRequest.id());

              TaggingHelpers.updateTags(proxy, proxyInvocation, model.getAppId(), themeArn, ResourceModel.TYPE_NAME,
                  response.entity().tags(), model.getTags(), logger);

              return response;
            })
            .handleError((awsRequest, exception, client, model1, context) -> {
              return handleErrorInternal(request, exception, proxyClient, model1, callbackContext);
            })
            .progress())
        .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
  }
}
