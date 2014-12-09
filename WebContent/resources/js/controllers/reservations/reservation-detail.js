var ReservationDetailController = function($scope, $state, $stateParams, $filter, $timeout, Reservations, Branches, TimezoneConverter, ngTableParams, ngProgress) {
	console.log($stateParams.branchId);
	console.log($stateParams.groupId);

	$scope.startProgress();
	$scope.$parent.branchResolved.promise.then(function(b) {
		Reservations.query({branchId: $stateParams.branchId, groupId: $stateParams.groupId}, function(d, h) {
			$scope.reservations = d;
			_.each(d, function(dd) {
	    		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);
	    		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.selectedBranch.timezone);

	    		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
	    		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.selectedBranch.timezone);
			});
			console.log(d);
			$scope.endProgress();
		}, function(d, h) {
			console.log(d);
			$scope.endProgress();
		});
	}, function() {
		$scope.endProgress();
	});

	$scope.setSelectedBranch($stateParams.branchId);
};

angular.module('adminReservationManagementApp').controller('ReservationDetailController',
		['$scope', '$state', '$stateParams', '$filter',  '$timeout',
		 'Reservations', 'Branches', 'TimezoneConverter', 'ngTableParams', 'ngProgress', ReservationDetailController]);
