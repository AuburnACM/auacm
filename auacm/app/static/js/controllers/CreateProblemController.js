// TODO(brandonlmorris): Ditch this. Merge it with EditProblemController
app.controller('CreateProblemController', ['$scope', '$http', '$route', '$window',
        function($scope, $http, $route, $window) {
    $scope.cases = [{input:"", output:""}];
    $scope.oneCase = true;
    $scope.success = false;

    // Dynamically add new test cases (and form fields)
    $scope.addCase = function() {
        var len = $scope.cases.length;
        if (!($scope.cases[len-1].input.length === 0 &&
              $scope.cases[len-1].output.length === 0)) {
            $scope.cases.push({input:"", output:""});
            $scope.oneCase = false;
        }
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
            // Redirect to the new problem page
            // NOTE: Current problem list in client is invalid now
            $scope.success = true;
            // response.data.pid
            $window.location.href = 'http://' + $window.location.host +
                '/#/problems/' + response.data.data.pid;
        }, function(response) {
            console.log('Error uploading new problem');
            console.log(response);
            console.log(response.data.status + ': ' + response.data.error);
        });
    };

}]);
