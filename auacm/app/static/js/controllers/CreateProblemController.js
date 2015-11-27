app.controller('CreateProblemController', ['$scope', '$http', '$route',
        function($scope, $http, $route) {
    $scope.cases = [{input:"", output:""}];
    $scope.oneCase = true;
    $scope.success = false;

    // Dynamically add new test cases (and form fields)
    $scope.addCase = function() {
        $scope.cases.push({input:"", output:""});
        $scope.oneCase = false;
    };

    $scope.deleteCase = function() {
        $scope.cases.splice($scope.cases.length-1);
        if ($scope.cases.length === 1) {
            $scope.oneCase = true;
        }
    };

    $scope.createProblem = function() {
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
        if ($scope.cases[0].input.length !== 0 && $scope.cases[0].output.length !== 0)
            fd.append('cases', angular.toJson($scope.cases));
        if ($scope.difficulty <= 100 && $scope.difficulty >= 0)
            fd.append('difficulty', $scope.difficulty);
        if (typeof $scope.appearedIn !== 'undefined')
            fd.append('appeared_in', $scope.appearedIn);
        if (typeof $scope.inFile !== 'undefined')
            fd.append('in_file', $scope.inFile);
        if (typeof $scope.outFile !== 'undefined')
            fd.append('out_file', $scope.outFile);
        if (typeof $scope.solFile !== 'undefined')
            fd.append('sol_file', $scope.solFile);

        $http({
            method: 'POST',
            url: '/api/problems/',
            headers: {'Content-type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            // TODO(brandonlmorris) - clear the form
            // TODO(brandonlmorris) - should update the global problems list
            $scope.success = true;
        }, function(response) {
            console.log('Error uploading new problem');
            console.log(response);
            console.log(response.data.status + ': ' + response.data.error);
        });
    };

}]);
