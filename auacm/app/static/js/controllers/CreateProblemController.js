app.controller('CreateProblemController', ['$scope', '$http', '$route', 
    function($scope, $http, $route) {
  $scope.cases = [{input:"", output:""}];

  // Dynamically add new test cases (and form fields)
  $scope.addCase = function() {
    $scope.cases.push({input:"", output:""});
  }

  $scope.createProblem = function() {
    // TODO(brandonlmorris): turn the data to JSON and send it to the API
    var fd = new FormData();
    fd.append('title', $scope.title);
    fd.append('description', $scope.description);
    fd.append('input_description', $scope.inputDescription);
    fd.append('output_description', $scope.outputDescription);
    fd.append('cases', $scope.cases);

    $http({
      method: 'POST',
      url: '/api/problems',
      headers: {'Content-type': undefined},
      transformRequest: angular.identity,
      data: fd
    }).then(function(response) {
      console.log('I think it might have worked');
    }, function(response) {
      console.log('Something went terribly wrong');
    });
  }

}]);