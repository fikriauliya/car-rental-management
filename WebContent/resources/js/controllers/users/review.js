myApp = angular.module('myApp');
myApp.controller('ReviewUserController', ['$scope', '$location', '$timeout', 'Users', 'PeerReviews', 'ngTableParams', '$filter',
  function($scope, $location, $timeout, Users, PeerReviews, ngTableParams, $filter) {
	$scope.userId = $location.search().userId;
	$scope.newReview = new PeerReviews();
	$scope.newReview.point = 1;
	$scope.groupedReviews = {};
	$scope.years = [];
	$scope.averagePoint = 0;

	$scope.refreshReviews = function() {
		PeerReviews.query({userId: $scope.userId}, function(data, header) {
			$scope.groupedReviews = _.groupBy(data, function(d) {
				var date = new Date(d.timestamp);
				var year = date.getFullYear();
				return year;
			});
			$scope.years = _.keys($scope.groupedReviews);
			if ($scope.years.length > 0)
				$scope.selectedYear = $scope.years[$scope.years.length - 1];

			$scope.tableParams.reload();
		});
	};

	$scope.createReview = function() {
		$scope.newReview.to = $location.search().userId;
		$scope.newReview.$save({}, function(data, header) {
			$scope.info = "Thank you for your review";
			$scope.errors = [];
			$scope.newReview = new PeerReviews();
			$scope.newReview.point = 1;
			$scope.refreshReviews();

		}, function(data, header) {
			$scope.info = "";
			$scope.errors = data.data;
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