app.controller('JudgeController', ['$scope', '$http', '$websocket', 
        function($scope, $http, $websocket) {
    $scope.submit = function() {
        var fd = new FormData();
        fd.append('pid', $scope.pid);
        fd.append('file', $scope.file);
        $http({
            method: 'POST',
            url: '/api/submit',
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
                console.log("success");
        }, function(response) {
                console.log("error");
        });
    };
    
    var ws = $websocket.$new('ws://localhost:5000/test');
    ws.$on('$open', function() {
        console.log('websocket is open');
    });
}]);
