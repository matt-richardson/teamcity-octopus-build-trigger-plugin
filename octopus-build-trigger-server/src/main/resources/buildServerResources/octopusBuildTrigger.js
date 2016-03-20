
window.octopusBuildTrigger = function() {
  var url;
  var apiKey;

  function handleProjectResponse(response) {
    var dropdown = $$('[name="prop:' + projectIdPropertyName + '"]')[0]
    var oldValue = $j(dropdown).val();
    $j(dropdown).empty();

    for(i=0; i < response.responseJSON.length; i++) {
      var option = document.createElement("option");
      option.text = response.responseJSON[i].Name;
      option.value = response.responseJSON[i].Id;
      option.selected = (option.value == oldValue);
      dropdown.add(option);
    };
    clearError();
  }

  function clearError() {
    $j('[id="error_' + octopusUrlPropertyName + '"]').text("");
    $j('[id="error_' + octopusApiKeyPropertyName + '"]').text("");
  }

  function setError(urlError, apiKeyError) {
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

  function handleApiResponse(response) {
    var projectsLink = response.responseJSON.Links.Projects.replace(/\{.*/, '') + '/all';
    new Ajax.Request(url + '/' + projectsLink + '?apikey=' + apiKey, {
      requestHeaders: {
          'X-Prototype-Version': null,
          'X-Requested-With': null
      },
      onSuccess: handleProjectResponse,
      on0: handleNetworkFailureResponse,
      on303: handle401Response,
      on401: handle401Response,
      on404: handle404Response,
      onFailure: handleFailureResponse,
      onException: handleExceptionResponse,
      method: 'GET'
    });
  }

  function getApiResponse(successHandler) {
    url = $$('[name="prop:' + octopusUrlPropertyName + '"]')[0].value.replace(/\/$/, '');
    apiKey = $$('[name="prop:' + octopusApiKeyPropertyName + '"]')[0].value;

    new Ajax.Request(url + '/api?apikey=' + apiKey, {
        requestHeaders: {
            'X-Prototype-Version': null,
            'X-Requested-With': null
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
    getApiResponse(handleApiResponse);
  }

  function checkConnectivity() {
    getApiResponse(clearError);
  }

  return {
    reloadProjectList : reloadProjectList,
    checkConnectivity : checkConnectivity
  }
}();

$j(document).ready(function() {
  var dropdown = $j('[name="prop:' + projectIdPropertyName + '"]');
  if (dropdown.length > 0)
    window.octopusBuildTrigger.reloadProjectList();
  else
    window.octopusBuildTrigger.checkConnectivity();
});
</script>

