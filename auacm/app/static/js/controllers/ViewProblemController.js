app.controller('ViewProblemController', ['$scope', '$route', '$routeParams', '$http',
        function($scope, $route, $routeParams, $http) {
    $scope.pid = $routeParams.pid;

    $http.get('/api/problems/' + $scope.pid)
        .then(function(response) {
            $scope.current_prob = response.data.data;
        }, function(error) {
            console.log(error.data.status + ': ' + error.data.error);
        }
    );
}]);
