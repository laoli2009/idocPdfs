var app = angular.module("user.controller", []);

app.controller("userCtrl" , ["$scope", '$http', '$q', '$location' , function($scope, $http, $q , $location) {
    
	
	$scope.init = function () {
	    console.info("laoli");
	    $("#id_account_l").val("");
	    $("#id_password_l").val("");
	    $("#id_email").val("");
	    $("#id_password").val("");
	    $("#id_captcha_1").val("");
	    $("#id_mobile").val("");
	    $("#id_mobile_code").val("");
	    $("#id_password_m").val("");
	    $("#id_captcha_m_1").val("");
	    $("#id_captcha_1").val("");
	    $("#login-form-tips").hide();
	    $("#findpassword-tips").hide();
	    $("#mobile_code_password-tips").hide();
	    $("#register-tips").hide();
	    $("#loginModal").modal({ backdrop: 'static', keyboard: false });  
	    $("#loginModal").modal("show")
	};
	$scope.init();
	
    $scope.loginFailed = false ;
    
	$scope.login = function(user){
		console.log(user.userName)
		console.log(user.password)
		
		$scope.loginFailed = false ;
	    $scope.predictLable = "" ;
		var deferred = $q.defer();
		$http.get("login.do?userName=" +  user.userName + "&password=" + user.password)
		     .success(function(data) {
					deferred.resolve(data);
					if("yes" == data.status){
                        $scope.userName = user.userName 
                    	window.location = "./home"
                    }
					else{
						$scope.loginFailed = true ;
					}
			  })
			  .error(function(data) {
			        deferred.reject(data);
		      }) ;
		return deferred.promise;
	}
	
	
	

}]);