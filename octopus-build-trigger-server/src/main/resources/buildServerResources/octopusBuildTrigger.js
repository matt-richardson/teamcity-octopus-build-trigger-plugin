
window.octopusBuildTrigger = function() {
  var url;
  var apiKey;

  function handleCheckConnectivityResponse(response) {
    if (response.responseJSON.type) {
      if (response.responseJSON.type == 'Url')
          setError(response.responseJSON.message, '');
      else
          setError('', response.responseJSON.message);
    }
    else {
        clearError();
    }
  }

  function handleProjectResponse(response) {
    var dropdown = $$('[name="prop:' + projectIdPropertyName + '"]')[0]
    $j(dropdown).empty();
    var oldValue = $j(dropdown).attr('data-old-value');
    if (response.responseJSON.type) {
        if (response.responseJSON.type == 'Url')
            setError(response.responseJSON.message, '');
        else
            setError('', response.responseJSON.message);
    }
    else {
        for(i=0; i < response.responseJSON.length; i++) {
          var option = document.createElement("option");
          option.text = response.responseJSON[i].name;
          option.value = response.responseJSON[i].id;
          option.selected = (option.value == oldValue);
          dropdown.add(option);
        };
        clearError();
    }
  }

  function clearError() {
    $j('#octopus-build-trigger-busy').hide();
    $j('[id="error_' + octopusUrlPropertyName + '"]').text("");
    $j('[id="error_' + octopusApiKeyPropertyName + '"]').text("");
  }

  function setError(urlError, apiKeyError) {
    $j('#octopus-build-trigger-busy').hide();
    if (urlError) {
      $j('[id="error_' + octopusUrlPropertyName + '"]').text(urlError);
      $j('[id="error_' + octopusApiKeyPropertyName + '"]').text("");
    }
    else {
      $j('[id="error_' + octopusUrlPropertyName + '"]').text("");
      $j('[id="error_' + octopusApiKeyPropertyName + '"]').text(apiKeyError);
    }
  }

  function handleNetworkFailureResponse(xhr, response) {
    setError("Unable to connect. Please ensure the url and API key are correct.", null);
  }

  function handle401Response(xhr, response) {
    setError(null, "Invalid api key");
  }

  function handle404Response(xhr, response) {
    setError("Unable to connect to url: 404 Not Found", null);
  }

  function handleFailureResponse(xhr, response) {
    setError("Unable to connect to url: unknown failure", null);
  }

  function handleExceptionResponse(response, err) {
    setError("Unable to connect to url: exception", null);
  }

  function getApiResponse(successHandler) {
    url = $$('[name="prop:' + octopusUrlPropertyName + '"]')[0].value.replace(/\/$/, '');
    apiKey = $$('[name="prop:' + octopusApiKeyPropertyName + '"]')[0].value;
    if (url == '' || apiKey == '') {
        clearError();
        return;
    }
    $j('#octopus-build-trigger-busy').show();

    new Ajax.Request('/octopus-build-trigger/projects.html', {
        requestHeaders: {
            'X-Prototype-Version': null,
            'X-Requested-With': null
        },
        parameters: {
            octopusUrl: url,
            octopusApiKey: apiKey
        },
        onSuccess: successHandler,
        on0: handleNetworkFailureResponse,
        on303: handle401Response,
        on401: handle401Response,
        on404: handle404Response,
        onFailure: handleFailureResponse,
        onException: handleExceptionResponse,
        method: 'GET'
    });
  }

  function reloadProjectList() {
    getApiResponse(handleProjectResponse);
  }

  function checkConnectivity() {
    getApiResponse(handleCheckConnectivityResponse);
  }

  return {
    reloadProjectList : reloadProjectList,
    checkConnectivity : checkConnectivity
  }
}();

$j(document).ready(function() {
  var dropdown = $j('[name="prop:' + projectIdPropertyName + '"]');
  if (dropdown.length > 0) {
    $j(dropdown).attr('data-old-value', $j(dropdown).val())
    window.octopusBuildTrigger.reloadProjectList();
  }
  else
    window.octopusBuildTrigger.checkConnectivity();
});
</script>

