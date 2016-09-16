(function() {
    var WeatherModule = angular.module("WeatherModule", []);

    var WeatherSvc = function($http, $q) {
        var key = "cities";

        this.cities = [];

        this.loadCities = function() {
            var cities = localStorage.getItem(key);
            if (!cities)
                this.cities = ["Singapore"];
            else
                this.cities = JSON.parse(cities);
            return (this.cities.sort());
        };

        this.addCity = function(city) {
            var found = this.cities.find(function(c) {
                return (c.toLowerCase() === city.toLowerCase());
            });
            if (!found) {
                this.cities.unshift(city);
                this.cities.sort();
                localStorage.setItem(key, JSON.stringify(this.cities));
            }
        }

        this.saveCities = function(cities) {
            this.cities = cities;
            localStorage.setItem(key, JSON.stringify(cities));
        }

        this.getWeather = function(city, key) {
            var defer = $q.defer();

            $http.get("http://api.openweathermap.org/data/2.5/weather", {
                params: {
                    units: "metric",
                    q: city,
                    appid: key
                }
            }).then(function(result) {
                defer.resolve(result.data);
            }).catch(function(error) {
                defer.reject(error);
            });

            return (defer.promise);
        }
    };

    WeatherModule.service("WeatherSvc", ["$http", "$q", WeatherSvc]);
})();