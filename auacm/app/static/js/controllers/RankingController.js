app.controller('RankingController', ['$scope', '$route', '$http',
    function($scope, $route, $http) {
    $scope.display = false;
    $scope.ranks = [];
    $scope.timeFrame = 'all';

    // API call to get the ranking of the users
    var getRanking = function() {
        $http.get('/api/ranking')
            .then(function(response) {
                $scope.ranks = response.data.data;
                $scope.display = true;
            },
            function (error) {
                console.error(error);
            })
    };
    getRanking();

    // Get a filtered ranking when the user selects a time frame
    $scope.onTimeFrameChange = function() {
        $http.get('/api/ranking/' + $scope.timeFrame)
            .then(function(response) {
                $scope.ranks = response.data.data;
            },
            function (error) {
                console.error(error);
            })
    };
}]);
