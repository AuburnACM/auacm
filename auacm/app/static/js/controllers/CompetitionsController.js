app.controller('CompetitionsController', ['$scope', '$http', function($scope, $http) {
    $http.get('/api/competitions')
        .then(function(response) {
            $scope.competitions = response.data.data;
        },
        function(error) {
            
        });
}]);