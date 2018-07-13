var app = angular.module("idocpdfs.service", []);
app.service("idocpdfsService", ['$http', '$q', function($http, $q) {

    this.getFileYearRecoder = function() {
        var deferred = $q.defer();
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPaht = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        $http.get(localhostPaht + projectName + "/getFileYearRecoder")
            .success(function(data) {
                deferred.resolve(data);
            })
            .error(function(data) {
                deferred.reject(data);
            });
        return deferred.promise;
    };


    this.getPageResponseBean = function(fromDate, toDate, name, pageNow, pageSize) {
        var deferred = $q.defer();
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPaht = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        $http.get(localhostPaht + projectName + "/getPageResponseBean?fromDate=" + fromDate + "&toDate=" + toDate + "&name=" + name + "&pageNow=" + pageNow + "&pageSize=" + pageSize)
            .success(function(data) {
                deferred.resolve(data);
            })
            .error(function(data) {
                deferred.reject(data);
            });
        return deferred.promise;
    };


    this.makeResponseBeanFile = function(fromDate, toDate, name) {
        var deferred = $q.defer();
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPaht = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        $http.get(localhostPaht + projectName + "/makeResponseBeanFile?fromDate=" + fromDate + "&toDate=" + toDate + "&name=" + name)
            .success(function(data) {
                deferred.resolve(data);
            })
            .error(function(data) {
                deferred.reject(data);
            });
        return deferred.promise;
    };

}]);