app.controller('TeamController', ['$scope', '$http', '$location', '$window',
        '$routeParams', function($scope, $http, $location, $window, $routeParams) {
    $scope.individuals = [];
    $scope.teams = {};
    $http.get('/api/competitions/' + $routeParams.cid + '/teams')
        .then(function(response) {
            $scope.teams = response.data.data;

            for (var team in $scope.teams) {
                if ($scope.teams[team].length === 1 &&
                        $scope.teams[team][0].display === team) {
                    // If the team only consists of a user and a team with the same
                    // display name, then we should put that user in the 'unclassfied'
                    // group.
                    $scope.individuals.push($scope.teams[team][0]);
                    delete $scope.teams[team];
                }
            }
        },
        function(error) {
            console.error(error);
        });

    $scope.addTeam = function() {
        $scope.teams[$scope.newTeamName] = [];
    };
    $scope.teamExists = function() {
        return $scope.newTeamName in $scope.teams;
    };
    $scope.removeTeam = function(name) {
        for (var i = 0; i < $scope.teams[name].length; i++) {
            $scope.individuals.push($scope.teams[name][i]);
        }
        delete $scope.teams[name];
    };
    $scope.save = function() {
        var i;
        var teams = {};
        for (var team in $scope.teams) {
            var usernames = [];
            teams[team] = [];
            for (i = 0; i < $scope.teams[team].length; i++) {
                teams[team].push($scope.teams[team][i].username);
            }
        }
        for (i = 0; i < $scope.individuals.length; i++) {
            teams[$scope.individuals[i].display] =
                    [$scope.individuals[i].username];
        }
        console.log(teams);
        var fd = new FormData();
        fd.append('teams', angular.toJson(teams));
        $http({
            method: 'PUT',
            url: '/api/competitions/' + $routeParams.cid + '/teams',
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            console.log('success');
        }, function(response) {
            console.error(response);
        });
    };

    var socket = io.connect('http://' + $window.location.host + '/register');
    socket.on('connect', function() {});
    socket.on('new_user', function(event) {
        console.log('got new user', event);
        if ($routeParams.cid == event.cid) {
            $scope.individuals.push(event.user);

            if (!$scope.$$phase) {
                $scope.$digest();
            }
        }
    });
}]);
