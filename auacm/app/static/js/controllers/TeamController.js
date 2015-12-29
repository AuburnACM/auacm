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

    var socket = io.connect('http://' + $window.location.host + '/register');
    socket.on('connect', function() {
        console.log('Connected');
    });
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
