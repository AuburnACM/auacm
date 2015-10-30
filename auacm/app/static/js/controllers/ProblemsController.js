app.controller('ProblemsController', ['$scope', '$filter', '$window', 
        function($scope, $filter, $window) {
    var orderBy = $filter('orderBy');
    $scope.order = function(predicate) {
        $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
        $scope.predicate = predicate;
        $scope.problems = orderBy($scope.problems, predicate, $scope.reverse);
    };
    
    $scope.integerOrder = function(predicate) {
        $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
        $scope.predicate = predicate;
        $scope.problems.sort(function(a, b) {
            return $scope.reverse ? 
                    parseInt(a.difficulty) - parseInt(b.difficulty) : 
                    parseInt(b.difficulty) - parseInt(a.difficulty);
        });
    }

    $scope.createProblem = function() {
        $window.location.href = 'http://' + $window.location.host + '/#/problems/create';
    }
}]);
