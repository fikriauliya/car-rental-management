myApp = angular.module('myApp');
myApp.config(function($locationProvider) { $locationProvider.html5Mode(true); });
myApp.controller('ReviewUserController', ['$scope', '$location', '$timeout', 'Users', 'PeerReviews',
  function($scope, $location, $timeout, Users, PeerReviews) {
	$scope.userId = $location.search().userId;
	$scope.newReview = new PeerReviews();

	$scope.refreshReviews = function() {
		PeerReviews.query({userId: $scope.userId}, function(data, header) {
			$scope.reviews = data;
			console.log(data);
		});
	}

	$scope.createReview = function() {
		$scope.newReview.to = $location.search().userId;
		$scope.newReview.$save({}, function(data, header) {
			console.log("Saved");
			$scope.refreshReviews();

		}, function(data, header) {
			console.log("Failed");
		});
	};

	$scope.refreshReviews();
  }]
);