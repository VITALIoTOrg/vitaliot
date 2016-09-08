//var DiscovererBASE_URL="https://vital-integration.atosresearch.eu:8843/discoverer/"
var DiscovererBASE_URL="http://localhost:8080/discoverer/";
var QUERY_URL;
var TextQuery;

getPPImetadata = function() {
    var resultDiv = $("#resultPPImetadata");
    $.ajax({
        //url: "https://vital-integration.atosresearch.eu:8843/discoverer/ppi/metadata",
        //url: "http://localhost:8080/discoverer/ppi/metadata",
        url: DiscovererBASE_URL+"ppi/metadata",
        type: "POST",
        //contentType: "application/json",
        //data: { },
        dataType: "JSON",
        success: function (result) {
            switch (result) {
                case true:
                    processResponse(result);
                    break;
                default:
                    resultDiv.html(result);
                    next();
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
        alert("oops! we are sorry, but something went wrong.");
        alert(thrownError);
        }
    });
};

function DiscovererPages() {
  document.getElementById("DiscovererPages").innerHTML = "Paragraph changed.";
};


function CreateQuery() {
      var entityType = document.getElementById("SelectEntityID").value;
      if (entityType == "ico") {
          QUERY_URL = DiscovererBASE_URL+"ico";
      }
      if (entityType == "system") {
          QUERY_URL = DiscovererBASE_URL+"system";
      }
      if (entityType == "service") {
          QUERY_URL = DiscovererBASE_URL+"service";
      }
      //alert(QUERY_URL);
      TextQuery = document.getElementById("TextQueryID").value;
};

sendQuery = function() {
      var resultDiv = $("#TextQueryResultID");
      $.ajax({
          url: QUERY_URL,
          type: "POST",
          contentType: "application/json",
          //data: JSON.stringify(TextQuery),
          data : TextQuery,
          dataType: "JSON",
          success: function (result) {
              switch (result) {
                  case true:
                      processResponse(result);
                      break;
                  default:
                      resultDiv.html(result);
                      next();
              }
          },
          error: function (xhr, ajaxOptions, thrownError) {
          console.log(xhr);
          //alert("oops! we are sorry, but something went wrong.");
          //alert(thrownError);
          }
      });
};

getLogin = function(form) {
      var cookie;
      $.ajax({
        type: 'POST',
        url: "https://vitalgateway.cloud.reply.eu/securitywrapper/rest/authenticate",
        //contentType: "application/json; charset=utf-8",
        data: {
          "name": form.username.value,
          "password": form.password.value
        },
        success: function (data) {
          alert("success");
          //self.location.href = "../index.html";
        },
        error: function (e) {
          alert("error");
        }
      });
  };
