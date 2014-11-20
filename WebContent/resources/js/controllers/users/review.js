myApp = angular.module('myApp');
myApp.config(function($locationProvider) { $locationProvider.html5Mode(true); });
myApp.controller('ReviewUserController', ['$scope', '$location', '$timeout', 'Users', 'PeerReviews', 'ngTableParams', '$filter',
  function($scope, $location, $timeout, Users, PeerReviews, ngTableParams, $filter) {
	$scope.userId = $location.search().userId;
	$scope.newReview = new PeerReviews();
	$scope.reviews = [];

	$scope.refreshReviews = function() {
		PeerReviews.query({userId: $scope.userId}, function(data, header) {
			$scope.reviews = data;
			$scope.tableParams.reload();
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

	 $scope.tableParams = new ngTableParams({
		    page: 1, count: 10,
		    sorting: {
		        name: 'asc'     // initial sorting
		    }
		}, {
	    total: 0,
	    getData: function($defer, params) {
	        var orderedData = params.sorting() ? $filter('orderBy')($scope.reviews, params.orderBy()) : $scope.reviews;
            params.total(orderedData.length);
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

	$scope.refreshReviews();
  }]
);