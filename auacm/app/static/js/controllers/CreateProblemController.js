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

        // Add defined elements into the formdata header
        var fd = new FormData();
        if (typeof $scope.title != 'undefined')
            fd.append('title', $scope.title);
        if (typeof $scope.description != 'undefined')
            fd.append('description', $scope.description);
        if (typeof $scope.inputDescription != 'undefined')
            fd.append('input_description', $scope.inputDescription);
        if (typeof $scope.outputDescription != 'undefined')
            fd.append('output_description', $scope.outputDescription);
        // This should be validated much better
        if ($scope.cases[0].input.length !== 0 || $scope.cases[0].output.length !== 0)
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
