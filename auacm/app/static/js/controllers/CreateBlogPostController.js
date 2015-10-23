app.controller('CreateBlogPostController', ['$scope', '$http', '$location', 
        function($scope, $http, $location) {
    $scope.disableForm = false;
    $scope.createPost = function() {
        $scope.disableForm = true;
        var request = {
            'title' : $scope.title,
            'subtitle' : $scope.subtitle,
            'body' : $scope.$parent.body
        };
        $http({
            method: 'POST',
            url: '/api/blog/',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
            },
            data: request
        }).then(function(response) {
            $scope.disableForm = false;
            $scope.$parent.body = '';
            $scope.blogPosts.unshift(response.data.data);
            $location.path('/blog/' + response.data.data.id);
        }, function(response) {
            $scope.disableForm = false;
            console.log("error");
        });
    }
}]);
