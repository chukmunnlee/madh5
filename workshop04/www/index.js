(function() {

    var GoogleMapKey = "__YOUR_GMAP_KEY__";
    var OpenWeatherMapKey = "__YOUR_OPEN_WEATHER_MAP_KEY__";

    var WeatherApp = angular.module("WeatherApp", ["ionic", "uiGmapgoogle-maps", "WeatherModule"]);

    var WeatherConfig = function($stateProvider, $urlRouterProvider, uiGmapGoogleMapApiProvider, MAP_KEY) {

        uiGmapGoogleMapApiProvider.configure({
            key: MAP_KEY,
            v: "3.",
            libraries: "weather,geometry,visualization"
        });

        $stateProvider.state("cityList", {
            url: "/cities",
            templateUrl: "views/city_list.html",
            controller: "CityListCtrl as cityListCtrl"

        }).state("addCity", {
            url: "/add",
            templateUrl: "views/add_city.html",
            controller: "AddCityCtrl as addCityCtrl"
        }).state("showWeather", {
            url: "/weather/:city",
            templateUrl: "views/show_weather.html",
            controller: "ShowWeatherCtrl as showWeatherCtrl"
        });

        $urlRouterProvider.otherwise("/cities");
    };

    var ShowWeatherCtrl = function($scope, $q, $stateParams, $state, uiGmapGoogleMapApi, WEATHER_KEY, WeatherSvc) {
        var showWeatherCtrl = this;
        var updateWeather = function(weather) {
            showWeatherCtrl.weather = weather;
            showWeatherCtrl.weather.sys.sunrise = new Date(weather.sys.sunrise * 1000);
            showWeatherCtrl.weather.sys.sunset = new Date(weather.sys.sunset * 1000);
            showWeatherCtrl.weather.coord.latitude = weather.coord.lat;
            showWeatherCtrl.weather.coord.longitude = weather.coord.lon;
        };

        showWeatherCtrl.mapOptions = {
            draggable: false
        };
        showWeatherCtrl.city = $stateParams.city;
        showWeatherCtrl.weather = { coord: {latitude: 0, longitude: 0 }, sys: {country: "X"}};

        showWeatherCtrl.updateWeather = function() {
            WeatherSvc.getWeather(showWeatherCtrl.city, WEATHER_KEY)
                .then(updateWeather)
                .then(function() {
                    $scope.$broadcast("scroll.refreshComplete");
                });
        };

		showWeatherCtrl.shareIt = function() {
			var weather = showWeatherCtrl.weather.weather.map(function(v) {
				return (v.description);
			}).join(",");
			window.socialmessage.send({
				text: "Weather: " + showWeatherCtrl.city + " - " + weather + ", temp: " + showWeatherCtrl.weather.main.temp + "C" 
			});
		};

        WeatherSvc.getWeather(showWeatherCtrl.city, WEATHER_KEY)
            .then(updateWeather)
            .then(uiGmapGoogleMapApi)
            .catch(function(error) {
                console.error(">>> error: %s ", error);
            });
    };

    var CityListCtrl = function($state, $ionicListDelegate, WeatherSvc) {
        var cityListCtrl = this;
        cityListCtrl.cities = WeatherSvc.loadCities();

        cityListCtrl.showWeather = function($index) {
            $state.go("showWeather", {city: cityListCtrl.cities[$index]});
        };

        cityListCtrl.removeCity = function($index) {
            cityListCtrl.cities.splice($index, 1);
            WeatherSvc.saveCities(cityListCtrl.cities);
            $ionicListDelegate.closeOptionButtons();
        };

        cityListCtrl.addCity = function($index) {
            $state.go("addCity")
        };
    };

    var AddCityCtrl = function($state, WeatherSvc) {
        var addCityCtrl = this;

        addCityCtrl.save = function() {
            WeatherSvc.addCity(addCityCtrl.city);
            $state.go("cityList");
        };

        addCityCtrl.cancel = function() {
            $state.go("cityList");
        };
    };

    var  WeatherCtrl = function() {
        var weatherCtrl =  this;
    };

    WeatherApp.constant("MAP_KEY", GoogleMapKey);
    WeatherApp.constant("WEATHER_KEY", OpenWeatherMapKey);

    WeatherApp.config(["$stateProvider", "$urlRouterProvider", "uiGmapGoogleMapApiProvider", "MAP_KEY", WeatherConfig]);

    WeatherApp.controller("CityListCtrl", ["$state", "$ionicListDelegate", "WeatherSvc", CityListCtrl]);
    WeatherApp.controller("AddCityCtrl", ["$state", "WeatherSvc", AddCityCtrl])
    WeatherApp.controller("ShowWeatherCtrl", ["$scope", "$q", "$stateParams", "$state",
            "uiGmapGoogleMapApi", "WEATHER_KEY", "WeatherSvc", ShowWeatherCtrl])
    WeatherApp.controller("WeatherCtrl", [WeatherCtrl]);

})();
