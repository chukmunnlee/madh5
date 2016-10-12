(function() {
	var TopNewsApp = angular.module("TopNewsApp", []);

	var TopNewsFactory = function($rootScope) {
		return (function() {
			return ({
				socket: null,
				connect: function(host) {
					this.socket = new WebSocket("ws://" + host + "/topnews/event");
					this.socket.onmessage = function(evt) {
						$rootScope.$apply(function() {
							var data = JSON.parse(evt.data);
							$rootScope.$broadcast("topnews." + data.type, data.payload);
						});
					}
				},
				disconnect: function() {
					this.socket && this.socket.close();
					this.socket = null;
				}
			});
		});
	}

	var TopNewsController = function($scope, TopNewsFactory) {
		var newsEvent = TopNewsFactory();
		var index = 0;
		var ctrl = this;
		ctrl.news = {};
		ctrl.comments = [];
		$scope.$on("topnews.news", function(_, newsItem) {
			ctrl.news = newsItem;
		});
		$scope.$on("topnews.comment", function(_, comment) {
			ctrl.comments[index] = comment;
			index = ++index % 3;

		})

		$scope.$on("$destroy", function() {
			newsEvent.disconnect();
		});

		newsEvent.connect("localhost:8080");
	}

	TopNewsApp.factory("TopNewsFactory", ["$rootScope", TopNewsFactory]);
	TopNewsApp.controller("TopNewsController", ["$scope", "TopNewsFactory", TopNewsController]);

})();

