'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('mainModule',['ngRoute']);
        // .factory('socket', function(socketFactory) {
        //     console.log('creating socket');
        //     var socket = socketFactory();
        //     return socket;
        // });
// configure our routes
app.config(function($routeProvider) {
    $routeProvider
        .when('/', { // route for the problems page
            templateUrl : 'static/html/problems.html',
            controller : 'ProblemsController',
            activetab : 'problems'
        })
        .when('/judge', { // route for the judge page
            templateUrl : 'static/html/judge.html',
            controller : 'JudgeController',
            activetab : 'judge'
        })
        .when('/competitions', { // route for the competitions page
            templateUrl : 'static/html/competitions.html',
            controller : 'CompetitionsController',
            activetab : 'competitions'
        })
        .when('/competitions/:cid', { // route for the competitions page
            templateUrl : 'static/html/scoreboard.html',
            controller : 'ScoreboardController',
            activetab : 'competitions'
        })
        .when('/settings', { // route for the settings page
            templateUrl : 'static/html/settings.html',
            controller : 'SettingsController',
            activetab : 'settings'
        })
});

app.filter('secondsToDateTime', [function() {
    return function(seconds) {
        return new Date(1970, 0, 1).setSeconds(seconds);
    };
}])

app.filter('iif', function () {
   return function(input, trueValue, falseValue) {
        return input ? trueValue : falseValue;
   };
});

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);
