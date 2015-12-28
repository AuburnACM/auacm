app.controller('CompetitionsController', ['$scope', '$http', '$location',
        function($scope, $http, $location) {
    $scope.createCompetition = function() {
        $location.path('/competitions/create');
    };

    $scope.getRemainingTime = function(competition) {
        return competition.startTime + competition.length - (Date.now() / 1000);
    };

    $http.get('/api/competitions')
        .then(function(response) {
            $scope.competitions = response.data.data;
        },
        function(error) {

        });
}]);
