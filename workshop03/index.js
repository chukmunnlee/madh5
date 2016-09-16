(function() {
    var WeatherApp = angular.module("WeatherApp", ["ionic", "WeatherModule"]);

    var WeatherConfig = function($stateProvider, $urlRouterProvider) {

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

    var ShowWeatherCtrl = function($stateParams, $state, APP_KEY, WeatherSvc) {
        var showWeatherCtrl = this;
        showWeatherCtrl.city = $stateParams.city;
        showWeatherCtrl.weather = {};

        WeatherSvc.getWeather(showWeatherCtrl.city, APP_KEY)
            .then(function(weather) {
                showWeatherCtrl.weather = weather;
                showWeatherCtrl.weather.sys.sunrise = new Date(weather.sys.sunrise * 1000);
                showWeatherCtrl.weather.sys.sunset = new Date(weather.sys.sunset * 1000);
            }, function(error) {
                console.error(">>> error: %s ", error);
            });
    };

    var CityListCtrl = function($state, $ionicListDelegate, WeatherSvc) {
        var cityListCtrl = this;
        cityListCtrl.cities = WeatherSvc.loadCities();

        cityListCtrl.showWeather = function($index) {
            $state.go("showWeather", {city: cityListCtrl.cities[$index]});
        }

        cityListCtrl.removeCity = function($index) {
            cityListCtrl.cities.splice($index, 1);
            WeatherSvc.saveCities(cityListCtrl.cities);
            $ionicListDelegate.closeOptionButtons();
        }

        cityListCtrl.addCity = function($index) {
            $state.go("addCity")
        }
    };

    var AddCityCtrl = function($state, WeatherSvc) {
        var addCityCtrl = this;

        addCityCtrl.save = function() {
            WeatherSvc.addCity(addCityCtrl.city);
            $state.go("cityList");
        }

        addCityCtrl.cancel = function() {
            $state.go("cityList");
        }
    };

    var  WeatherCtrl = function() {
        var weatherCtrl =  this;
    };

    WeatherApp.constant("APP_KEY", "be93736b8adfdad5094ce0b9f35d0ea3");
    WeatherApp.config(["$stateProvider", "$urlRouterProvider", WeatherConfig]);

    WeatherApp.controller("CityListCtrl", ["$state", "$ionicListDelegate", "WeatherSvc", CityListCtrl]);
    WeatherApp.controller("AddCityCtrl", ["$state", "WeatherSvc", AddCityCtrl])
    WeatherApp.controller("ShowWeatherCtrl", ["$stateParams", "$state", "APP_KEY", "WeatherSvc", ShowWeatherCtrl])
    WeatherApp.controller("WeatherCtrl", [WeatherCtrl]);

})();