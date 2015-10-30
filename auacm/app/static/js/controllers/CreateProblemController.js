app.controller('CreateProblemController', ['$scope', '$http', '$route', 
    function($scope, $http, $route) {
  $scope.cases = [{input:"", output:""}];

  // Dynamically add new test cases (and form fields)
  $scope.addCase = function() {
    $scope.cases.push({input:"", output:""});
  }

  $scope.createProblem = function() {
    // TODO(brandonlmorris): turn the data to JSON and send it to the API
  }

}]);