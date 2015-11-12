app.controller('ViewProblemController', ['$scope', '$route', '$routeParams', '$http',
        function($scope, $route, $routeParams, $http) {
    var pid = $routeParams.pid;

    $http.get('/api/problems/' + pid)
        .then(function(response) {
            $scope.current_prob = response.data.data;
            console.log($scope.current_prob);
        }, function(error) {
            console.log(error.data.status + ": " + error.data.error);
        }
    );
}]);