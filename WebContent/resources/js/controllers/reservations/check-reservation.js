reservationManagementApp = angular.module('reservationManagementApp');
reservationManagementApp.controller('CheckReservationController', ['$scope', '$timeout', 'Reservations', 'TimezoneConverter', 'ngProgress', 'ngTableParams', '$filter',
  function($scope, $timeout, Reservations, TimezoneConverter, ngProgress, ngTableParams, $filter) {
	$scope.errors = [];
	$scope.info = "";
	$scope.progress = 0;
	$scope.detailedReservations = [];

	$scope.refreshReservations = function() {
		$scope.startProgress();
		Reservations.query({}, function(d, h){
			_.each(d, function(dd) {
        		dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, dd.inventory.owner.timezone);
        		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, dd.inventory.owner.timezone);

        		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, dd.inventory.owner.timezone);
        		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, dd.inventory.owner.timezone);
    		});

        	$scope.detailedReservations = d;
        	$scope.tableParams.reload();

			$scope.endProgress();
		}, function(d, h) {
			$scope.endProgress();
		});
	};

	$scope.totalPrice = function(reservations) {
		var a = _.reduce(reservations, function(memo, item){ return memo + item.inventoryFee + item.overdueFee; }, 0);
		if (reservations.length > 0 && reservations[0].assignedDriver) { return a + reservations[0].driverFee; }
		return a;
	}

	$scope.tableParams = new ngTableParams(
	{
	    page: 1, count: 10,
	    sorting: {
	        start: 'asc'
	    }
	},
	{
	    total: 0,
	    groupBy: 'groupId',
	    getData: function($defer, params) {
	    	var filteredData = $filter('filter')($scope.detailedReservations, $scope.filterText);
	        var orderedData = params.sorting() ? $filter('orderBy')(filteredData, params.orderBy()) : filteredData;

	        params.total(orderedData.length);
	        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	    }
	});

	$scope.startProgress = function() {
		if ($scope.progress == 0) ngProgress.start();
		$scope.progress++;
	};

	$scope.endProgress = function() {
		$scope.progress--;
		if ($scope.progress == 0) ngProgress.complete();
	};

	$scope.cancelRental = function(branchId, groupId) {
		Reservations.update({branchId: branchId, groupId: groupId, operation: "cancelRental"}, function(d, h){
			$scope.refreshReservations();
		}, function(d, h) {
			$scope.refreshReservations();
			alert(d.data);
		});
	};

	$scope.refreshReservations();
}]);