app.controller('EditProblemController', ['$scope', '$route', '$http',
        '$routeParams', function($scope, $route, $http, $routeParams) {
    var pid = $routeParams.pid;
    $scope.oneCase = true;

    $http.get('/api/problems/' + pid)
        .then(function(response) {
            $scope.current_prob = response.data.data;
            if ($scope.current_prob.sample_cases.length === 0) {
                $scope.current_prob.sample_cases.push({input:'', output:''});
            } else {
                $scope.oneCase = false;
            }
        }, function(error) {
            console.log(error.data.status + ' ' + error.data.error);
        }
    );


    $scope.addCase = function() {
        $scope.current_prob.sample_cases.push({input:'', output:''});
        $scope.oneCase = false;
    };

    $scope.deleteCase = function() {
        $scope.current_prob.sample_cases.splice($scope.current_prob.sample_cases.length-1);
        if ($scope.current_prob.sample_cases.length === 1) {
            $scope.oneCase = true;
        }
    };

    $scope.save = function() {
        // Send the form data to the api
        var fd = new FormData();
        var prob = $scope.current_prob;
        if (typeof prob.name !== 'undefined')
            fd.append('name', prob.name);
        if (typeof prob.description !== 'undefined')
            fd.append('description', prob.description);
        if (typeof prob.input_desc !== 'undefined')
            fd.append('input_desc', prob.input_desc);
        if (typeof prob.output_desc !== 'undefined')
            fd.append('input_desc', prob.output_desc);
        if (prob.cases.input !== 0 && prob.cases.output !== 0)
            fd.append('cases', angular.toJson($scope.cases));
        if (prob.difficulty >= 0 && prob.difficulty <= 100)
            fd.append('difficulty', prob.difficulty);
        if (typeof $scope.inFile !== 'undefined')
            fd.append('in_file', $scope.inFile);
        if (typeof $scope.outFile !== 'undefined')
            fd.append('out_file', $scope.outFile);
        if (typeof $scope.solFile !== 'undefined')
            fd.append('sol_file', $scope.outFile);

        $http({
            method: 'POST',
            url: 'api/problems/edit' + pid,
            headers: {'Content-type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            // TODO(brandonlmorris) - clear the form
            // TODO(brandonlmorris) - should update the global problems list
            console.log('Problem updated');
        }, function(error) {
            console.log('Error updating problem');
            console.log(error.data.status + '+ ' + error.data.error);
        });
    };
}]);
