var app = angular.module("idocpdfs.controller", ["idocpdfs.service"]);

app.controller("idocpdfsCtrl", ["$scope", '$http', '$q', "idocpdfsService", function($scope, $http, $q, idocpdfsService, NgTableParams) {


    $scope.init = function() {
        console.log("sb");
        idocpdfsService.getFileYearRecoder().then(function(data) {
            $scope.file_infos = data;
            $(document).ready(function() {
                $("#treeview").treeview({
                    toggle: function() {}
                });
            });
        }, function(data) {
            $defer.resolve([]);
        });
    };
    $scope.init();

    $scope.gao = function(year, month, select_fileNames) {
        $scope.select_year = year;
        $scope.select_month = month;
        $scope.select_fileNames = select_fileNames;
        $scope.search_fileNames = $scope.select_fileNames;
    };

    $scope.likeSearch = function(likeFileName) {
        if (likeFileName != undefined) {
            $scope.search_fileNames = [];
            for (i = 0; i < $scope.select_fileNames.length; i++) {
                if ($scope.select_fileNames[i].indexOf(likeFileName) >= 0) {
                    $scope.search_fileNames.push($scope.select_fileNames[i]);
                }
            }
        } else {
            $scope.search_fileNames = $scope.select_fileNames;
        }
    };

    $scope.downloadFileByName = function(select_year, select_month, fileName) {
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPaht = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        $http.get(localhostPaht + projectName + "/downloadFileByName?select_year=" + select_year + "&select_month=" + select_month + "&fileName=" + fileName, { responseType: 'arraybuffer' })
            .success(function(data, status, headers) {
                var octetStreamMime = 'application/octet-stream';
                var success = false;
                headers = headers();
                filename = fileName;
                var contentType = headers['content-type'] || octetStreamMime;
                try {
                    var blob = new Blob([data], { type: contentType });
                    if (navigator.msSaveBlob)
                        navigator.msSaveBlob(blob, filename);
                    else {
                        var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
                        if (saveBlob === undefined) throw "Not supported";
                        saveBlob(blob, filename);
                    }
                    success = true;
                } catch (ex) {}

                if (!success) {
                    var urlCreator = window.URL || window.webkitURL || window.mozURL || window.msURL;
                    if (urlCreator) {
                        var link = document.createElement('a');
                        if ('download' in link) {
                            try {
                                var blob = new Blob([data], { type: contentType });
                                var url = urlCreator.createObjectURL(blob);
                                link.setAttribute('href', url);
                                link.setAttribute("download", filename);
                                var event = document.createEvent('MouseEvents');
                                event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
                                link.dispatchEvent(event);
                                success = true;
                            } catch (ex) {
                                console.log(ex);
                            }
                        }
                        if (!success) {
                            try {
                                var blob = new Blob([data], { type: octetStreamMime });
                                var url = urlCreator.createObjectURL(blob);
                                window.location = url;
                                success = true;
                            } catch (ex) {
                                console.log(ex);
                            }
                        }

                    }
                }

                if (!success) {
                    window.open(httpPath, '_blank', '');
                }
            })
            .error(function(data, status) {
                console.log("Request failed with status: " + status);
                $scope.errorDetails = "Request failed with status: " + status;
            });
    };

    $scope.panelrmcc = false;
    $scope.showloading = false;
    $scope.searchRmCC = function() {
        if ($scope.fromDate == undefined) {
            confirm("请选择起始时间！");
            return;
        }
        if ($scope.toDate == undefined) {
            confirm("请选择截止时间！");
            return;
        }
        if ($scope.desc == undefined) {
            confirm("请选择类型！");
            return;
        }
        if ($scope.fromDate > $scope.toDate) {
            confirm("起始时间应小于等于截止时间！");
            return;
        }
        $scope.searchFromDate = $scope.fromDate.Format("yyyy-MM-dd");
        $scope.searchToDate = $scope.toDate.Format("yyyy-MM-dd");
        $scope.searchName = $scope.desc.searchFilePrefix;
        if ($scope.panelrmcc) {
            $scope.panelrmcc = false;
        }
        $scope.showloading = true;
        idocpdfsService.getPageResponseBean($scope.searchFromDate, $scope.searchToDate, $scope.searchName, 1, 12).then(function(data) {
            $scope.showloading = false;
            $scope.responseBeans = data.responseBeans;
            $scope.pageNow = data.pageNow;
            $scope.maxPage = data.maxPage;
            $scope.panelrmcc = true;
        }, function(data) {
            $defer.resolve([]);
        });
    };

    $scope.nextSearchRmCC = function(add) {
        searchPage = $scope.pageNow + add;
        if (searchPage < 1 || searchPage > $scope.maxPage) {
            return;
        }
        if ($scope.panelrmcc) {
            $scope.panelrmcc = false;
        }
        $scope.showloading = true;
        idocpdfsService.getPageResponseBean($scope.searchFromDate, $scope.searchToDate, $scope.searchName, searchPage, 12).then(function(data) {
            console.info(data);
            $scope.showloading = false;
            $scope.responseBeans = data.responseBeans;
            $scope.pageNow = data.pageNow;
            $scope.maxPage = data.maxPage;
            $scope.panelrmcc = true;
        }, function(data) {
            $defer.resolve([]);
        });
    };

    $scope.makeResponseBeanFile = function() {
        if ($scope.fromDate == undefined) {
            confirm("请选择起始时间！");
            return;
        }
        if ($scope.toDate == undefined) {
            confirm("请选择截止时间！");
            return;
        }
        if ($scope.desc == undefined) {
            confirm("请选择类型！");
            return;
        }
        if ($scope.fromDate > $scope.toDate) {
            confirm("起始时间应小于等于截止时间！");
            return;
        }
        if ($scope.desc.desc == "all") {
            dateSpan = $scope.toDate - $scope.fromDate;
            dateSpan = Math.abs(dateSpan);
            iDays = Math.floor(dateSpan / (24 * 3600 * 1000));
            if (iDays >= 62) {
                confirm("只能下载2个月范围的所有数据！");
                return;
            }
        }
        $scope.searchFromDate = $scope.fromDate.Format("yyyy-MM-dd");
        $scope.searchToDate = $scope.toDate.Format("yyyy-MM-dd");
        $scope.searchName = $scope.desc.searchFilePrefix;
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPaht = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        $http.get(localhostPaht + projectName + "/makeResponseBeanFile?fromDate=" + $scope.searchFromDate + "&toDate=" + $scope.searchToDate + "&name=" + $scope.searchName, { responseType: 'arraybuffer' })
            .success(function(data, status, headers) {
                var octetStreamMime = 'application/octet-stream';
                var success = false;
                headers = headers();
                filename = $scope.searchFromDate + "_" + $scope.searchToDate + "_" + $scope.searchName + ".xls";
                var contentType = headers['content-type'] || octetStreamMime;
                try {
                    var blob = new Blob([data], { type: contentType });
                    if (navigator.msSaveBlob)
                        navigator.msSaveBlob(blob, filename);
                    else {
                        var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
                        if (saveBlob === undefined) throw "Not supported";
                        saveBlob(blob, filename);
                    }
                    success = true;
                } catch (ex) {}

                if (!success) {
                    var urlCreator = window.URL || window.webkitURL || window.mozURL || window.msURL;
                    if (urlCreator) {
                        var link = document.createElement('a');
                        if ('download' in link) {
                            try {
                                var blob = new Blob([data], { type: contentType });
                                var url = urlCreator.createObjectURL(blob);
                                link.setAttribute('href', url);
                                link.setAttribute("download", filename);
                                var event = document.createEvent('MouseEvents');
                                event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
                                link.dispatchEvent(event);
                                success = true;
                            } catch (ex) {
                                console.log(ex);
                            }
                        }
                        if (!success) {
                            try {
                                var blob = new Blob([data], { type: octetStreamMime });
                                var url = urlCreator.createObjectURL(blob);
                                window.location = url;
                                success = true;
                            } catch (ex) {
                                console.log(ex);
                            }
                        }

                    }
                }

                if (!success) {
                    window.open(httpPath, '_blank', '');
                }
            })
            .error(function(data, status) {
                console.log("Request failed with status: " + status);
                $scope.errorDetails = "Request failed with status: " + status;
            });
    };

    $scope.preNameOperation = [{ "idx": 0, "desc": "all", "searchFilePrefix": "all" },
        { "idx": 1, "desc": "AE", "searchFilePrefix": "AE" },
        { "idx": 2, "desc": "AU", "searchFilePrefix": "AU" },
        { "idx": 3, "desc": "BD", "searchFilePrefix": "BD" },
        { "idx": 4, "desc": "CA", "searchFilePrefix": "CA" },
        { "idx": 5, "desc": "CH", "searchFilePrefix": "CH" },
        { "idx": 6, "desc": "CZ", "searchFilePrefix": "CZ" },
        { "idx": 7, "desc": "DK", "searchFilePrefix": "DK" },
        { "idx": 8, "desc": "DE", "searchFilePrefix": "DE" },
        { "idx": 9, "desc": "EE", "searchFilePrefix": "EE" },
        { "idx": 10, "desc": "IE", "searchFilePrefix": "IE" },
        { "idx": 11, "desc": "LT", "searchFilePrefix": "LT" },
        { "idx": 12, "desc": "LV", "searchFilePrefix": "LV" },
        { "idx": 13, "desc": "AT", "searchFilePrefix": "AT" },
        { "idx": 14, "desc": "IT", "searchFilePrefix": "IT" },
        { "idx": 15, "desc": "BE", "searchFilePrefix": "BE" },
        { "idx": 16, "desc": "SK", "searchFilePrefix": "SK" },
        { "idx": 17, "desc": "ES", "searchFilePrefix": "ES" },
        { "idx": 18, "desc": "NL", "searchFilePrefix": "NL" },
        { "idx": 19, "desc": "PT", "searchFilePrefix": "PT" },
        { "idx": 20, "desc": "FR", "searchFilePrefix": "FR" },
        { "idx": 21, "desc": "GB", "searchFilePrefix": "bs" },
        { "idx": 22, "desc": "HK", "searchFilePrefix": "HK" },
        { "idx": 23, "desc": "II", "searchFilePrefix": "II" },
        { "idx": 24, "desc": "IN", "searchFilePrefix": "IN" },
        { "idx": 25, "desc": "IS", "searchFilePrefix": "IS" },
        { "idx": 26, "desc": "JP", "searchFilePrefix": "JP" },
        { "idx": 27, "desc": "KR", "searchFilePrefix": "KR" },
        { "idx": 28, "desc": "LK", "searchFilePrefix": "LK" },
        { "idx": 29, "desc": "MO", "searchFilePrefix": "MO" },
        { "idx": 30, "desc": "MY", "searchFilePrefix": "MY" },
        { "idx": 31, "desc": "NO", "searchFilePrefix": "NO" },
        { "idx": 32, "desc": "NP", "searchFilePrefix": "NP" },
        { "idx": 33, "desc": "NZ", "searchFilePrefix": "NZ" },
        { "idx": 34, "desc": "PH", "searchFilePrefix": "PH" },
        { "idx": 35, "desc": "RU", "searchFilePrefix": "RU" },
        { "idx": 36, "desc": "SE", "searchFilePrefix": "SE" },
        { "idx": 37, "desc": "SG", "searchFilePrefix": "SG" },
        { "idx": 38, "desc": "TH", "searchFilePrefix": "TH" },
        { "idx": 39, "desc": "TW", "searchFilePrefix": "TW" },
        { "idx": 40, "desc": "IL", "searchFilePrefix": "IL" },
        { "idx": 41, "desc": "KH", "searchFilePrefix": "KH" },
        { "idx": 42, "desc": "VN", "searchFilePrefix": "VN" }
    ]





    Date.prototype.Format = function(fmt) { //author: meizz 
        var o = {
            "M+": this.getMonth() + 1, //月份 
            "d+": this.getDate(), //日 
            "h+": this.getHours(), //小时 
            "m+": this.getMinutes(), //分 
            "s+": this.getSeconds(), //秒 
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
            "S": this.getMilliseconds() //毫秒 
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    $scope.downloadFileByresponseBean = function(responseBean) {
        console.info(responseBean);
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPaht = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        $http.get(localhostPaht + projectName + "/downloadFileByresponseBean?fileDate=" + responseBean.fileDate + "&fileName=" + responseBean.name, { responseType: 'arraybuffer' })
            .success(function(data, status, headers) {
                var octetStreamMime = 'application/octet-stream';
                var success = false;
                headers = headers();
                filename = responseBean.name;
                var contentType = headers['content-type'] || octetStreamMime;
                try {
                    var blob = new Blob([data], { type: contentType });
                    if (navigator.msSaveBlob)
                        navigator.msSaveBlob(blob, filename);
                    else {
                        var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
                        if (saveBlob === undefined) throw "Not supported";
                        saveBlob(blob, filename);
                    }
                    success = true;
                } catch (ex) {}

                if (!success) {
                    var urlCreator = window.URL || window.webkitURL || window.mozURL || window.msURL;
                    if (urlCreator) {
                        var link = document.createElement('a');
                        if ('download' in link) {
                            try {
                                var blob = new Blob([data], { type: contentType });
                                var url = urlCreator.createObjectURL(blob);
                                link.setAttribute('href', url);
                                link.setAttribute("download", filename);
                                var event = document.createEvent('MouseEvents');
                                event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
                                link.dispatchEvent(event);
                                success = true;
                            } catch (ex) {
                                console.log(ex);
                            }
                        }
                        if (!success) {
                            try {
                                var blob = new Blob([data], { type: octetStreamMime });
                                var url = urlCreator.createObjectURL(blob);
                                window.location = url;
                                success = true;
                            } catch (ex) {
                                console.log(ex);
                            }
                        }

                    }
                }

                if (!success) {
                    window.open(httpPath, '_blank', '');
                }
            })
            .error(function(data, status) {
                console.log("Request failed with status: " + status);
                $scope.errorDetails = "Request failed with status: " + status;
            });
    }

}]);