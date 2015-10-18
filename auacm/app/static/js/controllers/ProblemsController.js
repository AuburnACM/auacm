app.controller('ProblemsController', ['$scope', '$filter', function($scope, $filter) {
    var orderBy = $filter('orderBy');
    $scope.order = function(predicate) {
        $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
        $scope.predicate = predicate;
        $scope.problems = orderBy($scope.problems, predicate, $scope.reverse);
    };
}]);
