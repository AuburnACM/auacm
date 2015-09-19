app.controller('ProblemsController', ['$scope', '$http', function($scope, $http) {
    $http.get('/api/problems')
        .then(function(response) {
            $scope.problems = response.data.data;
        }, 
        function(error) {
            
        });
}]);
