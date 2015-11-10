app.controller('CreateProblemController', ['$scope', '$http', '$route', 
        function($scope, $http, $route) {
    $scope.cases = [{input:"", output:""}];

    // Dynamically add new test cases (and form fields)
    $scope.addCase = function() {
        $scope.cases.push({input:"", output:""});
    };

    $scope.createProblem = function() {
        // TODO(brandonlmorris): turn the data to JSON and send it to the API
        // Should stop request if any parts of the form are empty
        if ($scope.title === undefined || $scope.description === undefined ||
                $scope.inputDescription === undefined || 
                $scope.outputDescription === undefined ||
                $scope.cases.length === 0) {
            console.log("Error, the form must be completely filled out");
            // return;
        }

        console.log("This is the cases: " + $scope.cases);
        console.log($scope.cases);
        var fd = new FormData();
        fd.append('title', $scope.title);
        fd.append('description', $scope.description);
        fd.append('input_description', $scope.inputDescription);
        fd.append('output_description', $scope.outputDescription);
        fd.append('cases', angular.toJson($scope.cases));

        $http({
            method: 'POST',
            url: '/api/problems/create',
            headers: {'Content-type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            // TODO(brandonlmorris) - clear the form
            console.log('I think it might have worked');
        }, function(response) {
            console.log('Error uploading new problem');
            console.log(response.data.status + ": " + response.data.error);
        });
    };

}]);
