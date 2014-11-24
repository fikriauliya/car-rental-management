myApp = angular.module('myApp');
myApp.controller('ReviewUserController', ['$scope', '$location', '$timeout', 'Users', 'PeerReviews', 'ngTableParams', '$filter', 'ngProgress',
  function($scope, $location, $timeout, Users, PeerReviews, ngTableParams, $filter, ngProgress) {
	$scope.userId = $location.search().userId;
	$scope.newReview = new PeerReviews();
	$scope.newReview.point = 1;
	$scope.groupedReviews = {};
	$scope.years = [];
	$scope.averagePoint = 0;

	$scope.clearNotification = function() {
		$scope.errors = [];
		$scope.info = "";
	};

	$scope.refreshReviews = function() {
		if (isAdmin || isHR) {
			ngProgress.start();
			PeerReviews.query({userId: $scope.userId}, function(data, header) {
				$scope.clearNotification();

				$scope.groupedReviews = _.groupBy(data, function(d) {
					var date = new Date(d.timestamp);
					var year = date.getFullYear();
					return year;
				});
				$scope.years = _.keys($scope.groupedReviews);
				if ($scope.years.length > 0)
					$scope.selectedYear = $scope.years[$scope.years.length - 1];

				$scope.tableParams.reload();
				ngProgress.complete();
			}, function(data, header) {
				$scope.clearNotification();
				$scope.errors = data.data;

				ngProgress.complete();
			});
		}
	};

	$scope.createReview = function() {
		ngProgress.start();
		$scope.newReview.to = $location.search().userId;
		$scope.newReview.$save({}, function(data, header) {
			$scope.clearNotification();

			$scope.info = "Thank you for your review";
			$scope.newReview = new PeerReviews();
			$scope.newReview.point = 1;
			$scope.refreshReviews();
			ngProgress.complete();
		}, function(data, header) {
			$scope.clearNotification();

			$scope.errors = data.data;
			ngProgress.complete();
		});
	};

	$scope.tableParams = new ngTableParams({
		    page: 1, count: 10,
		    sorting: {
		        name: 'desc'
		    }
		}, {
	    total: 0,
	    getData: function($defer, params) {
	    	if ($scope.selectedYear) {
		    	var reviews = $scope.groupedReviews[$scope.selectedYear];
		        var orderedData = params.sorting() ? $filter('orderBy')(reviews, params.orderBy()) : reviews;

		        if (reviews.length == 0) {
		        	$scope.averagePoint = 0;
		        } else {
		        	var sum = _.reduce(reviews, function(memo, item) {
		        		return memo + item.point;
		        	}, 0);
		        	var size = reviews.length;
		        	$scope.averagePoint = sum/size;
		        }

	            params.total(orderedData.length);
	            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	    	}
        }
    });

	$scope.refreshReviews();
  }]
);