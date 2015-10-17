app.controller('MainController', ['$scope', '$http', '$route', function($scope, $http, $route) {
    $scope.username = 'placeholder'
    $scope.$route = $route
    $http.get('/api/problems')
        .then(function(response) {
            $scope.problems = response.data.data;
        },
        function(error) {
            
        });
}]);
