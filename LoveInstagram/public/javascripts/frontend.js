var totalLiked = 0;
var seconds = 180; //3 minutes

//Can only do 100 likes per hour 

function setCookie(cname, cvalue) {
    document.cookie = cname + "=" + cvalue + ";";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}

function redirectUser(){
  setCookie("clientId", $("#clientId").val());
  setCookie("clientSecret", $("#clientSecret").val());
  setCookie("tags", $("#tags").val().replace(/ /g,''));
  window.location.replace("https://api.instagram.com/oauth/authorize/?client_id="+ getCookie("clientId") +"&redirect_uri=http://localhost:3000/api&response_type=code");
} 

function exchangeCodeForAccessToken(code){
  var clientId = getCookie("clientId");
  var clientSecret = getCookie("clientSecret");

  $.ajax({
    type: "GET",
    dataType: "text",
    cache: false,
    url: ("http://localhost:8080/api?clientId=" + clientId + "&clientSecret=" + clientSecret + "&code=" + code),
    success: function (data) {
      likeMedia(data, totalLiked);
      setInterval(function(){likeMedia(data, totalLiked)}, seconds * 1000);
    },
    error: function (data) {
      $('#accessToken').html("That request didn't work (auth): " + data);
    }
  })
}

function likeMedia(accessToken, numLiked){
  var tags = getCookie("tags");
  var uri = ("http://localhost:8080/likeMedia?accessToken=" + accessToken + "&tags=" + tags).replace(/"/g, '');

  $.ajax({
    type: "GET",
    dataType: "text",
    cache: false,
    url: uri,
    success: function (data) {
      totalLiked = parseInt(data) + numLiked
      if(data == 0){
        $('#notify').html(totalLiked + " images liked. Didn't like any images in the last request. The limit for requests may have been hit for this hour.");
      }else{
        $('#notify').html(totalLiked + " images liked.");        
      }
    },
    error: function (data) {
      $('#notify').html("That request didn't work (likeMedia): " + data);
    }
  })
}