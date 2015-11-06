app.controller('CompetitionsController', ['$scope', '$http', '$location', 
        function($scope, $http, $location) {
    $scope.createCompetition = function() {
        $location.path('/competitions/create')
    }
    $http.get('/api/competitions')
        .then(function(response) {
            $scope.competitions = response.data.data;
        },
        function(error) {
            
        });
}]);