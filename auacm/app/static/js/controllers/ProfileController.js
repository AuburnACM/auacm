app.controller('ProfileController', ['$scope', '$routeParams', '$http',
    function($scope, $routeParams, $http) {

    $scope.username = $routeParams.username;
    $scope.display = false;
    $scope.has_activity = false;

    var getProfile = function() {
        $http.get('/api/profile/' + $scope.username)
            .then(function(response) {
                data = response.data.data;
                $scope.display_name = data.display;
                $scope.problems_solved = data.problems_solved;
                $scope.recent_attempts = data.recent_attempts;
                $scope.recent_competitions = data.recent_competitions;
                $scope.recent_blog_posts = data.recent_blog_posts;
                $scope.has_activity =   data.recent_attempts.length > 0 ||
                                        data.recent_competitions.length > 0 ||
                                        data.recent_blog_posts.length > 0; 
                $scope.display = true;
            },
            function (error) {
                console.error(error);
            })
    };
    getProfile();

}]);
