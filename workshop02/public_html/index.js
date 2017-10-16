(function() {

    var server = "localhost:8080/topnews"
    //var server = "10.10.0.50:8080/topnews"

    var TopNewsApp = angular.module("TopNewsApp", []);

    var TopNewsSvc = function($rootScope, $http, $httpParamSerializerJQLike) {
        this.socket = null;
        this.subscribe = function(callback) {
            this.socket = new WebSocket("ws://" + server + "/event")
            this.socket.onmessage = function(evt) {
                $rootScope.$apply(function() {
                    callback(JSON.parse(evt.data));
                })
            }
        };
        this.unsubscribe = function() {
            this.socket.close();
        }
        this.postComment = function(formData) {
            return ($http({
                method: "POST",
                url: "http://" + server + "/comment",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                data: $httpParamSerializerJQLike(formData)
            }));
        }
    }

    var TopNewsCtrl = function(TopNewsSvc) {

        var topNewsCtrl = this;
        topNewsCtrl.current = {};
        topNewsCtrl.name = "";
        topNewsCtrl.comment = "";

        topNewsCtrl.postComment = function() {
            TopNewsSvc.postComment({
                title: topNewsCtrl.current.payload.title,
                name: topNewsCtrl.name,
                comment: topNewsCtrl.comment
            }).then(function() {
                topNewsCtrl.comment = "";
            }).catch(function(err) {
                console.error(">>. error: %s", err);
            })
        }

        TopNewsSvc.subscribe(function(news) {
            topNewsCtrl.current = news;
        });

    }

    TopNewsApp.service("TopNewsSvc", ["$rootScope", "$http",
        "$httpParamSerializerJQLike", TopNewsSvc])

    TopNewsApp.controller("TopNewsCtrl", [ "TopNewsSvc", TopNewsCtrl])

})();
