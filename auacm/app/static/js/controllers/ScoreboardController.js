app.controller('ScoreboardController', ['$scope', '$http', '$routeParams',
        '$window', '$interval',
        function($scope, $http, $routeParams, $window, $interval) {
    // Store competition ID
    $scope.cid = $routeParams.cid;
    // Initialize time left in competition to 0
    $scope.timeLeft = 0;
    $scope.active = false;
    $scope.ended = true;
    $scope.timeUntil = 0;
    var offset = 0;

    $scope.socket.on('system_time', function(event) {
        // The server is *ahead* of the client by offset milliseconds
        offset = -Date.now() + event.milliseconds;
    });

    var genScoreboard = function() {
        var team, i;
        for (i = 0; i < $scope.teams.length; i++) {
            // Compute how many problems each team has solved and their time
            team = $scope.teams[i];
            var solved = 0;
            var time = 0;
            for (var problemName in team.problemData) {
                if (team.problemData[problemName].status === 'correct') {
                    solved++;
                    time += team.problemData[problemName].problemTime;
                }
            }
            team.solved = solved;
            team.time = time;
        }

        // Sort each team based on their solved numbers then total time
        $scope.teams.sort(function(a, b) {
            if (a.solved != b.solved) {
                return b.solved - a.solved;
            } else {
                return a.time - b.time;
            }
        });

        if ($scope.teams.length > 0) {
            // Calculate rank for each team.
            var rank = 1;
            var prevSolved = $scope.teams[0].solved;
            var prevTime = $scope.teams[0].solved;
            $scope.teams[0].rank = rank;
            for (i = 1; i < $scope.teams.length; i++) {
                team = $scope.teams[i];
                if (team.solved < prevSolved) {
                    rank++;
                    team.rank = rank;
                } else if (team.solved == prevSolved && team.time > prevTime) {
                    rank++;
                    team.rank = rank;
                } else {
                    team.rank = rank;
                }
                prevSolved = team.solved;
                prevTime = team.time;
            }
        }

        // Can break if an $apply is already in progress, need to check first (???)
        if (!$scope.$$phase) {
            $scope.$digest();
        }
    };

    var problemIsInComp = function(problemId) {
        for (var problem in $scope.compProblems) {
            if (problemId === $scope.compProblems[problem].pid) {
                return true;
            }
        }
        return false;
    };

    var connectToSocket = function() {
        // Perform live updates to the scoreboard
        var viewed = [];
        $scope.socket.on('status', function(event) {
            if (viewed.indexOf(event.submissionId) > -1 ||
                    event.status == 'running' ||
                    !problemIsInComp(event.problemId) ||
                    event.submitTime > $scope.competition.startTime +
                        $scope.competition.length) {
                // The scoreboard ignores the problem for any of the following
                // reasons:
                // The submission has already been handled,
                // The event's status is running,
                // The competition does not contain this problem, or
                // The problem was acceptedafter the contest was over.
                return;
            }
            viewed.push(event.submissionId); // note that we've seen this
            for (var i = 0; i < $scope.teams.length; i++) {
                if ($scope.teams[i].users.indexOf(event.username) != -1) {
                    // If the user that submitted the problem was on this team
                    var problem = $scope.teams[i].problemData[event.problemId];
                    problem.submitCount++;
                    if (problem.status !== 'correct') {
                        if (event.status === 'correct') {
                            problem.problemTime = Math.floor((event.submitTime -
                                $scope.competition.startTime) / 60) +
                                (problem.submitCount - 1) * 20;
                            problem.status = 'correct';
                        } else {
                            problem.status = 'incorrect';
                        }
                    }
                }
            }
            // Publish these changes to the scoreboard
            genScoreboard();
        });
    };


    // Generate the scoreboard on load
    $http.get('/api/competitions/' + $scope.cid)
        .then(function(response) {
            $scope.competition = response.data.data.competition;
            $scope.compProblems = response.data.data.compProblems;
            $scope.problemNames = Object.keys($scope.compProblems).sort();
            $scope.teams = response.data.data.teams;
            genScoreboard();
            connectToSocket();

            var clientTime = Math.floor((Date.now() + offset) / 1000);
            if (clientTime < $scope.competition.startTime +
                    $scope.competition.length) {
                // only start the timer if the competition is still going or
                // it hasn't yet started
                $scope.ended = false;
                $scope.active = false;
                var timer = $interval(function() {

                    var timeToEnd = $scope.competition.startTime +
                            $scope.competition.length - clientTime;
                    // Compute the remaining time as the minimum of the time
                    // until the compeition is over and the length of the
                    // competition.
                    $scope.timeLeft = Math.min(timeToEnd,
                            $scope.competition.length);

                    if ($scope.timeLeft < $scope.competition.length) {
                        $scope.active = true;
                    } else {
                        $scope.timeUntil = $scope.competition.startTime -
                            clientTime;
                    }

                    clientTime = Math.floor((Date.now() + offset) / 1000);
                    if ($scope.timeLeft <= 0) {
                        $scope.active = false;
                        $scope.ended = true;
                        $scope.timeLeft = 0;
                        $interval.cancel(timer);
                    }
                }, 1000);
            } else {
                $scope.ended = true;
            }

        },
        function(error) {

        });
}]);
