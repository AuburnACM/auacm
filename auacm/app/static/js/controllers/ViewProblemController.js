app.controller('ViewProblemController', ['$scope', '$route', '$routeParams',
        function($scope, $route, $routeParams) {
    // Is this even necessary? Yes. Yes it is.
    var pid = $routeParams.pid;

    $scope.current_prob = null;
    // console.log($scope.problems);
    for (var i = 0; i < $scope.problems.length; i++) {
        if ($scope.problems[i].pid == pid) {
            $scope.current_prob = $scope.problems[i];
            break;
        }
    }

    $scope.title = $scope.current_prob.name;
    console.log($scope.current_prob.description);
}]);