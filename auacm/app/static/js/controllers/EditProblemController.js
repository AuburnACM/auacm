app.controller('EditProblemController', ['$scope', '$route', '$http',
        '$routeParams', '$window',
    function($scope, $route, $http, $routeParams, $window) {
    var pid = $routeParams.pid;
    $scope.isCreate = typeof pid === 'undefined';
    $scope.oneCase = true;


    if (!$scope.isCreate) {
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
    } else {
        $scope.current_prob = {};
        $scope.current_prob.sample_cases = [{input:'', output:''}];
    }

    $scope.addCase = function() {
        var len = $scope.current_prob.sample_cases.length;
        if (!($scope.current_prob.sample_cases[len-1].input.length === 0 &&
              $scope.current_prob.sample_cases[len-1].output.length === 0)) {
            $scope.current_prob.sample_cases.push({input:'', output:''});
            $scope.oneCase = false;
        }
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
        fd.append('pid', $scope.current_prob.pid);
        if (typeof prob.name !== 'undefined')
            fd.append('name', prob.name);
        if (typeof prob.description !== 'undefined')
            fd.append('description', prob.description);
        if (typeof prob.input_desc !== 'undefined')
            fd.append('input_desc', prob.input_desc);
        if (typeof prob.output_desc !== 'undefined')
            fd.append('output_desc', prob.output_desc);
        if (prob.sample_cases[0].input.length !== 0 && prob.sample_cases[0].output.length !== 0)
            fd.append('cases', angular.toJson(prob.sample_cases));
        if (prob.difficulty >= 0 && prob.difficulty <= 100)
            fd.append('difficulty', prob.difficulty);
        if (typeof $scope.inFile !== 'undefined')
            fd.append('in_file', $scope.inFile);
        if (typeof $scope.outFile !== 'undefined')
            fd.append('out_file', $scope.outFile);
        if (typeof $scope.solFile !== 'undefined')
            fd.append('sol_file', $scope.solFile);
        if (typeof prob.appeared !== 'undefined')
            fd.append('appeared_in', prob.appeared);

        $http({
            method: ($scope.isCreate ? 'POST' : 'PUT'),
            url: 'api/problems/' + ($scope.isCreate ? '' : $scope.current_prob.pid),
            headers: {'Content-type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            // Redirect to the new problem page
            // NOTE: Global problem list in client may be invalid now
            $window.location.href = 'http://' + $window.location.host +
                '/#/problems/' + response.data.data.pid;
        }, function(error) {
            console.log('Error updating problem');
            console.log(response);
            console.log(error.data.status + ': ' + error.data.error);
        });
    };
}]);
