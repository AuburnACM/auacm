app.controller('ProblemsController', ['$scope', '$http', function($scope, $http) {
    $scope.username = 'placeholder'
    $http.get('/api/me')
        .then(function(response) {
            $scope.username = response.data.data.displayName;
        },
        function(error) {
            
        })
    $http.get('/api/problems')
        .then(function(response) {
            $scope.problems = response.data.data;
        },
        function(error) {
            
        });
}]);
