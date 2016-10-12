var TopNewsApp = angular.module("TopNewsApp", []);

TopNewsApp.factory("TopNewsFactory", ["$rootScope", TopNewsFactory]);

TopNewsApp.controller("TopNewsController", ["$scope", "TopNewsFactory", TopNewsController]);

function TopNewsFactory($rootScope) {

    return (function (evtType) {
        var factory = {
            source: null,
            connect: function () {
                this.source = new EventSource("api/" + evtType);
                this.source.onmessage = function (evt) {                        
                    $rootScope.$apply(function () {
                        $rootScope.$broadcast("topnews." + evtType, JSON.parse(evt.data));
                    });
                }
            },
            disconnect: function () {
                this.source && this.source.close();
                this.source = null;
            }
        };
        return (factory);
    });
}

function TopNewsController($scope, TopNewsFactory) {
    $scope.newsEvent = TopNewsFactory("news");
    $scope.commentEvent = TopNewsFactory("comment");
    $scope.news = {};
    $scope.comments = [];
    var index = 0;
    $scope.$on("topnews.news", function(_, newsItem) {        
        $scope.news = newsItem;
    });
    $scope.$on("topnews.comment", function(_, commentData) {
        $scope.comments[index] = commentData;
        index = ++index % 3;
    });
    $scope.$on("$destroy", function() {
        $scope.newsEvent.disconnect();
        $scope.commentEvent.disconnect();
    });
    $scope.newsEvent.connect();    
    $scope.commentEvent.connect();
}

