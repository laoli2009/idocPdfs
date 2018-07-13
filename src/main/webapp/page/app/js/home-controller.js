var app = angular.module("home.controller", ["ngRoute",  "ftpfiles.controller"]) ;  
  
app.config(function ($routeProvider) {  
    $routeProvider.  
        when('/home', {  
            templateUrl: './page/app/html/ftpfiles.html',  
            controller: 'ftpfilesCtrl'  
        }).  
        when('/ftpfilesmanage', {  
            templateUrl: './page/app/html/ftpfiles.html',  
            controller: 'ftpfilesCtrl'  
        }).  
        when('/upload', {  
            templateUrl: './page/app/html/ftpfiles.html',  
            controller: 'ftpfilesCtrl'  
        }).  
        otherwise({  
            redirectTo: '/home'  
        });  
});  