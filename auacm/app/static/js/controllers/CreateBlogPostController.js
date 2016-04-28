app.controller('CreateBlogPostController', ['$scope', '$http', '$location', 
        '$routeParams', function($scope, $http, $location, $routeParams) {
    $scope.disableForm = false;
    $scope.editing = $location.path().endsWith('/edit');

    $scope.createPost = function() {
        $scope.disableForm = true;
        var request = {
            'title' : $scope.title,
            'subtitle' : $scope.subtitle,
            'body' : $scope.$parent.body
        };
        if ($scope.editing) {
            request.bid = $routeParams.id;
        }
        $http({
            method: $scope.editing ? 'PUT' : 'POST',
            url: '/api/blog' + ($scope.editing ? '/' + $routeParams.id : ''),
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
        }, function(error) {
            $scope.disableForm = false;
            console.log(error);
        });
    }

    // If editing, prepopulate the fields
    if ($scope.editing) {
        $http.get('/api/blog/' + $routeParams.id).then(function(response) {
            // Fill in the fields
            var post = response.data.data;
            $scope.title = post.title;
            $scope.subtitle = post.subtitle;
            $scope.$parent.body = post.body;
        }, function(error) {
            // TODO: Redirect to 404 page
            console.log(error.data.status + ' ' + error.data.error);
        })
    }
}]);
